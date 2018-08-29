package win.leizhang.demo.springboot.mq.rocketmq.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.UnsupportedEncodingException;

/**
 * Created by zealous on 2018/8/29.
 */
@Slf4j
@Component
public class RocketmqSender {

    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;
    @Value("${apache.rocketmq.producer.producerGroup}")
    private String producerGroup;

    //@PostConstruct
    public void defaultMQProducer(String topic, String tag, Object obj) {
        //生产者的组名
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        //指定NameServer地址，多个地址以 ; 隔开
        producer.setNamesrvAddr(namesrvAddr);

        String str = JSON.toJSONString(obj);
        // 业务代码
        log.debug("准备发mq, topic==>{}, tags==>{}, body==>{}", topic, tag, str);

        // 消息体
        Message msg = new Message();
        msg.setTopic(topic);
        msg.setTags(tag);
        try {
            msg.setBody(str.getBytes(RemotingHelper.DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException uee) {
            log.info("不支持的编码转换类型，原消息体是==>{}", str);
            uee.fillInStackTrace();
        }

        try {
            /**
             * Producer对象在使用之前必须要调用start初始化，初始化一次即可
             * 注意：切记不可以在每次发送消息时，都调用start方法
             */
            producer.start();
            StopWatch stop = new StopWatch();
            stop.start();

            // 发
            SendResult result = producer.send(msg);
            log.info("发送结果:响应==>{}, 状态==>{}", result.getMsgId(), result.getSendStatus());

            stop.stop();
            log.info("发送耗时:{}ms", stop.getTotalTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.shutdown();
        }
    }

}
