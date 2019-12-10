package com.cjw.bigdata.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/**
 * HBase 操作工具类
 * @author Ego
 * @version 1.0
 * @since 2019/12/1 15:44
 */
public class HBaseUtils {

    private Connection conn = null;

    private HBaseUtils() {
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

    private static HBaseUtils instance = null;

    public  static synchronized HBaseUtils getInstance() {
        if(null == instance) {
            instance = new HBaseUtils();
        }
        return instance;
    }

    /**
     * 根据表名获取到Table实例
     */
    public Table getTable(String tableName) {

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
    private void put(String tableName, String rowKey, String cf, String column, String value) {
        Table table = getTable(tableName);


        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(cf.getBytes(), column.getBytes(), value.getBytes());

        try {
            table.put(put);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据表名和输入条件获取 HBase 的记录条数
     */
    public Map<String, Long> query(String tableName, String columnFamily, String qualifier, String condition) throws Exception{

        HashMap<String, Long> map = new HashMap<>();
        Table table = getTable(tableName);
        Scan scan = new Scan();
        Filter filter = new PrefixFilter(condition.getBytes());
        scan.setFilter(filter);

        ResultScanner results = table.getScanner(scan);
        results.forEach(result -> {
            String row = Bytes.toString(result.getRow());
            long clickCount = Bytes.toLong(result.getValue(columnFamily.getBytes(), qualifier.getBytes()));
            map.put(row, clickCount);
        });

        return map;
    }


    public static void main(String[] args) throws Exception {
        String tableName = "course_clickcount" ;
        String rowkey = "20191210_22";
        String condition = "2019";
        String cf = "info" ;
        String qualifier = "click_count";
        String value = "4";
        Map<String, Long> query = HBaseUtils.getInstance().query("course_search_clickCount", cf, qualifier, condition);
        for(Map.Entry<String, Long> entry: query.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        HBaseUtils.getInstance().put(tableName, rowkey, cf, qualifier, value);
    }
}
