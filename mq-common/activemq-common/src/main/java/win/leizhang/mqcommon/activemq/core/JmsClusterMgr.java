package win.leizhang.mqcommon.activemq.core;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.AlreadyClosedException;
import org.apache.activemq.ConnectionClosedException;
import org.apache.activemq.ConnectionFailedException;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.IllegalStateException;
import org.springframework.jms.JmsSecurityException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import win.leizhang.mqcommon.activemq.core.thread.*;
import win.leizhang.mqcommon.activemq.model.MqReceiver;

import javax.jms.*;
import javax.sql.DataSource;
import java.io.Serializable;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;

import org.springframework.jms.IllegalStateException;


public class JmsClusterMgr {
    private static Logger logger = LoggerFactory.getLogger(JmsClusterMgr.class.getName());
    public static final String EXPIRED_TIME = "expiredTime";
    public static final String SYSTEM_NAME = "systemName";
    private List<String> topicList = new ArrayList<String>();
    private JmsCluster jmsCluster;

    @Autowired
    private DataSource dataSource;

    private int brokerNum;
    private int retryCount = 3;
    private long retryInterval = 3000;
    private List<JmsBroker> jmsBrokerList;
    @Value("${mq.exception.message.auto.resend:false}")
    private boolean isAutoResendExceptionMessage;
    @Value("${mq.sendmail:false}")
    private boolean isSendMail;

    @Autowired
    @Qualifier("jmsProviderTaskExecutor")
    private ThreadPoolTaskExecutor jmsProviderTaskExecutor;

    @Autowired
    @Qualifier("jmsConsumerTaskExecutor")
    private ThreadPoolTaskExecutor jmsConsumerTaskExecutor;

    @Autowired
    public void initThread() {
        if (isAutoResendExceptionMessage) {
            ReSendExcptionDataThread sendThread = new ReSendExcptionDataThread(dataSource, JmsClusterMgr.this);
            sendThread.start();
            logger.info("分布式事务推送消息线程启动成功");
        }
    }

    public void init() {
        this.jmsBrokerList = this.jmsCluster.getJmsBrokerList();
        this.brokerNum = this.jmsBrokerList.size();
    }

    /**
     * 获取可用节点
     *
     * @return
     */
    public JmsBroker getJmsBroker() {
        JmsBroker jmsBroker;
        int random = (int) (Math.random() * 10.0D);
        jmsBroker = this.jmsBrokerList.get(random % this.brokerNum);
        if (jmsBroker.isNormalWork()) {
            return jmsBroker;
        }
        for (int i = 0; i < this.brokerNum; i++) {
            jmsBroker = this.jmsBrokerList.get(i);
            if (jmsBroker.isNormalWork()) {
                return jmsBroker;
            }
        }
        return null;
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
     * @throws Exception
     */
    private int sendNormalMsgOnTransaction(Object msg, boolean queue, String destName, boolean persitent,
                                           Map<String, Object> msgPropertyMap) {
        synchronized (topicList) {
            if (!topicList.contains(destName)) {
                topicList.add(destName);
            }
        }
        Random random = new Random();
        String uuid = UUID.randomUUID().toString();
        String msgId = uuid.substring(0, 8) + random.nextInt(10) + uuid.substring(9, 13) + random.nextInt(10)
                + uuid.substring(14, 18) + random.nextInt(10) + uuid.substring(19, 23) + random.nextInt(10)
                + uuid.substring(24);
        // TODO
        String msgTime = LocalDateTime.now().toString();
        msgPropertyMap.put("msgId", msgId);
        msgPropertyMap.put("msgTime", msgTime);
        writeMqSenderToDB(destName, msg.toString(), msgPropertyMap);
        logger.info("当前事务名称：" + TransactionSynchronizationManager.getCurrentTransactionName());
        if (TransactionSynchronizationManager.getCurrentTransactionName() != null) {
            afterCommit(msg, destName, queue, persitent, msgPropertyMap);
        } else {
            jmsProviderTaskExecutor.execute(new SendMessageThread(dataSource, JmsClusterMgr.this, queue, msg, destName,
                    persitent, msgPropertyMap));
        }
        return 1;
    }

    private void afterCommit(Object msg, String destName, boolean queue, boolean persitent,
                             Map<String, Object> msgPropertyMap) {
        TransactionSynchronizationManager.registerSynchronization(new JMSTransactionSynchronizationAdapter(
                JmsClusterMgr.this, msg, queue, destName, persitent, msgPropertyMap));
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
    @SuppressWarnings("unused")
    private int sendNormalMsg(final Object msg, final boolean queue, final String destName, final boolean persitent,
                              final Map<String, Object> msgPropertyMap) {
        JmsBroker jmsBroker = null;
        // String exception = "";
        int isSended = 0;
        for (int i = 0; i < retryCount; i++) {
            try {
                jmsBroker = getJmsBroker();
                if (jmsBroker == null) {
                    logger.info("JMS集群中没有可用broker");
                    // exception = "JMS集群中没有可用broker";
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
                            jmsProviderTaskExecutor.execute(new SenderResumeThread(jmsBroker));
                            ActiveMQConnectionFactory acf = (ActiveMQConnectionFactory) jmsBroker.getJmsFactory()
                                    .getConnectionFactory();
                            logger.error("JMS集群节点 " + acf.getBrokerURL() + " 出现异常：" + e.toString() + " , 修复线程已启动");
                        }
                    }
                }
                logger.error("发送消息出现异常," + retryInterval / 1000 + "s后将进行第" + (i + 1) + "次重新发送 {}", e.getMessage(), e);
                // exception = e.toString();
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (isSended == 0) {
            String ex = "消息发送失败，发送消息重试次数达上限";
            logger.info(ex);
            // 发送邮件通知
/*
            if (isSendMail) {
                String content = "MQ 消息推送失败 topic=" + destName + " msgText=" + msg + " \n异常: " + ex;
                jmsProviderTaskExecutor.execute(new SendEmailThread(destName, content, dataSource));
            }
*/

        }
        return isSended;
    }

    /**
     * 在同一个事务内发送队列消息
     *
     * @param queueName      队列名称
     * @param msg            消息内容
     * @param msgPropertyMap 消息其他属性
     * @return 1成功，0失败
     */
    public int sendQueueMsgOnTransaction(String queueName, Object msg, Map<String, Object> msgPropertyMap) {
        return sendNormalMsgOnTransaction(msg, true, queueName, false, msgPropertyMap);
    }

    /**
     * 在同一个事务内发送持久化队列消息
     *
     * @param queueName 队列名称
     * @param msg       消息内容
     * @return 1成功，0失败
     */
    public int sendPstQueueMsgOnTransaction(String queueName, Object msg, Map<String, Object> msgPropertyMap) {
        return sendNormalMsgOnTransaction(msg, true, queueName, true, msgPropertyMap);
    }

    /**
     * 在同一个事务内发送topic消息
     *
     * @param topicName      主题名称
     * @param msg            消息内容
     * @param msgPropertyMap 消息其他属性
     * @return 1成功，0失败
     */
    public int sendTopicMsgOnTransaction(String topicName, Object msg, Map<String, Object> msgPropertyMap) {
        return sendNormalMsgOnTransaction(msg, false, topicName, false, msgPropertyMap);
    }

    /**
     * 在同一个事务内发送持久化topic消息
     *
     * @param topicName      主题
     * @param msg            消息内容
     * @param msgPropertyMap 消息其他属性
     * @return 1成功，0失败
     */
    public int sendPstTopicMsgOnTransaction(String topicName, Object msg, Map<String, Object> msgPropertyMap) {
        return sendNormalMsgOnTransaction(msg, false, topicName, true, msgPropertyMap);
    }

    public int registClusterListener(String clientId, boolean queue, String destNameWithParam, String messageSelector,
                                     MessageListener messageListener) {
        int ret = 0;
        JmsBroker jmsBroker = null;
        Connection conn = null;

        for (int i = 0; i < this.brokerNum; i++) {
            try {
                jmsBroker = (JmsBroker) this.jmsBrokerList.get(i);
                jmsBroker.getMessageListenerMap().put(destNameWithParam, messageListener);

                conn = ((ActiveMQConnectionFactory) jmsBroker.getJmsFactory().getConnectionFactory())
                        .createConnection();
                jmsBroker.getConnMap().put(destNameWithParam, conn);

                if (clientId != null) {
                    conn.setClientID(clientId);
                }

                Session se = conn.createSession(false, 2);
                MessageConsumer messageConsumer = null;
                if ((clientId != null) && (!queue)) {
                    if (messageSelector == null)
                        messageConsumer = se.createDurableSubscriber(new ActiveMQTopic(destNameWithParam), clientId);
                    else {
                        messageConsumer = se.createDurableSubscriber(new ActiveMQTopic(destNameWithParam), clientId,
                                messageSelector, true);
                    }
                } else if (messageSelector == null) {
                    messageConsumer = se.createConsumer(
                            queue ? new ActiveMQQueue(destNameWithParam) : new ActiveMQTopic(destNameWithParam));
                } else {
                    messageConsumer = se.createConsumer(
                            queue ? new ActiveMQQueue(destNameWithParam) : new ActiveMQTopic(destNameWithParam),
                            messageSelector);
                }

                messageConsumer.setMessageListener(messageListener);

                conn.setExceptionListener(
                        new ConnExceptionListener(jmsBroker, queue, destNameWithParam, messageSelector, clientId));
                conn.start();
                ret++;
            } catch (Exception e) {
                if (((e instanceof AlreadyClosedException)) || ((e instanceof ConnectionClosedException))
                        || ((e instanceof ConnectionFailedException)) || ((e instanceof UncategorizedJmsException))
                        || ((e instanceof IllegalStateException)) || ((e instanceof JmsSecurityException))
                        || ((e instanceof ResourceAllocationException)) || ((e instanceof ConnectException))
                        || ((e instanceof JMSException))) {
                    new ReceiverResumeThread(jmsBroker, queue, destNameWithParam, messageSelector, clientId).start();
                    logger.error("注册JMS集群节点：" + e.toString() + " , 失败，监听修复线程已启动");
                } else {
                    logger.error("订阅消息出现异常" + e.toString());
                }
            }
        }

        logger.info("对JMS集群中所有Broker注册Destination " + destNameWithParam + " 的监听器完毕，该Destination成功注册了 " + ret + " 个监听器");
        return ret;
    }

    /**
     * 注册队列监听
     *
     * @param queueNameWithParam 需要监听的队列名称
     * @param messageListener    监听器
     * @return 监听器注册个数
     */
    public int registClusterQueueListener(String queueNameWithParam, MessageListener messageListener) {
        return registClusterListener(null, true, queueNameWithParam, null, messageListener);
    }

    /**
     * 注册队列监听
     *
     * @param clientId           客户端ID
     * @param queueNameWithParam 需要监听的队列名称
     * @param messageListener    监听器
     * @return 监听器注册个数
     */
    public int registClusterQueueListener(String clientId, String queueNameWithParam, MessageListener messageListener) {
        return registClusterListener(clientId, true, queueNameWithParam, null, messageListener);
    }

    /**
     * 注册Topic监听
     *
     * @param topicNameWithParam 需要监听的topic名称
     * @param messageListener    监听器
     * @return 监听器注册个数
     */
    public int registClusterTopicListener(String topicNameWithParam, MessageListener messageListener) {
        return registClusterListener(null, false, topicNameWithParam, null, messageListener);
    }

    /**
     * 注册Topic监听
     *
     * @param topicNameWithParam 需要监听的topic名称
     * @param messageSelector    消息选择器
     * @param messageListener    监听器
     * @return 监听器注册个数
     */
    public int registClusterTopicListener(String topicNameWithParam, String messageSelector, MessageListener messageListener) {
        return registClusterListener(null, false, topicNameWithParam, messageSelector, messageListener);
    }

    /**
     * @param uniqueClientId     唯一客户端ID
     * @param topicNameWithParam 需要监听的topic名称
     * @param messageListener    监听器
     * @return 监听器注册个数
     */
    public int registClusterDurableTopicListener(String uniqueClientId, String topicNameWithParam, MessageListener messageListener) {
        return registClusterListener(uniqueClientId, false, topicNameWithParam, null, messageListener);
    }

    /**
     * @param uniqueClientId     唯一客户端ID
     * @param topicNameWithParam 需要监听的topic名称
     * @param messageSelector    消息选择器
     * @param messageListener    监听器
     * @return 监听器注册个数
     */
    public int registClusterDurableTopicListener(String uniqueClientId, String topicNameWithParam, String messageSelector, MessageListener messageListener) {
        return registClusterListener(uniqueClientId, false, topicNameWithParam, messageSelector, messageListener);
    }

    /**
     * 取消所有注册的Listener
     */
    public void unregistClusterListener() {
        Connection conn = null;
        Map<String, Connection> connMap = null;
        Iterator<String> destNameWithParamIter = null;
        String destNameWithParam = null;
        for (JmsBroker jmsBroker : this.jmsBrokerList) {
            connMap = jmsBroker.getConnMap();
            destNameWithParamIter = connMap.keySet().iterator();
            while (destNameWithParamIter.hasNext()) {
                try {
                    destNameWithParam = (String) destNameWithParamIter.next();
                    conn = (Connection) connMap.get(destNameWithParam);
                    conn.stop();
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            jmsBroker.getConnMap().clear();
            jmsBroker.getDurableTopicClientIdMap().clear();
            jmsBroker.getMessageListenerMap().clear();
        }
    }

    /**
     * 取消指定topic的listener
     *
     * @param destName
     */
    public void unregistClusterListener(String destName) {
        Connection conn = null;
        Map<String, Connection> connMap = null;
        Iterator<String> destNameWithParamIter = null;
        String destNameWithParam = null;
        for (JmsBroker jmsBroker : this.jmsBrokerList) {
            connMap = jmsBroker.getConnMap();
            destNameWithParamIter = connMap.keySet().iterator();
            while (destNameWithParamIter.hasNext())
                try {
                    destNameWithParam = (String) destNameWithParamIter.next();
                    if (destNameWithParam.equals(destName)) {
                        conn = (Connection) connMap.get(destNameWithParam);
                        conn.stop();
                        conn.close();
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public JmsCluster getJmsCluster() {
        return this.jmsCluster;
    }

    public void setJmsCluster(JmsCluster jmsCluster) {
        this.jmsCluster = jmsCluster;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }

    public List<String> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<String> topicList) {
        this.topicList = topicList;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ThreadPoolTaskExecutor getJmsProviderTaskExecutor() {
        return jmsProviderTaskExecutor;
    }

    public void setJmsProviderTaskExecutor(ThreadPoolTaskExecutor jmsProviderTaskExecutor) {
        this.jmsProviderTaskExecutor = jmsProviderTaskExecutor;
    }

    public ThreadPoolTaskExecutor getJmsConsumerTaskExecutor() {
        return jmsConsumerTaskExecutor;
    }

    public void setJmsConsumerTaskExecutor(ThreadPoolTaskExecutor jmsConsumerTaskExecutor) {
        this.jmsConsumerTaskExecutor = jmsConsumerTaskExecutor;
    }

    public void writeReceiveMsgToDB(TextMessage objMsg) {
        try {
            String msgTime = objMsg.getStringProperty("msgTime");
            if (msgTime == null) {
                msgTime = LocalDateTime.now().toString();
            }
            String msgId = objMsg.getStringProperty("msgId");
            if (msgId == null) {
                msgId = UUID.randomUUID().toString();
            }
            String systemName = objMsg.getStringProperty("systemName");
            if (systemName == null) {
                systemName = "";
            }
            String msgText = objMsg.getText();
            String topicQueue = objMsg.getJMSDestination().toString();

            MqReceiver mqReceiver = new MqReceiver();
            mqReceiver.setMsgId(msgId);
            mqReceiver.setMsgText(msgText);
            mqReceiver.setMsgTime(msgTime);
            mqReceiver.setStatus("1");
            mqReceiver.setSystemName(systemName);
            mqReceiver.setTopicQueue(topicQueue);
            jmsConsumerTaskExecutor.execute(new ReceiveWriteDBThread(mqReceiver, dataSource));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeMqSenderToDB(String topic, String msgText, Map<String, Object> msgPropertyMap) {
        String sql = " insert into mq_sender (topic,msg_text,status,system_name,msg_time,msg_id) values (?,?,?,?,?,?) ";
        String systemName = msgPropertyMap.get(SYSTEM_NAME) == null ? "" : msgPropertyMap.get(SYSTEM_NAME).toString();
        String msgId = msgPropertyMap.get("msgId").toString();
        String msgTime = msgPropertyMap.get("msgTime").toString();
        Object params[] = new Object[]{topic, msgText, "1", systemName, msgTime, msgId};
        JDBCUtilSing instance = new JDBCUtilSing(dataSource);
        instance.executeUpdate(sql, params);
    }

}