package win.leizhang.demo.springboot.mq.service.job;

import com.alibaba.fastjson.JSON;
import com.crt.jms.mq.JmsClusterMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import win.leizhang.demo.springboot.mq.service.bo.MessageBO;

import java.util.Date;

import static win.leizhang.demo.springboot.mq.utils.MqConstant.SENDER_DEMO;
import static win.leizhang.demo.springboot.mq.utils.MqConstant.msgPropertyMap;

/**
 * Created by zealous on 2018/5/9.
 */
@EnableScheduling
@Component
public class JobSender {

    @Autowired
    JmsClusterMgr jmsClusterMgr;

    @Value("${win.leizhang.random.value}")
    private String value;
    @Value("${win.leizhang.random.bignumber}")
    private long bigNum;

    @Scheduled(cron = "1/5 * * * * ?")
    public void jobSenderTopic() {

        MessageBO bo = new MessageBO();
        bo.setId(bigNum);
        bo.setMsg(value);
        bo.setSendTime(new Date());

        String topicName = SENDER_DEMO;
        jmsClusterMgr.sendPstTopicMsgOnTransaction(topicName, JSON.toJSONString(bo), msgPropertyMap);
        System.out.println("send finish!");
    }
}
