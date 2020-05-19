import com.databricks.spark.xml._
import org.apache.spark.sql
import org.apache.spark.sql.SparkSession

object WikipediaAnalyzer {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder.master("local").getOrCreate()

    //load xml dataframe
    val dataframe = spark.read
      .option("rowTag", "page")
      .xml("enwiki_small.xml")

    //filter redirect entries
    val noRedirects = dataframe.filter(row => row.getAs("redirect") == null)

    //noRedirects is of the type Dataframe. The corresponding RDD is required so it can be turned into a pair RDD using the .map call
    val keyValue = noRedirects.rdd.map(row =>
      (
        row.getAs("title").toString(), //key
        row.getAs("revision").asInstanceOf[sql.Row].getAs("text").toString()) //value
      )

    println(keyValue.lookup("Bacteriophage"))
    try {
      spark.stop()
    } catch {
      case _: Throwable => () //Exceptions are expected here
    }
  }
}