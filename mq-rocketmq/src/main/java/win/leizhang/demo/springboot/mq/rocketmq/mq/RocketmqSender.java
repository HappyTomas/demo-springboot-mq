package win.leizhang.demo.springboot.mq.rocketmq.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
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

    private static final String DEFAULT_MSG = "Hello RocketMQ";

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
            uee.printStackTrace();
        }

        try {
            // TODO Producer对象在使用之前必须要调用start初始化，初始化一次即可
            // 注意：切记不可以在每次发送消息时，都调用start方法
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
            // TODO 待优化
            producer.shutdown();
        }
    }

    /**
     * 可靠的同步传输
     * description 可靠的同步传输广泛应用于重要通知消息，短信通知，短信营销系统等。
     */
    public void syncProducer(String topic, String tag, Object obj) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("TestSyncProducer");
        producer.setNamesrvAddr(namesrvAddr);
        producer.start();

        // 消息体
        Message msg = new Message(topic, tag, (DEFAULT_MSG).getBytes(RemotingHelper.DEFAULT_CHARSET));

        // 发
        SendResult result = producer.send(msg);
        log.info("发送结果:响应==>{}, 状态==>{}", result.getMsgId(), result.getSendStatus());

        producer.shutdown();
    }

    /**
     * 可靠的异步传输
     * description 异步传输一般用于响应时间敏感的业务场景。
     */
    public void asyncProducer(String topic, String tag, String keys, Object obj) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("TestAsyncProducer");
        producer.setNamesrvAddr(namesrvAddr);
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(0);

        for (int i = 0; i < 100; i++) {
            final int index = i;
            // 消息体
            Message msg = new Message(topic, tag, keys,
                    (DEFAULT_MSG + index).getBytes(RemotingHelper.DEFAULT_CHARSET));

            // 发
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.printf("%-10d OK %s %n", index, sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    System.out.printf("%-10d Exception %s %n", index, e);
                    e.printStackTrace();
                }
            });
        }

        //producer.shutdown();
    }

    /**
     * 单向传输
     * description 单向传输用于需要中等可靠性的情况，例如日志收集。
     */
    public void onewayProducer(String topic, String tag, Object obj) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("TestOnewayProducer");
        producer.setNamesrvAddr(namesrvAddr);
        producer.start();

        for (int i = 0; i < 100; i++) {
            // 消息体
            Message msg = new Message(topic, tag,
                    (DEFAULT_MSG + i).getBytes(RemotingHelper.DEFAULT_CHARSET));

            // 发
            producer.sendOneway(msg);
        }

        producer.shutdown();
    }

}
