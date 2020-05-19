name := "WikipediaAnalyzer"
version := "1.0"
scalaVersion := "2.12.0"
val sparkVersion = "2.4.5"

resolvers ++= Seq(
  "apache-snapshots" at "http://repository.apache.org/snapshots/"
)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "com.databricks" % "spark-xml_2.12" % "0.9.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "4.0.0",
)
