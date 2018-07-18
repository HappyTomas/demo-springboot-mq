package win.leizhang.mqcommon.activemq.model;

import java.io.Serializable;

public class MqCountResult implements Serializable {
    private long id;
    private String topic;
    private String topicQueue;
    private String msgTime;
    private Long count;
    private Long senderCount;
    private Long receiverCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getSenderCount() {
        return senderCount;
    }

    public void setSenderCount(Long senderCount) {
        this.senderCount = senderCount;
    }

    public Long getReceiverCount() {
        return receiverCount;
    }

    public void setReceiverCount(Long receiverCount) {
        this.receiverCount = receiverCount;
    }

    public String getTopicQueue() {
        return topicQueue;
    }

    public void setTopicQueue(String topicQueue) {
        this.topicQueue = topicQueue;
    }

}
