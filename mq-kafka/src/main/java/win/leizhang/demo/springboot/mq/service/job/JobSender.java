package win.leizhang.demo.springboot.mq.service.job;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import win.leizhang.demo.springboot.mq.service.KafkaSender;
import win.leizhang.demo.springboot.mq.service.bo.MessageBO;

import java.util.Date;
import java.util.UUID;

/**
 * Created by zealous on 2018/5/9.
 */
@Slf4j
@EnableScheduling
@Component
public class JobSender {

    @Autowired
    private KafkaSender sender;

    @Value("${win.leizhang.random.bignumber}")
    private long bigNum;

    private static final String DEFAULT_TOPIC = "zhang3";

    @Scheduled(cron = "1/5 * * * * ?")
    public void jobSenderMsg() {

        // 消息对象
        MessageBO bo = new MessageBO();
        bo.setId(bigNum);
        bo.setMsg(UUID.randomUUID().toString());
        bo.setSendTime(new Date());

        String str = JSON.toJSONString(bo);
        log.info("jobSenderMsg ==> {}", str);

        sender.send(DEFAULT_TOPIC, str);
    }

    @Scheduled(cron = "1/7 * * * * ?")
    public void jobSenderMsg2() {

        // 消息对象
        MessageBO bo = new MessageBO();
        bo.setId(System.currentTimeMillis());
        bo.setMsg(UUID.randomUUID().toString());
        bo.setSendTime(new Date());

        String str = JSON.toJSONString(bo);
        log.info("jobSenderMsg2 ==> {}", str);

        sender.send(DEFAULT_TOPIC, str);
    }
}
