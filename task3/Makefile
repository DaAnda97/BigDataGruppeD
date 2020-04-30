up:
	docker-compose up -d
	
file:
	docker exec -it master hadoop namenode -format
	docker cp NASA_access_log_Jul95.txt master:/hadoop-data/NASA_access_log_Jul95.txt
	docker exec -it master hadoop fs -mkdir -p /bda_course/exercise01
	docker exec -it master hadoop fs -put /hadoop-data/NASA_access_log_Jul95.txt /bda_course/exercise01/

mr:
	docker exec -it master hadoop fs -rm -r -f /bda_course/exercise01/output/$(job)
	docker cp ./MapReduce/target/mapreduce-1.0-SNAPSHOT-jar-with-dependencies.jar master:/hadoop-data/mapreduce.jar
	docker exec -it master hadoop jar /hadoop-data/mapreduce.jar $(job) /bda_course/exercise01/NASA_access_log_Jul95.txt /bda_course/exercise01/output/$(job)
	