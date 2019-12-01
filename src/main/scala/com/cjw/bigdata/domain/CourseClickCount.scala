package com.cjw.bigdata.domain

/**
 *  课程点击统计数实体类
 * @param day_course 对应HBase中的 RowKey, 即 时间 + 课程名
 * @param clickCount 点击次数
 *
 * @author Ego
 * @since 2019/12/1 15:09
 * @version 1.0
 */

case class CourseClickCount(day_course:String, clickCount:Long)
