# Hadoop
Hadoop is an open-source framework for working with huge data

## Installation

1. Create data folder
   ```
   mkdir data
   ```

1. Create network
   ``` bash
   docker network create proxy
   ```

1. Run the service
   ``` bash
   docker-compose up -d
   ```

1. Load file into hdfs
   ```
   docker cp lorem_ipsum.txt master:/hadoop-data/lorem_ipsum.txt
   docker exec -it master hadoop fs -put lorem_ipsum.txt /bda_course/exercise01/
   ```