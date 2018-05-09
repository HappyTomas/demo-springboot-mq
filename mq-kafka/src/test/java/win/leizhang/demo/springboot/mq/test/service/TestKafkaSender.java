package win.leizhang.demo.springboot.mq.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import win.leizhang.demo.springboot.mq.service.KafkaSender;
import win.leizhang.demo.springboot.mq.test.BaseTestCase;

/**
 * Created by zealous on 2018/5/9.
 */
public class TestKafkaSender extends BaseTestCase {

    @Autowired
    private KafkaSender sender;

    @Test
    public void testSend() {
        for (int i = 0; i < 3; i++) {
            //调用消息发送类中的消息发送方法
            sender.send();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        printData(null);
    }
}
