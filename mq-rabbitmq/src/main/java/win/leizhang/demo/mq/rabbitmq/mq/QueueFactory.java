package win.leizhang.demo.mq.rabbitmq.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


/**
 * Created by zealous on 2018/8/2.
 */
@Slf4j
@Component
public class QueueFactory {

    @Bean
    public Queue queueDemo() {
        return new Queue("demo");
    }

    @Bean
    public Queue queueZhanglei() {
        return new Queue("zhanglei");
    }

    @Bean
    public Queue queue1() {
        return new Queue("rpc.queue1");
    }

    @Bean
    public Queue queue2() {
        return new Queue("rpc.queue2");
    }

    @Bean
    public Queue queue3() {
        return new Queue("rpc.queue3");
    }

}
