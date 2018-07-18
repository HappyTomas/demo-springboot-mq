package win.leizhang.mqcommon.activemq.model;

public class MqReceiver {
    private String topicQueue;
    private String msgText;
    private String msgTime;
    private String msgId;
    private String status;
    private String systemName;

    public String getTopicQueue() {
        return topicQueue;
    }

    public void setTopicQueue(String topicQueue) {
        this.topicQueue = topicQueue;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @Override
    public String toString() {
        return "MqReceiver [topicQueue=" + topicQueue + ", msgText=" + msgText + ", msgTime=" + msgTime + ", msgId="
                + msgId + ", status=" + status + ", systemName=" + systemName + "]";
    }

}
