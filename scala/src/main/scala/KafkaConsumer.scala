import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

object KafkaConsumer {
  def main(args: Array[String]): Unit = {
    val sparkConf = new org.apache.spark.SparkConf().setAppName("KafkaConsumer").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "kafka:9092",
      "key.deserializer" -> classOf[org.apache.kafka.common.serialization.StringDeserializer],
      "value.deserializer" -> classOf[org.apache.kafka.common.serialization.StringDeserializer],
      "group.id" -> "unique_group_id",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("random-data")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val spark = SparkSession.builder.config(sparkConf).getOrCreate()
    import spark.implicits._

    val messages = stream.map(record => record.value())
    val jedisPool = new JedisPool(new JedisPoolConfig(), "redis://redis:6379/0")

    messages.foreachRDD { rdd =>
      if (!rdd.isEmpty()) {
        val df = spark.read.json(rdd)
        val aggregated = df.agg(
          sum(col("value").cast("int")).alias("sum"),
          count("*").alias("count")
        ).collect()

        val totalSum = aggregated.head.getAs[Long]("sum")
        val totalCount = aggregated.head.getAs[Long]("count")

        // Safely interact with Redis
        val jedis = jedisPool.getResource
        try {
          jedis.incrBy("scala_total_messages", totalCount)
          jedis.incrBy("scala_total_sum", totalSum)
        } finally {
          jedis.close()
        }
      }
    }

    ssc.start()
    ssc.awaitTermination()
  }
}
