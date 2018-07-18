package win.leizhang.mqcommon.activemq.core;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Connection;
import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.Map;

public class JmsBroker {
    private PooledConnectionFactory jmsFactory;
    private JmsTemplate jmsTemplate;
    private boolean normalWork;
    private Map<String, Connection> connMap;
    private Map<String, MessageListener> messageListenerMap;
    private Map<String, String> durableTopicClientIdMap;

    public JmsBroker() {
        this.connMap = new HashMap<>(0);
        this.messageListenerMap = new HashMap<>(0);
        this.durableTopicClientIdMap = new HashMap<>(0);
    }

    public PooledConnectionFactory getJmsFactory() {
        return this.jmsFactory;
    }

    public void setJmsFactory(PooledConnectionFactory jmsFactory) {
        this.jmsFactory = jmsFactory;
    }

    public JmsTemplate getJmsTemplate() {
        return this.jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public boolean isNormalWork() {
        return this.normalWork;
    }

    public void setNormalWork(boolean normalWork) {
        this.normalWork = normalWork;
    }

    public Map<String, Connection> getConnMap() {
        return this.connMap;
    }

    public void setConnMap(Map<String, Connection> connMap) {
        this.connMap = connMap;
    }

    public Map<String, MessageListener> getMessageListenerMap() {
        return this.messageListenerMap;
    }

    public void setMessageListenerMap(Map<String, MessageListener> messageListenerMap) {
        this.messageListenerMap = messageListenerMap;
    }

    public Map<String, String> getDurableTopicClientIdMap() {
        return this.durableTopicClientIdMap;
    }

    public void setDurableTopicClientIdMap(Map<String, String> durableTopicClientIdMap) {
        this.durableTopicClientIdMap = durableTopicClientIdMap;
    }
}