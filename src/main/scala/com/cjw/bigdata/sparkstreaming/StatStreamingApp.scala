package com.cjw.bigdata.sparkstreaming

import com.cjw.bigdata.dao.CourseClickCountDAO
import com.cjw.bigdata.domain.CourseClickCount
import com.cjw.bigdata.utils.{ClickLog, DateUtils}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import scala.collection.mutable.ListBuffer

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
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )
    val topics = Array(args(2))
    val messages = KafkaUtils.createDirectStream[String, String](
      ssc, PreferConsistent,
      Subscribe[String, String](topics, kafkaParams))

    // 进行简单的统计，测试整个数据处理流程的畅通性
    // messages.map(_.value()).count().print()

    // 进行数据清洗，处理成需要的统一格式
    val logs = messages.map(_.value())
    val cleanedData = logs.map(line => {
      val infos = line.split("\t")

      // infos(2) = "GET /class/130.html HTTP/1.1"
      // url = /class/130.html
      val urls = infos(2).split(" ")(1)
      var courseId = 0
      if (urls.startsWith("/class")) {
        val courseIdHTML = urls.split("/")(2)
        courseId = courseIdHTML.substring(0, courseIdHTML.lastIndexOf(".")).toInt
      }

      ClickLog(infos(0), DateUtils.parseToMinute(infos(1)), courseId, infos(3).toInt, infos(4))
    })

    // 统计到现在为止实战课程的访问量
    /**
     * https://www.sogou.com/web?query=Spark SQL实战
     * ===>
     * https:/www.sogou.com/web?query=Spark SQL实战
     */
    cleanedData.map(x => {
      val referer = x.referer.replaceAll("//", "/")
      val splits = referer.split("/")
      var host = ""
      if (splits.length > 2) {
        host = splits(1)
      }
      (host, x.courseId, x.time)
    }).filter(_._1 != "").map(x => {
      (x._3.substring(0, 8) + "_" + x._1 + "_" + x._2, 1)
    }).reduceByKey(_ + _).foreachRDD(rdd => {
      rdd.foreachPartition(partition => {
        val list = new ListBuffer[CourseClickCount]
        partition.foreach(pair => {
          println("pair{}" + pair)
          list.append(CourseClickCount(pair._1, pair._2))
        })
        CourseClickCountDAO.save(list)
      })
    })
    cleanedData.print()
    ssc.start()
    ssc.awaitTermination()

  }
}
