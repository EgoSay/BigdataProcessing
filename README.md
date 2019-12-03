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



## 思考 & 改进
- 日志实时处理系统, 将分析的结果写到`HBase`里，需要考虑压力问题
- 