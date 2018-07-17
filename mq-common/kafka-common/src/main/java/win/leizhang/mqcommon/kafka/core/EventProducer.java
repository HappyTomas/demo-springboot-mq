package win.leizhang.mqcommon.kafka.core;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import win.leizhang.mqcommon.kafka.config.PropertiesUtil;

import java.util.Properties;

/**
 * Created by wanfu on 2017/6/13.
 */
public class EventProducer {

    private KafkaProducer<String, Event> producer;

    private EventProducer() {
        init();
    }

    private void init() {
        Properties props = PropertiesUtil.getProducerConfig();
        producer = new KafkaProducer<>(props);
    }

    public void send(Event event) {
        ProducerRecord<String, Event> record = new ProducerRecord<>(event.getTopic(), event);
        producer.send(record);
    }

    public static EventProducer getDefaultEventProducer() {
        return ProducerHolder.instance;
    }

    private static class ProducerHolder {
        static EventProducer instance = new EventProducer();
    }

    // 关闭客户端
    public void close() {
        producer.close();
    }

}
