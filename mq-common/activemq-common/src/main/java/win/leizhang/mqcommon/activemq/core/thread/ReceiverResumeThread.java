package win.leizhang.mqcommon.activemq.core.thread;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.leizhang.mqcommon.activemq.core.ConnExceptionListener;
import win.leizhang.mqcommon.activemq.core.JmsBroker;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

public class ReceiverResumeThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(ReceiverResumeThread.class.getName());
    private JmsBroker jmsBroker;
    private boolean queue;
    private String destNameWithParam;
    private String messageSelector;
    private String clientId;

    public ReceiverResumeThread(JmsBroker jmsBroker, boolean queue, String destNameWithParam, String messageSelector, String clientId) {
        this.jmsBroker = jmsBroker;
        this.queue = queue;
        this.destNameWithParam = destNameWithParam;
        this.messageSelector = messageSelector;
        this.clientId = clientId;
    }

    public void run() {
        Connection conn = null;
        while (true) {
            try {
                conn = ((ActiveMQConnectionFactory) this.jmsBroker.getJmsFactory().getConnectionFactory()).createConnection();
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

                messageConsumer.setMessageListener((MessageListener) this.jmsBroker.getMessageListenerMap().get(this.destNameWithParam));

                conn.setExceptionListener(new ConnExceptionListener(this.jmsBroker, this.queue, this.destNameWithParam, messageSelector, clientId));
                conn.start();

                ActiveMQConnectionFactory acf = (ActiveMQConnectionFactory) this.jmsBroker.getJmsFactory().getConnectionFactory();
                logger.info("已修复对JMS Borker " + acf.getBrokerURL() + " 中Destination " + this.destNameWithParam + " 的监听");
                break;
            } catch (Exception e) {
                ActiveMQConnectionFactory acf = (ActiveMQConnectionFactory) this.jmsBroker.getJmsFactory().getConnectionFactory();
                logger.info("等待修复对JMS Borker " + acf.getBrokerURL() + " 中Destination " + this.destNameWithParam + " 的监听");
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
        }
    }
}
