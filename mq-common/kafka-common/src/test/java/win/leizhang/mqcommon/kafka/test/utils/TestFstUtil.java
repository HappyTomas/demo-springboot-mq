package win.leizhang.mqcommon.kafka.test.utils;

import org.junit.Test;
import win.leizhang.mqcommon.kafka.core.Event;
import win.leizhang.mqcommon.kafka.utils.FstUtil;

/**
 * Created by zealous on 2018/7/17.
 */
public class TestFstUtil {

    @Test
    public void test1() {
        for (int i = 0; i < 100; i++) {
            Event event = new Event("test", "i am word" + i);
            byte[] bytes = FstUtil.serialize(event);
            Event evt = FstUtil.deserialize(bytes, Event.class);
            System.out.println(evt.getEvent());
        }
        System.out.println("ok");
    }

}
