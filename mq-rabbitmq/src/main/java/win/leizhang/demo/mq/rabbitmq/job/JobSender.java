package win.leizhang.demo.mq.rabbitmq.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import win.leizhang.demo.mq.rabbitmq.mq.RabbitmqSender;

import java.util.UUID;

/**
 * Created by zealous on 2018/5/9.
 */
@Slf4j
@EnableScheduling
@Component
public class JobSender {

    @Autowired
    private RabbitmqSender sender;

    @Scheduled(cron = "1/3 * * * * ?")
    public void jobSenderMsg() {

        String str = "test==>" + UUID.randomUUID().toString();

        sender.sendDirect("demo", str);
    }

}
