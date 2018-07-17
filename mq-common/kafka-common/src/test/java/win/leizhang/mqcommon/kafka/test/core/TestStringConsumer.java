package win.leizhang.mqcommon.kafka.test.core;

import org.junit.Test;
import win.leizhang.mqcommon.kafka.core.StringConsumer;
import win.leizhang.mqcommon.kafka.test.handler.impl.StringHandler;

import java.util.Arrays;

/**
 * Created by zealous on 2018/7/17.
 */
public class TestStringConsumer {

    @Test
    public void test1() {
        StringConsumer consumer = StringConsumer.getDefaultStringConsumer();
        consumer.subscribe(Arrays.asList("zipkin"), new StringHandler());

        // 异常处理
        try {
            try {
                System.in.read();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            consumer.close();
        }

    }
}
