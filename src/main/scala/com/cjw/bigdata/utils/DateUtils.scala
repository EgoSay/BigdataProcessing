package com.cjw.bigdata.utils

import java.util.Date

import org.apache.commons.lang3.time.FastDateFormat

/**
 * 日期格式处理
 * @author Ego
 * @since 2019/12/1 13:54
 * @version 1.0
 */
object DateUtils {
  val YYYYMMDDHHMMSS_FORMAT: FastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
  val TARGET_FORMAT: FastDateFormat = FastDateFormat.getInstance("yyyyMMddHHmmss")


  def getTime(time: String): Long = {
    YYYYMMDDHHMMSS_FORMAT.parse(time).getTime
  }

  def parseToMinute(time :String): String = {
    TARGET_FORMAT.format(new Date(getTime(time)))
  }
  def main(args: Array[String]): Unit = {
    println(parseToMinute("2017-10-22 14:46:01"))
  }
}
