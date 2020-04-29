up:
	docker-compose up -d
	
add:
	docker exec -it master hadoop namenode -format
	docker cp lorem_ipsum.txt master:/hadoop-data/lorem_ipsum.txt
	docker exec -it master hadoop fs -mkdir -p /bda_course/exercise01
	docker exec -it master hadoop fs -put /hadoop-data/NASA_access_log_Jul95.txt /bda_course/exercise01/
	docker cp ./MapReduce/target/mapreduce-1.0-SNAPSHOT-jar-with-dependencies.jar master:/hadoop-data/mapreduce.jar

mr:
	docker exec -it master hadoop jar /hadoop-data/mapreduce.jar $(job) /bda_course/exercise01/NASA_access_log_Jul95.txt /bda_course/exercise01/output/$(job)
	