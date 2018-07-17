package win.leizhang.mqcommon.kafka.test.core;

import org.junit.Test;
import win.leizhang.mqcommon.kafka.core.EventConsumer;
import win.leizhang.mqcommon.kafka.test.handler.impl.OrderHandler;

import java.util.Arrays;

/**
 * Created by zealous on 2018/7/17.
 */
public class TestEventConsumer {

    @Test
    public void test1() {
        EventConsumer consumer = EventConsumer.getDefaultEventConsumer();
        consumer.subscribe(Arrays.asList("test"), new OrderHandler());

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
