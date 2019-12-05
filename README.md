## 概述
基于Flume+Kafka+Spark Streaming的大数据流处理平台

## 技术栈
- Flume
- Kafka
- Spark & Spark Streaming


## 实现步骤
### Spark Streaming 整合 Fume
注： 在Spark 2.3 已经不推荐使用 Flume了，详见[SPARK-22142 ](https://jira.apache.org/jira/browse/SPARK-22142)
#### 方式
- Flume-style Push-based Approach
- Pull-based Approach using a Custom Sink
#### 本地测试步骤
1. 启动Spark Streaming 作业
2. 启动Flume agent
3. 通过telnet 输入数据，观察控制台输出

### Spark整合Kafka

- [x]功能一
    >Spark Streaming清洗日志数据, 统计到现在为止的课程访问统计量, 然后将其写入HBase
    ```markdown
    ## HBase表设计
    create 'imooc_course_clickcount','info'
    ## rowKey设计
    yymmmdd_courseId
    ```
    
- [ ]功能二
    >通过统计搜索引擎或者其他渠道引流过来的访问量进行统计，进行不同的引流渠道效果分析
    ```markdown
    ## HBase表设计
    create 'imooc_course_search_clickcount','info'
    ## rowKey设计
    yymmmdd_search_courseId
    ```

## 思考 & 改进
- 统计结果存储为什么选择 HBase ? 其他的比如 MySQL，Redis 等
- 日志实时处理系统, 将分析的结果写到`HBase`里，需要考虑压力问题