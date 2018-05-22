package win.leizhang.demo.springboot.mq.test.service.business;

import com.alibaba.fastjson.JSON;
import com.crt.jms.mq.JmsClusterMgr;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import win.leizhang.demo.springboot.mq.service.bo.MessageBO;
import win.leizhang.demo.springboot.mq.test.BaseTestCase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static win.leizhang.demo.springboot.mq.utils.MqConstant.SENDER_DEMO;

/**
 * Created by zealous on 2018/5/22.
 */
public class TestSender extends BaseTestCase {

    @Autowired
    JmsClusterMgr jmsClusterMgr;

    private static final Map<String, Object> msgPropertyMapTest = new HashMap<>();

    static {
        msgPropertyMapTest.put(JmsClusterMgr.SYSTEM_NAME, "demo-activemq-test");
    }

    // FIXME BUG待修复

    @Rollback(false)
    @Test
    public void testSend1() {

        MessageBO bo = new MessageBO();
        bo.setId(1L);
        bo.setMsg("message123");
        bo.setSendTime(new Date());

        // name
        String topicName = SENDER_DEMO;
        printData(topicName);

        for (int i = 0; i < 10; i++) {
            // 发topic
            jmsClusterMgr.sendPstTopicMsgOnTransaction(topicName, JSON.toJSONString(bo), msgPropertyMapTest);
            // 发q
            //jmsClusterMgr.sendPstQueueMsgOnTransaction(topicName, JSON.toJSONString(bo), msgPropertyMapTest);
        }

        // 测试，睡3秒
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        printData("ok!");
    }

}
