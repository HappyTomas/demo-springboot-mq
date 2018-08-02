package win.leizhang.demo.mq.rabbitmq.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zealous on 2018/8/2.
 */
@Slf4j
@Component
public class RabbitmqSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String topic, String message) {
        rabbitTemplate.convertAndSend(topic, message);
    }
}
