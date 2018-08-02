package win.leizhang.demo.mq.rabbitmq.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by zealous on 2018/8/2.
 */
@Slf4j
@Component
public class TopicFactory {

    /**
     * 交换机
     */

    @Bean
    public TopicExchange exchangeDemo() {
        return new TopicExchange("exchange-demo");
    }

    /**
     * 绑定
     */

    @Bean
    public Binding binding(Queue queueZhanglei, TopicExchange exchange) {
        return BindingBuilder.bind(queueZhanglei).to(exchange).with("tpc-zhanglei");
    }

}
