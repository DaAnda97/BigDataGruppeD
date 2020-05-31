import com.databricks.spark.xml._
import org.apache.spark.sql
import org.apache.spark.sql.SparkSession
import java.util.Properties

import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.apache.spark.rdd.RDD
import org.wikiclean.WikiClean

import collection.JavaConverters._
import scala.reflect.ClassTag

object WikipediaAnalyzer {
  val spark = SparkSession.builder.master("local").getOrCreate()
  def main(args: Array[String]): Unit = {

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
    val onlyLowercaseLetters = noLinks.mapValues(x => x.toLowerCase().replaceAll("[^a-z\\s]", ""))

    //Split article into words (Actual lemmatisation need performance optimization)
    val lemmas = onlyLowercaseLetters.mapValues(Functions.toLemmas)

    //Remove unnecessary frequently used words
    val noStopwords = lemmas.mapValues(wordList => wordList.filterNot(StopWords.set).filter(_.length > 1))

    //-------------------------------------------------------------
    //4.1 Term frequency of each term with respect to each document
    //-------------------------------------------------------------
    val docFrequency = noStopwords.mapValues(wordList =>
      wordList.groupBy(identity).mapValues(_.size).toSeq.sortBy(_._2)(Ordering[Int].reverse)
    )

    docFrequency.lookup("Anarchism")(0).foreach(println)

    //-------------------------------------------------------------
    //4.2 Total frequency of each term
    //-------------------------------------------------------------
    val flattened = docFrequency.values.flatMap(identity)

    val totalFrequency = flattened.reduceByKey((a, b) => a+b)
      .sortBy(_._2, false)

    val mostFrequent = totalFrequency.keys.take(1000)

    val tf = totalFrequency.filter(pair => mostFrequent.contains(pair._1))

    //-------------------------------------------------------------
    //4.3 Inverse term frequency of each term
    //-------------------------------------------------------------
    val invTotalFrequency = tf.foreach(pair => 1.0/pair._2)

    //-------------------------------------------------------------
    //4.4 TF-IDF score of each term with respect to each document
    //-------------------------------------------------------------
    val numOfArticles = keyValue.count()
    val mappedToOne = noStopwords.mapValues(wordList => wordList.filter(mostFrequent.toSet))
    .mapValues(wordList =>
      wordList.toSet.map((word: String) => (word, 1))
    )

    //The number of documents in which a term appears
    val docOccurrences = mappedToOne.values.flatMap(identity).reduceByKey((a, b) => a+b)

    val idf = docOccurrences.mapValues(occurrences => scala.math.log(numOfArticles.toDouble/occurrences.toDouble))
    //The rdd needs to be collected to be used inside the subsequent lambda expression
    val idfLocal = idf.collect().toMap

    val tfIdf = docFrequency.mapValues(array =>
      array.filter(pair => mostFrequent.contains(pair._1))
        .map(pair => (pair._1, idfLocal(pair._1) * pair._2))
    )

    tfIdf.lookup("Anarchism")(0).sortBy(_._2).foreach(println)

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