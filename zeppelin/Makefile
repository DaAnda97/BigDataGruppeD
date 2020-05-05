up:
	docker-compose up -d
	
delete:
	docker container stop $(docker container ls -aq)
	docker container prune
	rm data
	
file:
	docker exec -it master hadoop namenode -format
	docker cp NASA_access_log_Jul95.txt master:/hadoop-data/NASA_access_log_Jul95.txt
	docker exec -it master hadoop fs -mkdir -p /bda_course/exercise01
	docker exec -it master hadoop fs -put /hadoop-data/NASA_access_log_Jul95.txt /bda_course/exercise01/