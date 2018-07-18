package win.leizhang.demo.springboot.mq.service.job;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import win.leizhang.demo.springboot.mq.service.bo.MessageBO;
import win.leizhang.mqcommon.activemq.core.JmsClusterMgr;

import java.util.Date;
import java.util.UUID;

import static win.leizhang.demo.springboot.mq.utils.MqConstant.SENDER_DEMO;
import static win.leizhang.demo.springboot.mq.utils.MqConstant.msgPropertyMap;

/**
 * Created by zealous on 2018/5/9.
 */
@EnableScheduling
@Component
public class JobSender {

    @Autowired
    private JmsClusterMgr jmsClusterMgr;

    @Value("${win.leizhang.random.value}")
    private String value;
    @Value("${win.leizhang.random.number}")
    private long number;
    @Value("${win.leizhang.random.bignumber}")
    private long bigNum;

    @Scheduled(cron = "1/5 * * * * ?")
    public void jobSenderVTopic() {

        MessageBO bo = new MessageBO();
        bo.setId(bigNum);
        bo.setMsg(value);
        bo.setSendTime(new Date());

        jmsClusterMgr.sendPstTopicMsgOnTransaction(SENDER_DEMO, JSON.toJSONString(bo), msgPropertyMap);
        System.out.println("send vTopic finish!");
    }

    @Scheduled(cron = "1/3 * * * * ?")
    public void jobSenderQueue() {

        MessageBO bo = new MessageBO();
        bo.setId(number);
        bo.setMsg(UUID.randomUUID().toString());
        bo.setSendTime(new Date());

        jmsClusterMgr.sendPstQueueMsgOnTransaction(SENDER_DEMO, JSON.toJSONString(bo), msgPropertyMap);
        System.out.println("send queue finish!");
    }
}
