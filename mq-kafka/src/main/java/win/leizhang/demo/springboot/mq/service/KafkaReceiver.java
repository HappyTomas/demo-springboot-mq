package win.leizhang.demo.springboot.mq.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by zealous on 2018/5/9.
 */
@Slf4j
@Component
public class KafkaReceiver {

    @KafkaListener(topics = {"zhisheng"})
    private void listen(ConsumerRecord<?, ?> record) {
        log.info("----------------- record ==> {}", record);
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());

        if (kafkaMessage.isPresent()) {
            // 原对象
            Object obj = kafkaMessage.get();
            // 转字符串
            String str = String.valueOf(obj);

            // 报文为空，返回
            if (StringUtils.isBlank(str)) {
                log.info("message is null, just return!");
                return;
            }

            log.info("----------------- string ==> {}", str);
        }

    }
}