package win.leizhang.demo.springboot.mq.rocketmq.job;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import win.leizhang.demo.springboot.mq.rocketmq.mq.RocketmqSender;
import win.leizhang.demo.springboot.mq.rocketmq.service.bo.MessageBO;

import java.util.Date;
import java.util.UUID;

/**
 * Created by zealous on 2018/8/29.
 */
@Slf4j
@EnableScheduling
@Component
public class JobSender {

    @Autowired
    private RocketmqSender rocketmqSender;

    @Scheduled(cron = "1/5 * * * * ?")
    public void jobSenderMsg() {

        // 消息对象
        MessageBO bo = new MessageBO();
        bo.setId(System.currentTimeMillis());
        bo.setMsg(UUID.randomUUID().toString());
        bo.setSendTime(new Date());

        log.info("jobSenderMsg ==> {}", JSON.toJSONString(bo));
        rocketmqSender.defaultMQProducer("TopicTest", "push", bo);

/*
        for (int i = 0; i < 10000; i++) {
        }*/
    }

}
