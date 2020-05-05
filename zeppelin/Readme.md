# Spark with Zeppelin Notebook
Hadoop is an open-source framework for working with huge data

## Data

1. Extract log-file `NASA_access_log_Jul95.gz` and rename it to `NASA_access_log_Jul95.txt`.

1. Remove it's last row. It's corrupt!

## Run

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

1. Check notebook at `localhost:80`
