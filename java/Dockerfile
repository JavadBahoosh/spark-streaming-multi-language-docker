# Stage 1: Build the jar using Maven
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Use the Spark image and copy the jar from the builder stage
FROM bitnami/spark:3.5.3

# Set the environment variables for Spark
ENV SPARK_HOME=/opt/bitnami/spark
ENV IVY_HOME=$SPARK_HOME/.ivy2
ENV PATH=$SPARK_HOME/bin:$PATH
ENV SPARK_USER=sparkuser

USER root

RUN adduser --disabled-password --gecos '' --uid 1000 sparkuser

# Create the required directories for Ivy
RUN mkdir -p $IVY_HOME/local

# Add the required Kafka JARs for Spark 3.x
ADD https://repo1.maven.org/maven2/org/apache/spark/spark-sql-kafka-0-10_2.12/3.5.3/spark-sql-kafka-0-10_2.12-3.5.3.jar /opt/bitnami/spark/jars/
ADD https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/3.5.0/kafka-clients-3.5.0.jar /opt/bitnami/spark/jars/
ADD https://repo1.maven.org/maven2/org/apache/kafka/kafka-streams/3.5.0/kafka-streams-3.5.0.jar /opt/bitnami/spark/jars/
ADD https://repo1.maven.org/maven2/org/apache/kafka/kafka_2.12/3.5.0/kafka_2.12-3.5.0.jar /opt/bitnami/spark/jars/
ADD https://repo1.maven.org/maven2/org/apache/spark/spark-core_2.12/3.5.3/spark-core_2.12-3.5.3.jar /opt/bitnami/spark/jars/

# Add the additional required JARs
ADD https://repo1.maven.org/maven2/org/apache/spark/spark-streaming-kafka-0-10-assembly_2.12/3.5.3/spark-streaming-kafka-0-10-assembly_2.12-3.5.3.jar /opt/bitnami/spark/jars/
ADD https://repo1.maven.org/maven2/org/apache/commons/commons-pool2/2.11.1/commons-pool2-2.11.1.jar /opt/bitnami/spark/jars/
ADD https://repo1.maven.org/maven2/org/apache/spark/spark-token-provider-kafka-0-10_2.12/3.5.3/spark-token-provider-kafka-0-10_2.12-3.5.3.jar /opt/bitnami/spark/jars/


COPY --from=builder /app/target/spark-java-consumer-1.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["spark-submit", "--class", "KafkaConsumer", "--master", "local[*]", "--conf", "spark.jars.ivy=/opt/bitnami/spark/.ivy2", "--conf", "spark.jars=/opt/bitnami/spark/jars/spark-sql-kafka-0-10_2.12-3.5.3.jar,/opt/bitnami/spark/jars/kafka-clients-3.5.0.jar,/opt/bitnami/spark/jars/kafka-streams-3.5.0.jar,/opt/bitnami/spark/jars/kafka_2.12-3.5.0.jar,/opt/bitnami/spark/jars/spark-core_2.12-3.5.3.jar,/opt/bitnami/spark/jars/spark-streaming-kafka-0-10-assembly_2.12-3.5.3.jar,/opt/bitnami/spark/jars/commons-pool2-2.11.1.jar,/opt/bitnami/spark/jars/spark-token-provider-kafka-0-10_2.12-3.5.3.jar", "/app.jar"]
