package win.leizhang.mqcommon.kafka.test.core;

import org.junit.Test;
import win.leizhang.mqcommon.kafka.core.Event;
import win.leizhang.mqcommon.kafka.core.EventProducer;

import java.io.Serializable;

/**
 * Created by zealous on 2018/7/17.
 */
public class TestEventProducer {

    @Test
    public void test1() {

        EventProducer producer = EventProducer.getDefaultEventProducer();
        for (int i = 0; i < 10; i++) {
            Order orderInfo = new Order();
            orderInfo.setOrderNo("orderNo" + i);
            orderInfo.setGoodNo("goodNo" + i);
            orderInfo.setGoodName("goodName" + i);
            Event event = new Event("test", orderInfo);
            event.putProperty("createTime", String.valueOf(System.currentTimeMillis()));
            producer.send(event);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 异常处理
        try {
            try {
                System.in.read();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            producer.close();
        }

    }
}

// 事件对象
class Order implements Serializable {

    private String orderNo;
    private String goodNo;
    private String goodName;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getGoodNo() {
        return goodNo;
    }

    public void setGoodNo(String goodNo) {
        this.goodNo = goodNo;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }
}
