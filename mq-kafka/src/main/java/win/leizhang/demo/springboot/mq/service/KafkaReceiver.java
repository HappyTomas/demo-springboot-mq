package win.leizhang.demo.springboot.mq.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by zealous on 2018/5/9.
 */
@Slf4j
@Component
public class KafkaReceiver {

    @KafkaListener(topics = {"zhang3"})
    private void receive(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        //log.debug("----------------- record ==> {}", record);
        log.info("----------------- record ==> detail: topic={}, partition={}, offset={}, timestamp={}, value={}", record.topic(), record.partition(), record.offset(), record.timestamp(), record.value());

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (!kafkaMessage.isPresent()) {
            log.error("其他为null的场景！");
        } else {
            String str = String.valueOf(kafkaMessage.get());
            if (StringUtils.isBlank(str) || StringUtils.equals(str, "null") || StringUtils.equals(str, "{}")) {
                log.warn("message is null, just return!");
            } else {
                // 业务处理和调用
                log.info("execute logic, the str ==> {}", str);
                log.debug("execute finish!");
            }
        }

        // 手工确认，返回
        ack.acknowledge();
        return;
    }

}