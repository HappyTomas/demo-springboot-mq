package win.leizhang.mqcommon.activemq.model;

import java.io.Serializable;

public class MqSender implements Serializable {
    private String topic;
    private String msgText;
    private String msgTime;
    private String msgId;
    private String status;
    private String systemName;

    public MqSender() {
    }

    public MqSender(String topic, String msgText, String msgTime, String msgId, String status, String systemName) {
        this.topic = topic;
        this.msgText = msgText;
        this.msgTime = msgTime;
        this.msgId = msgId;
        this.status = status;
        this.systemName = systemName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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
        return "MqSender [topic=" + topic + ", msgText=" + msgText + ", msgTime=" + msgTime + ", msgId=" + msgId
                + ", status=" + status + ", systemName=" + systemName + "]";
    }


}
