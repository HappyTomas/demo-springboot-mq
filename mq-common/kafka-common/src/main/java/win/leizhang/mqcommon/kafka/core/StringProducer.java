package win.leizhang.mqcommon.kafka.core;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.leizhang.mqcommon.kafka.config.PropertiesUtil;
import win.leizhang.mqcommon.kafka.utils.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanfu on 2017/6/15.
 */
public class StringProducer {

    private static Logger log = LoggerFactory.getLogger(StringProducer.class);

    private BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<Runnable>(10000);

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 1000 * 60, TimeUnit.MILLISECONDS, blockingQueue);

    private static KafkaProducer producer;

    private StringProducer() {
        String kafka = System.getProperty("kafka.trace.bootstrap.servers");
        try {
            if (null == kafka || kafka.length() <= 0) {
                Properties properties = new Properties();
                properties.load(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("application.properties"));
                kafka = properties.get("kafka.trace.bootstrap.servers").toString();
            }
        } catch (Exception e) {
            log.warn("trace kafka.trace.bootstrap.servers is not configured ");
        }
        if (null != kafka && kafka.length() > 0) {
            init();
        }
    }

    public static void init() {
        try {
            Properties prop = PropertiesUtil.getStrSerializerProducerConf();
            producer = new KafkaProducer(prop);
        } catch (Exception e) {
            log.error("init kafkaProducer error:", e.getMessage());
        }

    }

    public void sendCep(final String topic, final Object cepObj) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                //发送trace span信息到kafka
                String cepJsonSpan = JsonMapper.toJson(cepObj);
                ProducerRecord<String, String> record = new ProducerRecord(topic, cepJsonSpan);
                producer.send(record);
                log.debug("\ncepJsonSpan:\n" + cepJsonSpan);
            }
        };
        executor.submit(run);
    }

    public void send(String topic, String data) {
        ProducerRecord<String, String> record = new ProducerRecord(topic, data);
        producer.send(record);
    }

    public void send(String topic, String data, String key) {
        //Integer partition = key.hashCode() % 6;
        //ProducerRecord record = new ProducerRecord(topic, partition, null ,data);
        ProducerRecord record = new ProducerRecord(topic, key, data);
        producer.send(record);
    }

    public void send(final Object obj, final String key) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                //发送trace span信息到kafka
                List<Object> spanList = new ArrayList();
                spanList.add(obj);
                String jsonSpan = JsonMapper.toJson(spanList);
                ProducerRecord record = new ProducerRecord("zipkin", key, jsonSpan);
                producer.send(record);
                log.debug("\njsonSpan:\n" + jsonSpan);
            }
        };
        executor.submit(run);
    }

    public static StringProducer getStringProducer() {
        return ProducerHolder.instance;
    }

    private static class ProducerHolder {
        static StringProducer instance = new StringProducer();
    }

    /**
     * 关闭客户端
     */
    public void close() {
        producer.close();
    }

}
