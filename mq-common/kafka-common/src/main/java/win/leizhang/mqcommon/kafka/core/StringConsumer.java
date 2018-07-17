package win.leizhang.mqcommon.kafka.core;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.leizhang.mqcommon.kafka.config.PropertiesUtil;
import win.leizhang.mqcommon.kafka.exception.KafkaCommonException;
import win.leizhang.mqcommon.kafka.handler.StringMessageHandler;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanfu on 2017/6/15.
 */
public class StringConsumer {

    private static Logger log = LoggerFactory.getLogger(StringConsumer.class);

    private KafkaConsumer<String, String> consumer;

    private int pollTime = 1;

    private ExecutorService streamThreadPool;

    /**
     * 消费者客户端状态
     **/
    enum Status {
        INIT, RUNNING, STOPPING, STOPPED;
    }

    private volatile Status status = Status.INIT;

    private StringConsumer() {
        init();
    }

    private void init() {
        Properties props = PropertiesUtil.getStrSerializerConsumerConf();
        streamThreadPool = Executors.newFixedThreadPool(1);
        consumer = new KafkaConsumer<>(props);
    }

    private static class ConsumerHolder {
        static StringConsumer instance = new StringConsumer();
    }

    /**
     * 默认消费者客户端, 单例
     *
     * @return 消费者客户端
     */
    public static StringConsumer getDefaultStringConsumer() {
        return ConsumerHolder.instance;
    }

    public void subscribe(List<String> topics, StringMessageHandler handler) {
        if (topics == null || topics.size() <= 0) {
            throw new IllegalArgumentException("topic can't be null.");
        }
        if (consumer == null) {
            throw new IllegalArgumentException("cosumer init error, please check your config! ");
        }
        consumer.subscribe(topics, new ConsumerRebalanceListener() {
            long assigneStart;

            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                assigneStart = System.currentTimeMillis();
                log.info("mq-client Revoked ");
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                for (TopicPartition topicPartition : collection) {
                    //要关注kafka分区分配情况,详见 KAFKA-2978
                    log.info("mq-client Assigned ,topic: {}, partitions: {} duration: {} ms",
                            topicPartition.topic(), topicPartition.partition(), (System.currentTimeMillis() - assigneStart));
                }
            }
        });

        //consumer.subscribe(topics);
        status = Status.RUNNING;
        try {
            streamThreadPool.submit(new MessageTask(consumer, handler));
        } catch (Exception e) {
            throw new KafkaCommonException("thead exception ", e);
        }

    }

    /**
     * 消息任务
     */
    public class MessageTask implements Runnable {
        protected final KafkaConsumer<String, String> consumer;
        protected StringMessageHandler handler;

        public MessageTask(KafkaConsumer<String, String> consumer, StringMessageHandler handler) {
            this.consumer = consumer;
            this.handler = handler;
        }

        public void run() {
            while (status == Status.RUNNING) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(pollTime);
                    for (ConsumerRecord<String, String> record : records) {
                        if (record == null || record.value() == null) {
                            log.error("the record or record value is null, skip it!");
                            continue;
                        }
                        handler.execute(record.value());
                    }
                } catch (Exception e) {
                    log.error("poll exception, e:", e);
                }
            }
        }
    }

    // 关闭
    public void close() {
        status = Status.STOPPING;
        log.info("MQ Consumer is stopping.......");
        streamThreadPool.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!streamThreadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                streamThreadPool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!streamThreadPool.awaitTermination(10, TimeUnit.SECONDS))
                    log.error("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            streamThreadPool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
        consumer.unsubscribe();
        consumer.close();
        log.info("MQ Consumer is closed!");
        status = Status.STOPPED;
    }

}
