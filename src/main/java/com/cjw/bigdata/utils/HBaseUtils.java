package com.cjw.bigdata.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;

import java.io.IOException;


/**
 * HBase 操作工具类
 * @author Ego
 * @version 1.0
 * @since 2019/12/1 15:44
 */
public class HBaseUtils {

    private Connection conn = null;

    private void getConn() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop2:2181");

        conn = ConnectionFactory.createConnection(conf);
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

    public void put(String tableName, String rowKey, String cf, String column, String value) {
        Table table = getTable(tableName);

        Put put = new Put(Bytes.toBytes(rowKey));
        put.add(Bytes.toBytes(cf), Bytes.toBytes(column), Bytes.toBytes(value));

        try {
            table.put(put);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
