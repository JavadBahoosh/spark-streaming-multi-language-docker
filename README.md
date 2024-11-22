# Multi-Language Spark Streaming with Kafka and Redis: A Comparative Boilerplate

This project aims to create the required Docker infrastructure for consuming data from a Kafka topic using
Spark Streaming in different languages (Scala, Python, and Java). Additionally, it includes boilerplate code
for implementing Spark Streaming consumers in these languages.
## Prerequisites

- Docker
- redis-cli

## Setup

1. Clone the repository.
2. Build the Docker images: `docker compose build`
3. Run the project: `./run.sh`

## Monitoring

The `run.sh` script monitors Redis keys every 5 seconds:
- Scala: `scala_total_messages`, `scala_total_sum`
- Python: `python_total_messages`, `python_total_sum`
- Java: `java_total_messages`, `java_total_sum`
