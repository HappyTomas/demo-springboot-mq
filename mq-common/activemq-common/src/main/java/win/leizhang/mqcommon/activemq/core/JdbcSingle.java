package win.leizhang.mqcommon.activemq.core;

import org.springframework.jdbc.datasource.DataSourceUtils;
import win.leizhang.mqcommon.activemq.model.Email;
import win.leizhang.mqcommon.activemq.model.MqSender;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * jdbc工具类
 */
public class JdbcSingle {

    private DataSource dataSource;

    public JdbcSingle(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 设置PrepareStatement对象中Sql语句中的参数
     *
     * @param sql    sql语句
     * @param params 参数列表
     * @throws SQLException
     */
    private PreparedStatement setPrepareStatementParams(Connection conn, String sql, Object[] params)
            throws SQLException {
        PreparedStatement pstm = conn.prepareStatement(sql); // 获取对象
        if (params != null) {
            for (int i = 0; i < params.length; i++) // 遍历参数列表填充参数
            {
                pstm.setObject(i + 1, params[i]);
            }
        }
        return pstm;
    }

    public List<MqSender> executeQuery(String sql, Object[] params) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<MqSender> mqSenderList = new ArrayList<MqSender>();
        try {
            // 1.获取Connection
            conn = getConnection();
            // 2.获取Statement
            pstm = setPrepareStatementParams(conn, sql, params); // 填充参数
            // 3.执行查询，得到ResultSet
            rs = pstm.executeQuery();
            // 4.处理ResultSet
            while (rs.next()) {
                // rs.get+数据库中对应的类型+(数据库中对应的列别名)
                MqSender mqSender = new MqSender();
                mqSender.setMsgId(rs.getString("msg_id"));
                mqSender.setMsgText(rs.getString("msg_text"));
                mqSender.setMsgTime(rs.getString("msg_time"));
                mqSender.setStatus(rs.getString("status"));
                mqSender.setTopic(rs.getString("topic"));
                mqSender.setSystemName(rs.getString("system_name"));
                mqSenderList.add(mqSender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 5.关闭数据库相应的资源
            free(rs, pstm, conn);
        }
        return mqSenderList;
    }

    public Email getEmailSetting(String sql, Object[] params) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            // 1.获取Connection
            conn = getConnection();
            // 2.获取Statement
            pstm = setPrepareStatementParams(conn, sql, params); // 填充参数
            // 3.执行查询，得到ResultSet
            rs = pstm.executeQuery();
            // 4.处理ResultSet
            if (rs.next()) {
                // rs.get+数据库中对应的类型+(数据库中对应的列别名)
                Email email = new Email();
                email.setChannel(rs.getString("channel"));
                email.setEventId(rs.getString("eventId"));
                email.setPassword(rs.getString("password"));
                email.setTitle(rs.getString("title"));
                email.setTopic(rs.getString("topic"));
                email.setUser_email(rs.getString("user_email"));
                email.setUserName(rs.getString("user_name"));
                email.setCode(rs.getString("code"));
                email.setUrl(rs.getString("url"));
                return email;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 5.关闭数据库相应的资源
            free(rs, pstm, conn);
        }
        return null;
    }

    /**
     * 更新数据库操作
     *
     * @param sql    sql语句
     * @param params 参数列表
     * @return 执行操作的结果
     * @throws SQLException
     */
    public boolean executeUpdate(String sql, Object[] params) {
        boolean result = false;
        PreparedStatement pstm = null;
        Connection conn = null;
        try {
            conn = DataSourceUtils.getConnection(dataSource);
            pstm = setPrepareStatementParams(conn, sql, params); // 填充参数
            pstm.executeUpdate(); // 执行更新
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            free(null, pstm, conn);
        }
        return result;
    }

    private Connection getConnection() throws InterruptedException {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        while (conn == null) {
            Thread.sleep(200);
            conn = DataSourceUtils.getConnection(dataSource);
        }
        return conn;
    }

    /**
     * 释放资源
     *
     * @param rs
     * @param stmt
     * @param conn
     */
    public void free(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    DataSourceUtils.releaseConnection(conn, dataSource);
                }
            }
        }
    }
}
