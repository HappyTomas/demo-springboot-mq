package win.leizhang.mqcommon.kafka.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.leizhang.mqcommon.kafka.serialization.FstDeserializer;
import win.leizhang.mqcommon.kafka.serialization.FstSerializer;

import java.util.Properties;

/**
 * Created by wanfu on 2017/6/13.
 */
public class PropertiesUtil {

    private static Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

    public static Properties getStrSerializerProducerConf() {
        Properties props = getProducerConfig();
        props.put("value.serializer", StringSerializer.class);
        return props;
    }

    public static Properties getStrSerializerConsumerConf() {
        Properties props = getConsumerConfig();
        props.put("value.deserializer", StringDeserializer.class);
        return props;
    }

    public static Properties getProducerConfig() {
        Properties props = new Properties();
        try {
            Properties properties = new Properties();
            properties.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("application.properties"));
            Object servers = properties.get("kafka.trace.bootstrap.servers");
            if (null == servers) {
                log.error("trace kafka.trace.bootstrap.servers is not configured");
            }
            props.put("bootstrap.servers", servers.toString());
        } catch (Exception e) {
            log.error("The kafka application.properties file is not loaded. " + e.getMessage());
        }
        String bootstrap = System.getProperty("kafka.trace.bootstrap.servers");
        if (null != bootstrap) {
            props.put("bootstrap.servers", bootstrap);
            log.info("kafka.trace.bootstrap.servers load from System.property:" + bootstrap);
        }
        props.put("batch.size", 16384);
        props.put("linger.ms", 0);
        props.put("retries", 0);
        props.put("acks", "0");
        props.put("value.serializer", FstSerializer.class);
        props.put("key.serializer", StringSerializer.class);
        return props;
    }

    public static Properties getConsumerConfig() {
        Properties props = new Properties();
        try {
            Properties properties = new Properties();
            properties.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("application.properties"));
            Object servers = properties.get("kafka.trace.bootstrap.servers");
            Object groupId = properties.get("kafka.trace.group.id");
            if (null == servers) {
                log.error("trace kafka.trace.bootstrap.servers is not configured");
            }
            if (null == groupId) {
                log.error("trace kafka.trace.group.id is not configured");
            }
            props.put("bootstrap.servers", servers.toString());
            props.put("group.id", groupId.toString());
        } catch (Exception t) {
            log.error("The kafka properties file is not loaded." + t.getMessage());
        }
        String bootstrap = System.getProperty("kafka.trace.bootstrap.servers");
        if (null != bootstrap) {
            props.put("bootstrap.servers", bootstrap);
            log.info("kafka.trace.bootstrap.servers load from System.property:" + bootstrap);
        }
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", FstDeserializer.class);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("fetch.min.bytes", "1");
        //props.put("fetch.max.bytes", "2097152");
        props.put("max.partition.fetch.bytes", 1048576 / 3);//1048576 / 3
        //props.put("fetch.message.max.bytes", 2097152);
        //earliest 当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
        //latest 默认   当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据
        //props.put("auto.offset.reset","latest");
        return props;
    }

}
