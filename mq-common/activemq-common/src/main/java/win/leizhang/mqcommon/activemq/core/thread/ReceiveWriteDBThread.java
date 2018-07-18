package win.leizhang.mqcommon.activemq.core.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.leizhang.mqcommon.activemq.core.JdbcSingle;
import win.leizhang.mqcommon.activemq.model.MqReceiver;

import javax.sql.DataSource;

public class ReceiveWriteDBThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(ReceiveWriteDBThread.class.getName());
    private DataSource dataSource;
    private MqReceiver mqReceiver;

    public ReceiveWriteDBThread(MqReceiver mqReceiver, DataSource dataSource) {
        this.mqReceiver = mqReceiver;
        this.dataSource = dataSource;
    }

    public void run() {
        try {
            writeMsgToDB();
            logger.info("MQ 消费者消费消息写入DB成功,topic=" + mqReceiver.getTopicQueue() + " msgText=" + mqReceiver.getMsgText());
        } catch (Exception e) {
            logger.error("MQ 消费者消费消息写入DB异常,topic=" + mqReceiver.getTopicQueue() + " msgText=" + mqReceiver.getMsgText(), e);
        }
    }

    private void writeMsgToDB() throws Exception {
        String sql = " insert into mq_receiver (topic_queue,msg_text,msg_time,msg_id,status,system_name) values (?,?,?,?,?,?) ";
        Object params[] = new Object[]{mqReceiver.getTopicQueue(), mqReceiver.getMsgText(), mqReceiver.getMsgTime(), mqReceiver.getMsgId(), mqReceiver.getStatus(), mqReceiver.getSystemName()};
        JdbcSingle instance = new JdbcSingle(dataSource);
        instance.executeUpdate(sql, params);
    }


}
