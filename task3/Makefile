up:
	docker-compose up -d
	
file:
	docker exec -it master hadoop namenode -format
	docker cp lorem_ipsum.txt master:/hadoop-data/lorem_ipsum.txt
	docker exec -it master hadoop fs -mkdir -p /bda_course/exercise01
	docker exec -it master hadoop fs -put /hadoop-data/lorem_ipsum.txt /bda_course/exercise01/

mr-count-loremipsum:
	docker cp ./WordCount/target/wordcount-1.0-SNAPSHOT-jar-with-dependencies.jar master:/hadoop-data/wordcount.jar
	docker exec -it master hadoop jar /hadoop-data/wordcount.jar /bda_course/exercise01/lorem_ipsum.txt /bda_course/exercise01/output
	