from pyspark.sql import SparkSession
from pyspark.sql.functions import col, from_json
from pyspark.sql.types import StructType, StructField, IntegerType
import redis

# Initialize Spark session with Kafka and Kafka clients dependency
spark = SparkSession.builder \
    .appName("KafkaConsumer") \
    .getOrCreate()

# Define the schema for the Kafka value
json_schema = StructType([
    StructField("id", IntegerType(), True),
    StructField("value", IntegerType(), True)
])

# Read data from Kafka topic using structured streaming
kafka_df = spark \
    .readStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", "kafka:9092") \
    .option("subscribe", "random-data") \
    .load()

# The Kafka "value" is in bytes, so we need to deserialize it
messages = kafka_df.selectExpr("CAST(value AS STRING)").select(from_json("value", json_schema).alias("data")).select("data.*")

# Redis processing logic
def process_partition(partition):
    r = redis.Redis(host='redis', port=6379, db=0)
    total = 0
    count = 0
    for row in partition:
        total += row['value']
        count += 1
    r.incrby("python_total_messages", count)
    r.incrby("python_total_sum", total)

# Write the stream data to the process_partition function
messages.writeStream.foreachBatch(lambda batch_df, batch_id: batch_df.foreachPartition(process_partition)) \
    .outputMode("update") \
    .start() \
    .awaitTermination()
