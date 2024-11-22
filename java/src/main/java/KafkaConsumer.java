import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KafkaConsumer {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("KafkaConsumer").setMaster("local[*]");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "kafka:9092");
        kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put("group.id", "use_a_separate_group_id_for_streaming");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics = Arrays.asList("random-data");

        JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        jssc,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.Subscribe(topics, kafkaParams)
                );

        stream.map(record -> record.value())
                .foreachRDD(rdd -> {
                    if (!rdd.isEmpty()) {
                        SparkSession spark = SparkSession.builder().config(rdd.context().getConf()).getOrCreate();
                        Dataset<Row> df = spark.read().json(rdd);
                        Dataset<Row> valueData = df.select("value");

                        // Use getLong() to retrieve values as Long and then cast it to Integer if needed
                        JavaRDD<Integer> values = valueData.javaRDD().map(row -> {
                            Long longValue = row.getLong(0);
                            return longValue.intValue();
                        });

                        int sum = values.reduce(Integer::sum);
                        long count = values.count();

                        try (Jedis jedis = new Jedis("redis://redis:6379/0")) {
                            jedis.incrBy("java_total_messages", count);
                            jedis.incrBy("java_total_sum", sum);
                        }
                    }
                });


        try {
            jssc.start();
            jssc.awaitTermination();
        } catch (InterruptedException e) {
            System.err.println("Streaming context interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore the interrupt status
        }
    }
}
