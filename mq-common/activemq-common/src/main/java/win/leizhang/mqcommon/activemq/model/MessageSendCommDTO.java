package win.leizhang.mqcommon.activemq.model;

import java.util.List;
import java.util.Map;

public class MessageSendCommDTO {
    private String eventId;
    private String userName;
    private String passWord;
    private String channel;
    private Map<String, String> parameterMap;
    private String title;
    private List<Map<String, Object>> userInfoList;

    public String getEventId() {
        return this.eventId;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassWord() {
        return this.passWord;
    }

    public String getChannel() {
        return this.channel;
    }

    public Map<String, String> getParameterMap() {
        return this.parameterMap;
    }

    public String getTitle() {
        return this.title;
    }

    public List<Map<String, Object>> getUserInfoList() {
        return this.userInfoList;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setParameterMap(Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserInfoList(List<Map<String, Object>> userInfoList) {
        this.userInfoList = userInfoList;
    }

    @Override
    public String toString() {
        return "MessageSendCommDTO [eventId=" + eventId + ", userName=" + userName + ", passWord=" + passWord
                + ", channel=" + channel + ", parameterMap=" + parameterMap + ", title=" + title + ", userInfoList="
                + userInfoList + "]";
    }

}
