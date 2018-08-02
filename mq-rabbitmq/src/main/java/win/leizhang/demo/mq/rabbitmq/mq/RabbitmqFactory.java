package win.leizhang.demo.mq.rabbitmq.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;


/**
 * Created by zealous on 2018/8/2.
 */
@Slf4j
@Component
public class RabbitmqFactory {

    @Autowired
    private RabbitmqSender sender;

    @PostConstruct
    public void init() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < 100; i++) {
            sender.send("demo", "发送消息----demo-----" + i);
        }

        stopWatch.stop();
        log.info("发送消息耗时：{}ms", stopWatch.getTotalTimeMillis());
    }

    @Bean
    public Queue testQueue() {
        return new Queue("demo");
    }

    @Bean
    public Queue q1() {
        return new Queue("zhisheng");
    }

}
