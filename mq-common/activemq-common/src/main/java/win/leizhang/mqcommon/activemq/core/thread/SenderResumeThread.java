package win.leizhang.mqcommon.activemq.core.thread;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.core.BrowserCallback;
import win.leizhang.mqcommon.activemq.core.JmsBroker;

import javax.jms.JMSException;
import javax.jms.QueueBrowser;
import javax.jms.Session;

public class SenderResumeThread extends Thread {
    private JmsBroker jmsBroker;

    public SenderResumeThread(JmsBroker jmsBroker) {
        this.jmsBroker = jmsBroker;
    }

    public void run() {
        ActiveMQConnectionFactory acf = (ActiveMQConnectionFactory) this.jmsBroker.getJmsFactory().getConnectionFactory();
        while (true)
            try {
                this.jmsBroker.getJmsTemplate().browse(new BrowserCallback<Object>() {
                    public Object doInJms(Session arg0, QueueBrowser arg1) throws JMSException {
                        arg1.getQueue();
                        return null;
                    }
                });
                synchronized (this.jmsBroker) {
                    this.jmsBroker.setNormalWork(true);
                }
                System.out.println("已修复JMS Broker " + acf.getBrokerURL() + " , 已经加入集群");
                break;
            } catch (Exception e) {
                synchronized (this.jmsBroker) {
                    try {
                        System.out.println("JMS Broker " + acf.getBrokerURL() + " 正在修复中...");
                        this.jmsBroker.wait(10000L);
                    } catch (Exception localException1) {
                    }
                }
            }
    }
}
