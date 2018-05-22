package win.leizhang.demo.springboot.mq.service;

import com.crt.jms.mq.JmsClusterMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import win.leizhang.demo.springboot.mq.service.business.DemoMessageEvent;

import static win.leizhang.demo.springboot.mq.utils.MqConstant.LISTENER_DEMO;
import static win.leizhang.demo.springboot.mq.utils.MqConstant.SENDER_DEMO;

/**
 * 所有的MQ消费者在这里注册
 * <p>
 * Created by zealous on 2017/11/11.
 */
public class JmsRegisterListenerFactory {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private JmsClusterMgr jmsClusterMgr;

    @Autowired
    DemoMessageEvent demoMessageEvent;

    public JmsRegisterListenerFactory() {
    }

    public JmsClusterMgr getJmsClusterMgr() {
        return jmsClusterMgr;
    }

    public void setJmsClusterMgr(JmsClusterMgr jmsClusterMgr) {
        this.jmsClusterMgr = jmsClusterMgr;
    }

    public void init() {
        log.info("Start register MQ Listener.............");
        // demo的
        jmsClusterMgr.registClusterQueueListener(LISTENER_DEMO, demoMessageEvent);
        //jmsClusterMgr.registClusterQueueListener(SENDER_DEMO, demoMessageEvent);
        log.info("Finish register MQ Listener.............");
    }

}