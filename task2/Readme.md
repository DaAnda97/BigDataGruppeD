# Hadoop
Hadoop is an open-source framework for working with huge data

## Installation

1. Create certificate database
   ``` bash
   touch acme.json
   chmod 600 acme.json
   ```

1. Install tools and network
   ``` bash
   apt install net-tools
   docker network create proxy
   ```

1. Set environment-variables:
   ``` bash
   apt install apache2-utils -y
   echo "COMPOSE_PROJECT_NAME=hadoop" >> .env
   echo "DOMAIN=localhost" >> .env
   echo "EMAIL=<YOUR_MAIL>" >> .env
   echo "TRAEFIK_CREDS=$(echo $(htpasswd -nb <USER> '<PASSWORD>'))" >> .env
   echo "HADOOP_CREDS=$(echo $(htpasswd -nb <USER> '<PASSWORD>'))" >> .env
   ```


1. Run the service
   ``` bash
   docker-compose up -f traefik-compose.yml -d
   docker-compose up -d
   ```

