package com.cjw.bigdata.sparkstreaming

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

/**
 * 使用Spark Streaming处理Kafka过来的数据
 * 这里发现一个问题, 打印统计信息每次都是先打印一条，然后打印剩下的
 * 无论是10条， 15条还是20条日志，它都是先打印1条，然后打印9， 14， 19...剩下的, 看 flume Kafka-sink的源码没发现原因，后面还得再找找
 * @author Ego
 * @since 2019/12/1 12:20
 * @version 1.0
 */
object StatStreamingApp {

  def main(args: Array[String]): Unit = {

    if (args.length != 3) {
      println("Usage: StatStreamingApp <bootstrap.servers> <group.id> <topics>")
      System.exit(1)
    }
    // 初始化一个StreamingContext
    val conf = new SparkConf().setMaster("local[2]").setAppName("StatStreamingApp")
    val ssc = new StreamingContext(conf, Seconds(5))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> args(0),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> args(1),
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = Array(args(2))
    val messages = KafkaUtils.createDirectStream[String, String](
      ssc, PreferConsistent,
      Subscribe[String, String](topics, kafkaParams))

    messages.map(_.value()).count().print()
    //messages.map(_.value()).map((_, 1)).reduceByKey(_ + _).print()

    ssc.start()
    ssc.awaitTermination()

  }
}
