# Hadoop
Hadoop is an open-source framework for working with huge data

## Installation

1. Extract log-file `NASA_access_log_Jul95.gz` and rename it to `NASA_access_log_Jul95.txt`.

1. Remove it's last row. It's corrupt!

1. Build the executable jar (execute in MapReduce folder)
   ```
   mvn clean compile assembly:single
   ```

### Run in IntelliJ

1. Activate Windows Subsystem for Linux in Windows Features
1. Install Ubuntu from Store
1. Open Bash and install jdk
   ```
   sudo apt update
   sudo apt upgrade
   sudo apt-get install openjdk-8-jdk
   ```
1. Use as IntelliJ Terminal: File -> Settings -> Tools -> Terminal:
   ```
   Shell-Path: "C:/Program Files/WindowsApps/CanonicalGroupLimited.Ubuntu20.04onWindows_2004.2020.424.0_x64__79rhkp1fndgsc/ubuntu2004.exe" run
   Tab Name: Bash
   ```
1.    
   ```
   sudo java -jar ./target/mapreduce-1.0-SNAPSHOT-jar-with-dependencies.jar HoursCount ../NASA_access_log_Jul95.txt ./output/HoursCount
   sudo java -jar ./target/mapreduce-1.0-SNAPSHOT-jar-with-dependencies.jar ResponseLengthCount ../NASA_access_log_Jul95.txt ./output/ResponseLengthCount
   sudo java -jar ./target/mapreduce-1.0-SNAPSHOT-jar-with-dependencies.jar HostnameCount ../NASA_access_log_Jul95.txt ./output/HostnameCount
   ```

### Run with docker-cluster

1. Install make with chocolatey (Powershell as admin):
   ```
   Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
   choco install make
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
