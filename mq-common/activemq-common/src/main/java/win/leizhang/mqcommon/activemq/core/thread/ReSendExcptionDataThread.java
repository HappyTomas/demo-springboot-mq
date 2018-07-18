
package win.leizhang.mqcommon.activemq.core.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.ProducerCallback;
import win.leizhang.mqcommon.activemq.core.JdbcSingle;
import win.leizhang.mqcommon.activemq.core.JmsBroker;
import win.leizhang.mqcommon.activemq.core.JmsClusterMgr;
import win.leizhang.mqcommon.activemq.model.MqSender;

import javax.jms.*;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ReSendExcptionDataThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(ReSendExcptionDataThread.class.getName());
    private DataSource dataSource;
    private JmsClusterMgr jmsClusterMgr;

    public ReSendExcptionDataThread(DataSource dataSource, JmsClusterMgr jmsClusterMgr) {
        this.dataSource = dataSource;
        this.jmsClusterMgr = jmsClusterMgr;
    }

    public void run() {
        while (true) {
            // 1，查询异常数据
            Object params[] = jmsClusterMgr.getTopicList().toArray();
            if (params.length > 0) {
                List<MqSender> mqSenderList = getMqSenderList(params);
                // 2，发送异常数据
                Map<String, Object> msgPropertyMap = null;
                for (MqSender mqSender : mqSenderList) {
                    msgPropertyMap = new HashMap<String, Object>();
                    msgPropertyMap.put("msgTime", mqSender.getMsgTime());
                    msgPropertyMap.put("msgId", mqSender.getMsgId());
                    int result = sendMsg(mqSender.getMsgText(), false, mqSender.getTopic(), true, msgPropertyMap);
                    if (result == 1) {
                        // 发送成功，更新数据库状态
                        updateMqSenderStatus(mqSender);
                        logger.info("异常消息resend成功，topic=" + mqSender.getTopic() + " msgText=" + mqSender.getMsgText());
                    }
                }
            }
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息
     *
     * @param msg            消息内容
     * @param queue          是否使用queue
     * @param destName       topic名称
     * @param persitent
     * @param msgPropertyMap
     * @return 1：成功，0：失败
     */
    private int sendMsg(final Object msg, final boolean queue, final String destName, final boolean persitent,
                        final Map<String, Object> msgPropertyMap) {
        JmsBroker jmsBroker = null;
        int isSended = 0;
        try {
            jmsBroker = jmsClusterMgr.getJmsBroker();
            if (jmsBroker == null) {
                logger.info("JMS集群中没有可用broker");
                return 0;
            }
            Destination destination = null;
            jmsBroker.getJmsTemplate().execute(destination, new ProducerCallback<Object>() {
                public Object doInJms(Session arg0, MessageProducer arg1) throws JMSException {
                    Message message = null;
                    long expiredTime = -1L;

                    if ((msg instanceof String))
                        message = arg0.createTextMessage((String) msg);
                    else {
                        message = arg0.createObjectMessage((Serializable) msg);
                    }
                    if (msgPropertyMap != null) {
                        Object expiredTimeObj = msgPropertyMap.get("expiredTime");
                        if (expiredTimeObj != null) {
                            expiredTime = ((Long) expiredTimeObj).longValue();
                            msgPropertyMap.remove("expiredTime");
                        }
                        if (!msgPropertyMap.isEmpty()) {
                            Iterator<Entry<String, Object>> iter = msgPropertyMap.entrySet().iterator();
                            while (iter.hasNext()) {
                                Entry<String, Object> entry = (Entry<String, Object>) iter.next();
                                if ((entry.getValue() instanceof String))
                                    message.setStringProperty((String) entry.getKey(), (String) entry.getValue());
                                else if ((entry.getValue() instanceof Long))
                                    message.setLongProperty((String) entry.getKey(), ((Long) entry.getValue()).longValue());
                                else if ((entry.getValue() instanceof Boolean))
                                    message.setBooleanProperty((String) entry.getKey(), ((Boolean) entry.getValue()).booleanValue());
                                else if ((entry.getValue() instanceof Double))
                                    message.setDoubleProperty((String) entry.getKey(), ((Double) entry.getValue()).doubleValue());
                                else if ((entry.getValue() instanceof Integer))
                                    message.setIntProperty((String) entry.getKey(), ((Integer) entry.getValue()).intValue());
                                else if ((entry.getValue() instanceof Byte))
                                    message.setByteProperty((String) entry.getKey(), ((Byte) entry.getValue()).byteValue());
                                else if ((entry.getValue() instanceof Byte))
                                    message.setShortProperty((String) entry.getKey(), ((Short) entry.getValue()).shortValue());
                                else if ((entry.getValue() instanceof Float))
                                    message.setFloatProperty((String) entry.getKey(), ((Float) entry.getValue()).floatValue());
                                else {
                                    message.setObjectProperty((String) entry.getKey(), entry.getValue());
                                }
                            }
                        }
                    }

                    if (expiredTime > 0L) {
                        arg1.send(queue ? arg0.createQueue(destName) : arg0.createTopic(destName), message, persitent ? 2 : 1, 4, expiredTime);
                    } else {
                        arg1.setDeliveryMode(persitent ? 2 : 1);
                        arg1.send(queue ? arg0.createQueue(destName) : arg0.createTopic(destName), message);
                    }
                    return null;
                }
            });
            isSended = 1;
            logger.info("发送消息推送成功，topic= " + destName + " msg=" + msg);
        } catch (Exception e) {
            logger.error("发送消息出现异常{}", e.getMessage(), e);
        }
        return isSended;
    }


    private List<MqSender> getMqSenderList(Object params[]) {
        String sql = getSearchSql(params);
        JdbcSingle instance = new JdbcSingle(dataSource);
        return instance.executeQuery(sql, params);
    }

    private void updateMqSenderStatus(MqSender mqSender) {
        String sql = " update mq_sender set status = 1 , last_resend_time = sysdate() where msg_id = ? ";
        Object params[] = new Object[]{mqSender.getMsgId()};
        JdbcSingle instance = new JdbcSingle(dataSource);
        instance.executeUpdate(sql, params);
    }

    private String getSearchSql(Object params[]) {
        String sql = " select * from mq_sender where status = '0' and topic in ( ";
        StringBuilder queryBuilder = new StringBuilder(sql);
        for (int i = 0; i < params.length; i++) {
            queryBuilder.append(" ? ");
            if (i != params.length - 1)
                queryBuilder.append(",");
        }
        queryBuilder.append(" ) limit 10000 ");
        return queryBuilder.toString();
    }
}
