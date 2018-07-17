package win.leizhang.mqcommon.kafka.test.handler.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.leizhang.mqcommon.kafka.handler.StringMessageHandler;

/**
 * Created by wanfu on 2017/6/13.
 */
public class StringHandler implements StringMessageHandler {

    private static Logger log = LoggerFactory.getLogger(StringHandler.class);

    private static int i = 0;

    @Override
    public void execute(String data) {
        //log.info("result:" + data + ";time:" + System.currentTimeMillis() * 1000);
        //log.info("i:" + i++);
        //System.out.println("data:" + data + "data.length:"+data.length());
        System.out.println("i:" + i++);
    }
}
