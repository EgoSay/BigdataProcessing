package com.cjw.bigdata.utils

/**
 * 对日志进行清洗处理后的格式
 * @author Ego
 * @since 2019/12/1 13:57
 * @version 1.0
 */
case class ClickLog(ip: String, time:String, courseId:Int, statusCode:Int, referer:String)
