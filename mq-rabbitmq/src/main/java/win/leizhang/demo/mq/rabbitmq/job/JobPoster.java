package win.leizhang.demo.mq.rabbitmq.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import win.leizhang.demo.mq.rabbitmq.mq.RabbitmqSender;

import javax.annotation.PostConstruct;

/**
 * Created by zealous on 2018/8/2.
 */
@Slf4j
@Component
public class JobPoster {

    @Autowired
    private RabbitmqSender sender;

    @PostConstruct
    public void init() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < 10; i++) {
            // 普通消息
            sender.sendDirect("demo", "发送消息----demo-----" + i);
            // 主题消息
            sender.sendTopic("exchange-demo", "tpc-zhanglei", "发送topic----demo-----" + i);
            // 广播消息
            sender.sendFanout("exchange-fanout", "fanout广播" + i);
        }

        stopWatch.stop();
        log.info("发送消息耗时:[{}]ms", stopWatch.getTotalTimeMillis());
    }

}
