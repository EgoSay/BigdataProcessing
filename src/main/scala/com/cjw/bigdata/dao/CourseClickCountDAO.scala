package com.cjw.bigdata.dao

import com.cjw.bigdata.domain.CourseClickCount

import scala.collection.mutable.ListBuffer

/**
 * @author Ego
 * @since 2019/12/1 15:09
 * @version 1.0
 */

object CourseClickCountDAO {

  val tableName = "course_clickCount"
  // cf: Column Family
  val cf = "info"

  /**
   * 保存数据到 HBase
   * @param list CourseClickCount集合
   */
  def save(list: List[ListBuffer[CourseClickCount]]): Unit = {

  }

  /**
   * 根据 RowKey 查询
   * @return 点击统计数
   */
  def selectCount: Long ={
    0L
  }
}
