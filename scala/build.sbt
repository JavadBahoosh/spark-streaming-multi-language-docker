// Import the assembly plugin
import sbtassembly.AssemblyPlugin.autoImport._

// Enable the assembly plugin
enablePlugins(sbtassembly.AssemblyPlugin)

name := "spark-scala-consumer"
version := "1.0"
scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
  // Spark dependencies
  "org.apache.spark" %% "spark-core" % "3.5.3",
  "org.apache.spark" %% "spark-sql" % "3.5.3",
  "org.apache.spark" %% "spark-streaming" % "3.5.3",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.5.3",

  // Kafka dependencies
  "org.apache.kafka" % "kafka-clients" % "3.5.0",
  "org.apache.kafka" % "kafka-streams" % "3.5.0",
  "org.apache.kafka" %% "kafka" % "3.5.0",

  // Redis and Jedis
  "redis.clients" % "jedis" % "4.4.1",

  // Additional dependencies
  "org.apache.spark" %% "spark-streaming-kafka-0-10-assembly" % "3.5.3",
  "org.apache.commons" % "commons-pool2" % "2.11.1",
  "org.apache.spark" %% "spark-token-provider-kafka-0-10" % "3.5.3"
)

// Specify merge strategies for conflicting files
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
