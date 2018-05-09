package win.leizhang.demo.springboot.mq.service.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import win.leizhang.demo.springboot.mq.service.KafkaSender;

/**
 * Created by zealous on 2018/5/9.
 */
@EnableScheduling
@Component
public class JobSender {

    @Autowired
    private KafkaSender sender;

    @Scheduled(cron = "1/5 * * * * ?")
    public void jobSenderMsg() {
        sender.send();
    }
}
