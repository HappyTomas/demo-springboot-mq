package win.leizhang.demo.mq.rabbitmq.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by zealous on 2018/8/2.
 */
@Slf4j
@Component
public class RabbitmqReceiver {

    @RabbitListener(queues = "zhisheng")
    public void receive(String message) {
        System.out.println("收到的 message 是：" + message);
    }

    //@RabbitListener(queues = "rpc-queue-zhisheng")
    public void receive2(String message) {
        System.out.println("--------receive ------- " + message);
    }

}
