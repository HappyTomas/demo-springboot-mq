package win.leizhang.mqcommon.activemq.core;

import org.springframework.core.Ordered;
import org.springframework.transaction.support.TransactionSynchronization;
import win.leizhang.mqcommon.activemq.core.thread.SendMessageThread;

import java.util.Map;

public class JmsTransactionSynchronizationAdapter implements TransactionSynchronization, Ordered {
    private Object msg;
    private String destName;
    private boolean queue;
    private boolean persitent;
    private JmsClusterMgr jmsClusterMgr;
    private Map<String, Object> msgPropertyMap;

    public JmsTransactionSynchronizationAdapter(JmsClusterMgr jmsClusterMgr, Object msg, boolean queue, String destName, boolean persitent, Map<String, Object> msgPropertyMap) {
        this.jmsClusterMgr = jmsClusterMgr;
        this.msg = msg;
        this.destName = destName;
        this.queue = queue;
        this.persitent = persitent;
        this.msgPropertyMap = msgPropertyMap;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void suspend() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void beforeCommit(boolean readOnly) {
    }

    @Override
    public void beforeCompletion() {
    }

    @Override
    public void afterCommit() {
        jmsClusterMgr.getJmsProviderTaskExecutor().execute(new SendMessageThread(jmsClusterMgr.getDataSource(), jmsClusterMgr, queue, msg, destName, persitent, msgPropertyMap));
    }

    @Override
    public void afterCompletion(int status) {
    }

}
