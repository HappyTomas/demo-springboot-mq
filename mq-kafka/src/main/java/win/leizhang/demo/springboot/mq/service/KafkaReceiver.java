package win.leizhang.demo.springboot.mq.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by zealous on 2018/5/9.
 */
@Component
//@Slf4j
public class KafkaReceiver {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @KafkaListener(topics = {"zhisheng"})
    private void listen(ConsumerRecord<?, ?> record) {

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());

        if (kafkaMessage.isPresent()) {

            Object message = kafkaMessage.get();

            log.info("----------------- record =" + record);
            log.info("----------------- message =" + message);
        }

    }
}