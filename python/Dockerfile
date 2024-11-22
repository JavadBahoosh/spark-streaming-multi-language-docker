FROM bitnami/spark:3.5.3

# Install necessary Python packages
RUN pip install redis
RUN pip install pyspark  # Ensure PySpark is installed

# Set environment variables for Spark
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

# Copy the consumer script
COPY consumer.py /consumer.py

# Set the entry point to run the PySpark script with Kafka dependencies
ENTRYPOINT ["spark-submit", "--master", "local[*]", "--conf", "spark.jars.ivy=/opt/bitnami/spark/.ivy2", "--conf", "spark.jars=/opt/bitnami/spark/jars/spark-sql-kafka-0-10_2.12-3.5.3.jar,/opt/bitnami/spark/jars/kafka-clients-3.5.0.jar,/opt/bitnami/spark/jars/kafka-streams-3.5.0.jar,/opt/bitnami/spark/jars/kafka_2.12-3.5.0.jar,/opt/bitnami/spark/jars/spark-core_2.12-3.5.3.jar,/opt/bitnami/spark/jars/spark-streaming-kafka-0-10-assembly_2.12-3.5.3.jar,/opt/bitnami/spark/jars/commons-pool2-2.11.1.jar,/opt/bitnami/spark/jars/spark-token-provider-kafka-0-10_2.12-3.5.3.jar", "/consumer.py"]
