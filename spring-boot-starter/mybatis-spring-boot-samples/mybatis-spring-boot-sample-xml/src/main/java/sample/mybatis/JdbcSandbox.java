package sample.mybatis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dongyinuo
 * @date 2021-06-23
 */
public class JdbcSandbox {

    final static String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    final static String URL = "jdbc:mysql://localhost:3306/mybatis?serverTimezone=UTC";
    final static String USERNAME = "root";
    final static String PASSWORD = "root";

    static {
        try {
            // 加载JDBC驱动
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. 数据库连接频繁的开启和关闭本身就造成了资源的浪费，影响系统的性能 (datasource)
     * 2. SQL语句基本都散落在各个JAVA类中 (MappedStatement、BoundSql)
     *    - 可读性很差，不利于维护以及做性能调优
     *    - 改动Java代码需要重新编译、打包部署
     * 3. 传入参数映射和动态SQL  (ParameterHandler、TypeHandler)
     * 4. 结果映射和结果缓存  (ResultSetHandler、TypeHandler)
     *    - 需要返回什么类型的对象；需要返回的对象的数据结构怎么跟执行的结果映射，这样才能将具体的值copy到对应的数据结构上
     *    - 缓存
     * 5. 解决重复SQL语句问题
     *    - <include></include>
     * 6. 我们对数据库进行的操作大部分都是对表数据的增删改查，很多都是对单表的数据进行操作，由这点我们可以想到一个问题：
     *    单表操作可不可以不写SQL语句，通过JavaBean的默认映射生成对应的SQL语句, 比如：一个 customer 类 对应于 customer 表,
     *    customerNo 属性 对应于 customer_no 字段。这样我们就可以通过反射 拼凑成对应的SQL语句显然不是问题.
     *
     * 7. SqlSession、Executor、Configuration
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // 获取数据库连接
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        // 创建Statement对象
        PreparedStatement stmt = conn.prepareStatement("select city, name, address, zip from hotel where city = ?");

        // 设置传入参数
        stmt.setString(1, "1");

        // 执行SQL语句
        ResultSet rs = stmt.executeQuery();

        // 处理查询结果
        List<Map<String, Object>> result = resolveResult(rs);
        System.out.println(result);

        // 关闭链接
        close(rs, stmt, conn);
    }

    public static List<Map<String, Object>> resolveResult(ResultSet rs) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
        while(rs.next()){
            Map map = new HashMap();
            for(int i = 0; i < metaData.getColumnCount(); i++){
                String columnName = metaData.getColumnName(i+1);
                map.put(columnName,rs.getString(columnName));
            }
            resultList.add(map);
        }
        return resultList;
    }

    public static void close(ResultSet rs, Statement stmt, Connection conn){
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
