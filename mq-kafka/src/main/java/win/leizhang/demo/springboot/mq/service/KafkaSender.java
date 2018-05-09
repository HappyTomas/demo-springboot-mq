package win.leizhang.demo.springboot.mq.service;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import win.leizhang.demo.springboot.mq.service.bo.Message;

import java.util.Date;
import java.util.UUID;

/**
 * Created by zealous on 2018/5/9.
 */
//@Slf4j
@Component
public class KafkaSender {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${win.leizhang.random.bignumber}")
    private long bigNum;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送消息方法
     */
    public void send() {
        // 消息对象
        Message message = new Message();
        message.setId(bigNum);
        message.setMsg(UUID.randomUUID().toString());
        message.setSendTime(new Date());

        String str = JSON.toJSONString(message);
        log.info("message =" + str);

        // 发送
        String topic = "zhisheng";
        ListenableFuture future = kafkaTemplate.send(topic, str);

        // 处理回调
        future.addCallback(o -> log.info("send-消息发送成功 ==> {}", message.getMsg()), throwable -> log.error("消息发送失败 ==> {}", message));
    }

}
