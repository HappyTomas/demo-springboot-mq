package win.leizhang.demo.mq.rabbitmq.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by zealous on 2018/8/2.
 */
@Slf4j
@Component
public class FanoutFactory {

    /**
     * 交换机
     */

    @Bean
    public FanoutExchange exchangeFanout() {
        return new FanoutExchange("exchange-fanout");
    }

    /**
     * 绑定
     */
    @Bean
    public Binding binding1(@Qualifier("queue1") Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    public Binding binding2(@Qualifier("queue2") Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    public Binding binding3(@Qualifier("queue3") Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

}
