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
public class QueueFactory {

    @Autowired
    private RabbitmqSender sender;

    @Bean
    public Queue queueDemo() {
        return new Queue("demo");
    }

    @Bean
    public Queue queueZhanglei() {
        return new Queue("zhanglei");
    }

    @Bean(name = "queue1")
    public Queue queue() {
        return new Queue("rpc.queue1");
    }

    @Bean(name = "queue2")
    public Queue queue2() {
        return new Queue("rpc.queue2");
    }

    @Bean(name = "queue3")
    public Queue queue3() {
        return new Queue("rpc.queue3");
    }

    @PostConstruct
    public void init() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < 10; i++) {
            sender.sendDirect("demo", "发送消息----demo-----" + i);

            sender.sendTopic("exchange-demo", "tpc-zhanglei", "发送topic----demo-----" + i);

            sender.sendFanout("exchange-fanout", "fanout广播" + i);
        }

        stopWatch.stop();
        log.info("发送消息耗时:[{}]ms", stopWatch.getTotalTimeMillis());
    }

}
