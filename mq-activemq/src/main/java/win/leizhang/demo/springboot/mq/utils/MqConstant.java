package win.leizhang.demo.springboot.mq.utils;

import com.crt.jms.mq.JmsClusterMgr;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zealous on 2017/11/11.
 */
public class MqConstant {

    public static final Map<String, Object> msgPropertyMap = new HashMap<>();

    static {
        msgPropertyMap.put(JmsClusterMgr.SYSTEM_NAME, "demo-activemq");
    }

    // 默认MQ虚拟消费名
    private final static String ACTIVEMQ_V_CONSUMBER = "VConsumers.";
    // 默认MQ虚拟Topic
    private final static String ACTIVEMQ_VIRTUAL_TOPIC = "VirtualTopic.";
    // 节点
    private final static String NODE = "A.";

    /**
     * 定义的组和vTopic
     */
    private final static String VTOPIC_DEMO = ACTIVEMQ_VIRTUAL_TOPIC + "demoMessage.";

    /**
     * 定义的MQ队列名
     */
    private final static String QUEUE_DEMO_EVENT = "DEMO_EVENT";

    /**
     * sender
     */
    public final static String SENDER_DEMO = VTOPIC_DEMO + QUEUE_DEMO_EVENT;

    /**
     * listener
     */
    public final static String LISTENER_DEMO = ACTIVEMQ_V_CONSUMBER + NODE + SENDER_DEMO;

}
