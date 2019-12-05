package com.cjw.bigdata.dao

import com.cjw.bigdata.domain.CourseClickCount
import com.cjw.bigdata.utils.HBaseUtils
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

/**
 * @author Ego
 * @since 2019/12/1 15:09
 * @version 1.0
 */

object CourseClickCountDAO {

  val tableName = "course_clickcount"
  // cf: Column Family
  val cf = "info"
  val qualifer = "click_count"

  /**
   * 保存数据到 HBase
   * @param resultList CourseClickCount集合
   */
  def save(resultList: ListBuffer[CourseClickCount]): Unit = {

    val table = HBaseUtils.getInstance().getTable(tableName)
    for(ele <- resultList) {
      table.incrementColumnValue(ele.day_course.getBytes(), cf.getBytes(), qualifer.getBytes(), ele.clickCount)
    }

  }

  /**
   * 根据 RowKey 查询
   * @return 点击统计数
   */
  def calCount(day_course: String): Long ={
    val table = HBaseUtils.getInstance().getTable(tableName)
    val get = new Get(day_course.getBytes())
    val value = table.get(get).getValue(cf.getBytes(), qualifer.getBytes())
    if (value == Nil){
      0L
    } else {
      Bytes.toLong(value)
    }
  }

  def main(args: Array[String]): Unit = {
    val list = new ListBuffer[CourseClickCount]
    list.append(CourseClickCount("20191205_8",8))
    list.append(CourseClickCount("20191205_9",9))
    list.append(CourseClickCount("20191205_1",100))

    save(list)

    println(calCount("20191205_8") + ":" + calCount("20191205_9") + ":" + calCount("20191205_1"))
  }
}
