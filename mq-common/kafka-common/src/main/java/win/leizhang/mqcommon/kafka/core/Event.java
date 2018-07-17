package win.leizhang.mqcommon.kafka.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Event implements Serializable {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(Event.class);
    // 主题Topic
    private String topic;
    // 事件
    private Object event;
    // 消息内容
    private byte[] body;

    /**
     * 非消息中间件需要的业务信息
     */
    private Map<String, String> properties = new HashMap<>();

    /**
     * 事件构造函数
     *
     * @param topic 主题名字
     * @param event 事件内容
     */
    public Event(String topic, Object event) {
        this.topic = topic;
        this.event = event;
    }

    /**
     * 事件构造函数
     *
     * @param topic 主题名字
     * @param body  消息内容
     */
    public Event(String topic, byte[] body) {
        this.topic = topic;
        this.body = body;
    }

    public void putProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        logger.debug("properties info : {}, key : {} ", properties, key);
        if (key == null) {
            logger.error("the key[{}] cant not be null", key);
            return null;
        }
        if (properties == null) {
            logger.error("the properties is null!");
            return null;
        }
        return properties.get(key);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Object getEvent() {
        return event;
    }

    public void setEvent(Object event) {
        this.event = event;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
