package win.leizhang.mqcommon.activemq.core.thread;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.AlreadyClosedException;
import org.apache.activemq.ConnectionClosedException;
import org.apache.activemq.ConnectionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.IllegalStateException;
import org.springframework.jms.JmsSecurityException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.ProducerCallback;
import win.leizhang.mqcommon.activemq.core.JDBCUtilSing;
import win.leizhang.mqcommon.activemq.core.JmsBroker;
import win.leizhang.mqcommon.activemq.core.JmsClusterMgr;

import javax.jms.*;
import javax.sql.DataSource;
import java.io.Serializable;
import java.net.ConnectException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jms.IllegalStateException;

public class SendMessageThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(SendMessageThread.class.getName());
    private DataSource dataSource;
    private JmsClusterMgr jmsClusterMgr;
    private Object msg;
    private String destName;
    private boolean queue;
    private boolean persitent;
    private Map<String, Object> msgPropertyMap;

    public SendMessageThread(DataSource dataSource, JmsClusterMgr jmsClusterMgr, boolean queue, Object msg, String destName, boolean persitent, Map<String, Object> msgPropertyMap) {
        this.dataSource = dataSource;
        this.jmsClusterMgr = jmsClusterMgr;
        this.msg = msg;
        this.destName = destName;
        this.queue = queue;
        this.persitent = persitent;
        this.msgPropertyMap = msgPropertyMap;
    }

    public void run() {
        int result = sendMsg(msg, queue, destName, persitent, msgPropertyMap);
        if (result == 0) {
            //将数据库消息状态置0
            updateMqSenderStatus(msgPropertyMap.get("msgId").toString());
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
        //String exception = "";
        int isSended = 0;
        for (int i = 0; i < jmsClusterMgr.getRetryCount(); i++) {
            try {
                jmsBroker = jmsClusterMgr.getJmsBroker();
                if (jmsBroker == null) {
                    logger.info("JMS集群中没有可用broker");
                    //exception = "JMS集群中没有可用broker";
                    break;
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
                                        message.setLongProperty((String) entry.getKey(),
                                                ((Long) entry.getValue()).longValue());
                                    else if ((entry.getValue() instanceof Boolean))
                                        message.setBooleanProperty((String) entry.getKey(),
                                                ((Boolean) entry.getValue()).booleanValue());
                                    else if ((entry.getValue() instanceof Double))
                                        message.setDoubleProperty((String) entry.getKey(),
                                                ((Double) entry.getValue()).doubleValue());
                                    else if ((entry.getValue() instanceof Integer))
                                        message.setIntProperty((String) entry.getKey(),
                                                ((Integer) entry.getValue()).intValue());
                                    else if ((entry.getValue() instanceof Byte))
                                        message.setByteProperty((String) entry.getKey(),
                                                ((Byte) entry.getValue()).byteValue());
                                    else if ((entry.getValue() instanceof Byte))
                                        message.setShortProperty((String) entry.getKey(),
                                                ((Short) entry.getValue()).shortValue());
                                    else if ((entry.getValue() instanceof Float))
                                        message.setFloatProperty((String) entry.getKey(),
                                                ((Float) entry.getValue()).floatValue());
                                    else {
                                        message.setObjectProperty((String) entry.getKey(), entry.getValue());
                                    }
                                }
                            }
                        }

                        if (expiredTime > 0L) {
                            arg1.send(queue ? arg0.createQueue(destName) : arg0.createTopic(destName), message,
                                    persitent ? 2 : 1, 4, expiredTime);
                        } else {
                            arg1.setDeliveryMode(persitent ? 2 : 1);
                            arg1.send(queue ? arg0.createQueue(destName) : arg0.createTopic(destName), message);
                        }
                        return null;
                    }
                });
                isSended = 1;
                logger.info("发送消息推送成功，topic= " + destName + " msg=" + msg);
                break;
            } catch (Exception e) {
                if (((e instanceof AlreadyClosedException)) || ((e instanceof ConnectionClosedException))
                        || ((e instanceof ConnectionFailedException)) || ((e instanceof UncategorizedJmsException))
                        || ((e instanceof IllegalStateException)) || ((e instanceof JmsSecurityException))
                        || ((e instanceof ResourceAllocationException)) || ((e instanceof ConnectException))) {
                    synchronized (jmsBroker) {
                        if (jmsBroker.isNormalWork()) {
                            jmsBroker.setNormalWork(false);
                            jmsClusterMgr.getJmsProviderTaskExecutor().execute(new SenderResumeThread(jmsBroker));
                            ActiveMQConnectionFactory acf = (ActiveMQConnectionFactory) jmsBroker.getJmsFactory()
                                    .getConnectionFactory();
                            logger.error("JMS集群节点 " + acf.getBrokerURL() + "修复线程已启动{}", e.getMessage(), e);
                        }
                    }
                }
                logger.error("发送消息出现异常," + jmsClusterMgr.getRetryInterval() / 1000 + "s后将进行第" + (i + 1) + "次重新发送 {}"
                        , e.getMessage(), e);
                //exception = e.toString();
                try {
                    Thread.sleep(jmsClusterMgr.getRetryInterval());
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (isSended == 0) {
            logger.info("消息发送失败，发送消息重试次数达上限");
            // 发送邮件通知
            //if (jmsClusterMgr.isSendMail) {
            //	String content = "MQ 消息推送失败 topic=" + destName + " msgText=" + msg + " \n异常: " + exception;
            //	jmsClusterMgr.jmsProviderTaskExecutor.execute(new SendEmailThread(destName, content, dataSource));
            //}

        }
        return isSended;
    }

    private void updateMqSenderStatus(String msgId) {
        String sql = " update mq_sender set status = 0 where msg_id = ? ";
        Object params[] = new Object[]{msgId};
        JDBCUtilSing instance = new JDBCUtilSing(dataSource);
        instance.executeUpdate(sql, params);
    }
}