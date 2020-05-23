import com.databricks.spark.xml._
import org.apache.spark.sql
import org.apache.spark.sql.SparkSession
import java.util.Properties
import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.wikiclean.WikiClean
import collection.JavaConverters._

object WikipediaAnalyzer {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder.master("local").getOrCreate()

    //load xml dataframe
    val dataframe = spark.read
      .option("rowTag", "page")
      .xml("enwiki_small.xml")

    //Remove entries that only redirect to actual articles
    val noRedirects = dataframe.filter(row => row.getAs("redirect") == null)

    //Create a pair RDD of article titles to article text
    //noRedirects is of the type Dataframe. The corresponding RDD is required so it can be turned into a pair RDD using the .map call
    val keyValue = noRedirects.rdd.map(row =>
      (
        row.getAs("title").toString(), //key
        row.getAs("revision") //value
          .asInstanceOf[sql.Row]
          .getAs("text")
          .asInstanceOf[sql.Row]
          .getAs("_VALUE")
          .toString()
      )
    )

    //Convert the Wiki markup text to actual plaintext
    val plainText = keyValue.mapValues(Functions.toPlaintext)

    //Remove external links in square brackets
    val noLinks = plainText.mapValues(x => x.replaceAll("\\[.*?\\]", ""))

    //Change uppercase to lowercase letters and remove everything that is not a letter or whitespace
    val onlyLowercaseLetters = noLinks.mapValues(x => x.toLowerCase().replaceAll("[^a-z|\\s]", ""))

    //Split article into words (Actual lemmatisation need performance optimization)
    val lemmas = onlyLowercaseLetters.flatMapValues(Functions.toLemmas)

    //Remove unnecessary frequently used words
    val noStopwords = lemmas.filter(pair => !StopWords.list.contains(pair._2))

    noStopwords.lookup("Bacteriophage").foreach(println)

    try {
      spark.stop()
    } catch {
      case _: Throwable => () //Exceptions are expected here
    }
  }
}

object Functions {
  val wikiCleaner = new WikiClean.Builder().build
  val nlpProperties = new Properties()
  //nlpProperties.setProperty("annotators", "tokenize, ssplit, pos, lemma")
  nlpProperties.setProperty("annotators", "tokenize")
  val nlp = new StanfordCoreNLP(nlpProperties)

  /* Converts Wiki markup into plaintext */
  def toPlaintext(text: String): String = {
    //The xml tags had been removed as the file was loaded into a dataframe,
    //but are expected by the library
    return wikiCleaner.clean("<text xml:space=\"preserve\">" + text + "</text>")
  }

  /* Currently only splits Strings into words. Actual lemmatisation takes a very long time.
  *  Further optimization is needed.
  */
  def toLemmas(text: String): List[String] = {

    val doc = new CoreDocument(text)
    nlp.annotate(doc)
/*
    val tokens = doc.tokens().asScala
    val lemmas = tokens.map(x => x.lemma()).toList
    return lemmas
*/
    return doc.tokens().asScala.toList.map(x => x.toString())
  }
}