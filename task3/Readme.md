# Hadoop
Hadoop is an open-source framework for working with huge data

## Installation

1. Install make with chocolatey (Powershell as admin):
   ```
   Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
   choco install make
   ```

1. Extract log-file `NASA_access_log_Jul95.gz` and rename it to `NASA_access_log_Jul95.txt`.

1. Build the executable jar (execute in MapReduce folder)
   ```
   mvn clean compile assembly:single
   ```
   
1. Create data folder
   ```
   mkdir data
   ```

1. Create network
   ``` bash
   docker network create hadoop_network
   ```

1. Run the service
   ``` bash
   make up 
   ```

1. Load file and program into hdfs
   ``` bash
   make file
   ```

1. Run Map-Reduce-Job:
   ```
   make mr job=<JOB_NAME>
   ```

1. View results:
   ```
   docker exec -it master hadoop fs -cat /bda_course/exercise01/output/<JOB_NAME>/part-<FILENAME>
   ```
