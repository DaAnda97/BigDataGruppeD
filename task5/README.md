## Setup
1. Install Scala version 2.12.0-M1: https://downloads.lightbend.com/scala/2.12.0-M1/scala-2.12.0-M1.msi
    * Set SCALA_HOME to the install directory
    * Add the bin folder to PATH
2. Download winutils hadoop-3.0.0 https://github.com/steveloughran/winutils/tree/master/hadoop-3.0.0
    * You can use this site to download the single folder: http://kinolien.github.io/gitzip/
    * Set HADOOP_HOME to the location of the folder
    * Set SPARK_LOCAL_HOSTNAME=localhost

## Development in IntelliJ (Windows)
1. Install IntelliJ Scala plugin: `File > Settings > Plugins > "Scala"`
2. Import the project as `sbt` in IntelliJ
    * If neccessary, add the scala version to the project: `File > Project Structure > Global Libraries > + > Scala SDK > System`
    * If the standard run configuration is not available or does not work, try `Run > Edit configuration > + > sbt Task` and enter `~run` as task
    * If the imports cannot be resolved or auto completion does not work, try deleting the newly created .idea directory and open the project again
    * If an interruptedException is thrown at the end of the program, it may be disregarded

## elasticsearch
```
git clone https://github.com/deviantony/docker-elk.git
cd docker-elk/
docker-compose up -d
```
elasticsearch is running at localhost:9200. Log in with usr: elastic, pwd: changeme.

The data is available under: http://localhost:9200/[document|term]/[_source|_doc]/<ID> </br>
f.e. http://localhost:9200/document/_doc/Austria-Hungary, http://localhost:9200/term/_source/power

## data
enwiki_small.xml is a small version of the full Wikipedia dump used for development. The full dump is available here: https://dumps.wikimedia.org/enwiki/20200501/

