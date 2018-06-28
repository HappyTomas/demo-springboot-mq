package win.leizhang.demo.springboot.mq.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Created by zealous on 2018/5/9.
 */
@Slf4j
@Component
public class KafkaSender {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送消息方法
     */
    public void send(String topic, String str) {

        // 发送
        ListenableFuture future = kafkaTemplate.send(topic, str);

        try {
            // 用于调试
            SendResult result = (SendResult) future.get();
            RecordMetadata recordMetadata = result.getRecordMetadata();
            //log.debug("metaData ==> {}", recordMetadata);
            log.debug("metaData ==> detail: topic={}, partition={}, offset={}, timestamp={}, value={}", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp(), recordMetadata.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 处理回调
        future.addCallback(o -> log.info("send-消息发送成功 ==> ok"), throwable -> log.error("消息发送失败 ==> fail"));
    }

}
