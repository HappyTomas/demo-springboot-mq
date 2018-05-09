package win.leizhang.demo.springboot.mq.service;

import lombok.extern.slf4j.Slf4j;
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

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());

        if (kafkaMessage.isPresent()) {

            Object obj = kafkaMessage.get();

            log.info("----------------- record ==> {}", record);
            log.info("----------------- object ==> {}", obj);
        }

    }
}