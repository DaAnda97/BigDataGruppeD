import java.util.Properties

import com.databricks.spark.xml._
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.fields.TextField
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.SearchResponse
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties, RequestFailure, RequestSuccess}
import edu.stanford.nlp.pipeline.{CoreDocument, StanfordCoreNLP}
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.spark.api.java.function.VoidFunction
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql
import org.apache.spark.sql.SparkSession
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback
import org.wikiclean.WikiClean

import scala.collection.JavaConverters._


object WikipediaAnalyzer {
  val spark = SparkSession.builder.master("local").getOrCreate()

  def main(args: Array[String]): Unit = {
    //load xml dataframe
    val dataframe = spark.read
      .option("rowTag", "page")
      .option("nullValue", "")
      .xml("enwiki_small.xml")

    //Remove entries that only redirect to actual articles
    val noRedirects = dataframe.filter(row => row.getAs("redirect") == null)

    //Create a pair RDD of article titles to article text
    //noRedirects is of the type Dataframe. The corresponding RDD is required so it can be turned into a pair RDD using the .map call
    val articles = noRedirects.rdd.map(row =>
      (
        row.getAs("title").toString, //key
        new Article(
          row.getAs("title").toString,
          row.getAs("revision").asInstanceOf[sql.Row].getAs("text").asInstanceOf[sql.Row].getAs("_VALUE").toString,
          row.getAs("revision").asInstanceOf[sql.Row].getAs("text").asInstanceOf[sql.Row].getAs("_bytes").toString,
          row.getAs("revision").asInstanceOf[sql.Row].getAs("contributor").asInstanceOf[sql.Row].getAs("username"),
          row.getAs("revision").asInstanceOf[sql.Row].getAs("timestamp")
        ) //value
      )
    ).filter(title => !title._1.endsWith("(disambiguation)"))

    //Convert the Wiki markup text to actual plaintext
    val plainText = articles.mapValues(article => Functions.toPlaintext(article.title))

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
    var docFrequency = noStopwords.mapValues(wordList =>
      wordList.groupBy(identity).mapValues(_.size).toSeq.sortBy(_._2)(Ordering[Int].reverse)
    )

    //-------------------------------------------------------------
    //4.2 Total frequency of each term
    //-------------------------------------------------------------
    val flattened = docFrequency.values.flatMap(identity)

    val totalFrequency = flattened.reduceByKey((a, b) => a + b)
      .sortBy(_._2, false)

    //Number of most frequent words to be analyzed
    //For n>=100 Spark changes the way the SVD is calculated resulting in much longer runtime
    val n = totalFrequency.collect.size
    val mostFrequent = totalFrequency.keys.take(n)

    val tf = totalFrequency.filter(pair => mostFrequent.contains(pair._1))
    docFrequency = docFrequency.mapValues(array => array.filter(pair => mostFrequent.contains(pair._1)))

    //-------------------------------------------------------------
    //4.3 Inverse document frequency of each term
    //-------------------------------------------------------------
    val numOfArticles = noStopwords.count()
    val mappedToOne = noStopwords.mapValues(wordList => wordList.filter(mostFrequent.toSet))
      .mapValues(wordList =>
        wordList.toSet.map((word: String) => (word, 1))
      )

    //The number of documents in which a term appears
    val docOccurrences = mappedToOne.values.flatMap(identity).reduceByKey((a, b) => a + b)

    val idf = docOccurrences.mapValues(occurrences => scala.math.log(numOfArticles.toDouble / occurrences.toDouble))

    //-------------------------------------------------------------
    //4.4 TF-IDF score of each term with respect to each document
    //-------------------------------------------------------------

    //The rdd needs to be collected to be used inside the subsequent lambda expression
    val idfLocal = idf.collect().toMap

    val tfIdf = docFrequency.mapValues(array =>
      array.map(pair => (pair._1, idfLocal(pair._1) * pair._2))
    )

    //-------------------------------------------------------------
    //5. SVD
    //-------------------------------------------------------------
    val wordToIndex = mostFrequent.zipWithIndex.toMap

    val tfIdfMaps = tfIdf.mapValues(array => array.toMap)

    val vectors = tfIdfMaps.mapValues(frequencyMap =>
      Vectors.dense(
        Array.range(0, wordToIndex.size)
          .map(index => frequencyMap.getOrElse(mostFrequent(index), 0.0))
      )
    ).cache()

    val docToIndex = vectors.keys.zipWithIndex().collectAsMap()

    val mat = new RowMatrix(vectors.values, numOfArticles, n)

    val svd = mat.computeSVD(20, computeU = true)
    val docConceptRelevance: RowMatrix = svd.U
    val conceptStrengths: Vector = svd.s
    val termConceptRelevance = svd.V

    //-------------------------------------------------------------
    //6.1 Top concepts
    //-------------------------------------------------------------
    val strongestConcepts = conceptStrengths.toArray.zipWithIndex.sortBy(_._1).slice(15, 20)
    val conceptWords = new Array[Array[(String, Double)]](5)
    for (i <- 0 to 4) {
      println("Concept " + i)
      conceptWords(i) = new Array[(String, Double)](n)
      val ind = strongestConcepts(i)._2
      for (j <- 0 to (n - 1)) {
        conceptWords(i)(j) = (mostFrequent(j), termConceptRelevance(j, ind))
      }
      conceptWords(i) = conceptWords(i).sortBy(pair => pair._2)(Ordering[Double].reverse)
      for (j <- 0 to 10) {
        println(conceptWords(i)(j))
      }
    }
    println("--------")

    val relations = new Relations(svd, wordToIndex, mostFrequent, docToIndex)

    //Examples
    //6.2
    println("Terms related to Book:")
    relations.termTerms("book", 8).foreach(println)
    println("Terms related to Economy:")
    relations.termTerms("economy", 8).foreach(println)
    println("Terms related to System:")
    relations.termTerms("system", 8).foreach(println)

    //6.3
    println("Docs related to Anarchism:")
    relations.docDocs("Anarchism", 8).foreach(println)
    println("Docs related to Bible:")
    relations.docDocs("Bible", 8).foreach(println)
    println("Docs related to Buckingham Palace:")
    relations.docDocs("Buckingham Palace", 8).foreach(println)

    //6.4
    println("Docs related to Book:")
    relations.termDocs("book", 8).foreach(println)
    println("Docs related to Economy:")
    relations.termDocs("economy", 8).foreach(println)
    println("Docs related to System:")
    relations.termDocs("system", 8).foreach(println)

    //docTerms
    println("Terms related to Anarchism:")
    relations.docTerms("Anarchism", 8).foreach(println)

    //-------------------------------------------------------------
    //7 Elasticsearch - Start docker-elasticsearch-stack first!
    //-------------------------------------------------------------
    val client = initClient()

    //7.1 term index where to store for each term, the 20 most related terms and 100 most related documents
    insertToElasticsearch(client, relations)

    //7.2 document index, where to store for each document the 20 most related documents and 100 most related terms .map(identity)
    //7.3 metadata
    insertToElasticsearch(client, relations, articles.collect())

    testInsertion(client, relations);

    client.close()

    try {
      spark.stop()
    } catch {
      case _: Throwable => () //Exceptions are expected here
    }
  }

  def initClient(): ElasticClient = {
    val props = ElasticProperties("http://localhost:9200")
    val callback = new HttpClientConfigCallback {
      override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
        val creds = new BasicCredentialsProvider()
        creds.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "changeme"))
        httpClientBuilder.setDefaultCredentialsProvider(creds)
      }
    }

    ElasticClient(JavaClient(props, requestConfigCallback = NoOpRequestConfigCallback, httpClientConfigCallback = callback))
  }

  def insertToElasticsearch(client: ElasticClient, relations: Relations): Unit = {

    println("---- Inserting Terms ----")

    client.execute {
      createIndex("term").mapping(
        properties(
          TextField("mostRelevantTerms"),
          TextField("mostRelatedDocs")
        )
      )
    }.await

    relations.getAllWords().foreach { relation: String =>
      client.execute {
        indexInto("term").id(relation).fields(
          "mostRelevantTerms" -> relations.termTerms(relation, 20),
          "mostRelatedDocs" -> relations.termDocs(relation, 100)
        ).refresh(RefreshPolicy.Immediate)
      }.await
    }

  }

  def insertToElasticsearch(client: ElasticClient, relations: Relations, articles: Array[(String, Article)]) = {

    println("---- Inserting documents ----")

    client.execute {
      createIndex("document").mapping(
        properties(
          TextField("mostRelevantDocs"),
          TextField("mostRelatedTerms"),
          TextField("size"),
          TextField("lastContributor"),
          TextField("lastModified")
        )
      )
    }.await

    articles.foreach{ case (title: String, article: Article) =>
      client.execute {
        indexInto("document").id(article.title).fields(
          "mostRelevantDocs" -> relations.docDocs(article.title, 20),
          "mostRelatedTerms" -> relations.docTerms(article.title, 100),
          "size" -> article.size,
          "lastContributor" -> article.contributor,
          "lastModified" -> article.lastModified
        ).refresh(RefreshPolicy.Immediate)
      }.await
    }



  }

  def testInsertion(client: ElasticClient, relations: Relations) = {
    // Test successful insertion
    val resp = client.execute {
      search("term").query(relations.getAllWords().apply(0))
    }.await

    // resp is a Response[+U] ADT consisting of either a RequestFailure containing the
    // Elasticsearch error details, or a RequestSuccess[U] that depends on the type of request.
    // In this case it is a RequestSuccess[SearchResponse]

    println("---- Search Results ----")
    resp match {
      case failure: RequestFailure => println("We failed " + failure.error)
      case results: RequestSuccess[SearchResponse] => println(results.result.hits.hits.toList)
      case results: RequestSuccess[_] => println(results.result)
    }

    // Response also supports familiar combinators like map / flatMap / foreach:
    resp foreach (search => println(s"There were ${search.totalHits} total hits"))
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