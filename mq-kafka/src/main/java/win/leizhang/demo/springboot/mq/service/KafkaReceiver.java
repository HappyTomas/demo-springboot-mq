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

    private static final String DEFAULT_TOPIC = "zhang3";

    private final static String STR_NULL = "null";
    private final static String STR_BRACE = "{}";

    @KafkaListener(topics = {DEFAULT_TOPIC})
    private void receive(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        log.debug("----------------- record ==> {}", record);
        // 手工确认，返回
        ack.acknowledge();
        log.debug("----------------- record ==> detail: topic={}, partition={}, offset={}, timestamp={}, value={}", record.topic(), record.partition(), record.offset(), record.timestamp(), record.value());

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (!kafkaMessage.isPresent()) {
            log.error("其他为null的场景！");
        } else {
            String str = String.valueOf(kafkaMessage.get());
            if (StringUtils.isBlank(str) || StringUtils.equals(str, STR_NULL) || StringUtils.equals(str, STR_BRACE)) {
                log.warn("message is null, just return!");
            } else {
                // 分类处理
                handleByTopic(record.topic(), str);
            }
        }

        // 返回
    }


    /**
     * 按topic分类处理
     *
     * @param topic   主题
     * @param message 消息
     */
    private void handleByTopic(String topic, String message) {
        log.info("topic ==> {}, message ==> {}", topic, message);

        // 分类
        if (StringUtils.equals(topic, DEFAULT_TOPIC)) {
            // TODO logic
            log.info("logic={}, message={}", topic, message);
        } else {
            // 其他逻辑
            log.warn("未匹配到处理逻辑，仍然做签收处理！");
        }
        log.debug("execute finish!");
    }

}
