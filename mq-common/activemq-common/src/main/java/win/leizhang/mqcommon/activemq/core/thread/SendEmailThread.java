package win.leizhang.mqcommon.activemq.core.thread;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.leizhang.mqcommon.activemq.core.JDBCUtilSing;
import win.leizhang.mqcommon.activemq.model.Email;
import win.leizhang.mqcommon.activemq.model.MessageSendCommDTO;
import win.leizhang.mqcommon.activemq.utils.HttpClientUtil;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendEmailThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(SendEmailThread.class.getName());
    private DataSource dataSource;
    private String msg;
    private String topic;
    private String url;

    public SendEmailThread(String topic, String msg, DataSource dataSource) {
        this.msg = msg;
        this.dataSource = dataSource;
        this.topic = topic;
    }

    public void run() {
        sendEmail();
    }

    private MessageSendCommDTO getEmailSetting() {
        MessageSendCommDTO dto = null;
        String sql = " select user_name,password,channel,eventId,title,user_email,topic,code,url from mq_email where topic = ? ";
        Object params[] = new Object[]{topic};
        JDBCUtilSing instance = new JDBCUtilSing(dataSource);
        Email email = instance.getEmailSetting(sql, params);
        if (email == null) {
            logger.info("邮件发送失败，缺少邮件参数配置");
            return dto;
        } else {
            url = email.getUrl();
            dto = new MessageSendCommDTO();
            dto.setChannel(email.getChannel());
            dto.setEventId(email.getEventId());
            dto.setTitle(email.getTitle());
            dto.setUserName(email.getUserName());
            dto.setPassWord(email.getPassword());
            Map<String, String> parameterMap = new HashMap<String, String>();
            parameterMap.put(email.getCode(), msg);
            dto.setParameterMap(parameterMap);
            String emails = email.getUser_email();
            String[] em = emails.split("\\,");
            List<Map<String, Object>> emailList = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < em.length; i++) {
                if (!"".equals(em[i].trim())) {
                    Map<String, Object> emap = new HashMap<String, Object>();
                    emap.put("email", em[i].trim());
                    emailList.add(emap);
                }
            }
            dto.setUserInfoList(emailList);
        }
        return dto;
    }

    private void sendEmail() {
        MessageSendCommDTO dto = getEmailSetting();
        if (dto != null) {
            String result = new HttpClientUtil().httpPost(url, JSONObject.toJSON(dto).toString());
            logger.info("邮件发送结果" + result);
        }
    }
}
