package com.cjw.bigdata.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;


/**
 * HBase 操作工具类
 * @author Ego
 * @version 1.0
 * @since 2019/12/1 15:44
 */
public class HBaseUtils {

    private Connection conn = null;

    public HBaseUtils() {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1,hadoop2,hadoop3");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        // HDP 安装的 HBase在zookeeper的znode不同
        conf.set("zookeeper.znode.parent", "/hbase-unsecure");
        try {
            this.conn = ConnectionFactory.createConnection(conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据表名获取到Table实例
     */
    private Table getTable(String tableName) {

        Table table = null;

        try {
            table = conn.getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return table;
    }

    /**
     * 添加一条记录到HBase表
     * @param tableName HBase表名
     * @param rowKey  HBase表的rowKey
     * @param cf HBase表的columnFamily
     * @param column HBase表的列
     * @param value  写入HBase表的值
     */
    public void put(String tableName, String rowKey, String cf, String column, String value) {
        Table table = getTable(tableName);


        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(cf.getBytes(), column.getBytes(), value.getBytes());

        try {
            table.put(put);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static HBaseUtils instance = null;

    public  static synchronized HBaseUtils getInstance() {
        if(null == instance) {
            instance = new HBaseUtils();
        }
        return instance;
    }

    public static void main(String[] args) {
        String tableName = "course_clickcount" ;
        String rowkey = "20191203_22";
        String cf = "info" ;
        String column = "click_count";
        String value = "2";

        HBaseUtils.getInstance().put(tableName, rowkey, cf, column, value);
    }
}
