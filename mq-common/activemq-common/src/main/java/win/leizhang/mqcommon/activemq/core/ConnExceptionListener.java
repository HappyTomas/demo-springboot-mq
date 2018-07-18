package win.leizhang.mqcommon.activemq.core;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.leizhang.mqcommon.activemq.core.thread.ReceiverResumeThread;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;

public class ConnExceptionListener implements ExceptionListener {
    private static Logger logger = LoggerFactory.getLogger(ConnExceptionListener.class.getName());
    private JmsBroker jmsBroker;
    private boolean queue;
    private String destNameWithParam;
    private String messageSelector;
    private String clientId;

    public ConnExceptionListener(JmsBroker jmsBroker, boolean queue, String destNameWithParam, String messageSelector, String clientId) {
        this.jmsBroker = jmsBroker;
        this.queue = queue;
        this.destNameWithParam = destNameWithParam;
        this.messageSelector = messageSelector;
        this.clientId = clientId;
    }

    public void onException(JMSException exception) {
        try {
            ((Connection) this.jmsBroker.getConnMap().get(this.destNameWithParam)).close();
        } catch (Exception localException) {
        }
        new ReceiverResumeThread(this.jmsBroker, this.queue, this.destNameWithParam, messageSelector, clientId).start();
        ActiveMQConnectionFactory acf = (ActiveMQConnectionFactory) this.jmsBroker.getJmsFactory().getConnectionFactory();
        logger.error("JMS集群节点：" + acf.getBrokerURL() + " 上对Destination " + this.destNameWithParam + " 的监听器出现故障" + exception.toString() + "，监听修复线程已启动");
    }
}
