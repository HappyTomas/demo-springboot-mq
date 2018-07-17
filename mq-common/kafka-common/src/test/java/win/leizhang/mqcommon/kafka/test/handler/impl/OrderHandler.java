package win.leizhang.mqcommon.kafka.test.handler.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.leizhang.mqcommon.kafka.core.Event;
import win.leizhang.mqcommon.kafka.handler.EventMessageHandler;

/**
 * Created by wanfu on 2017/6/13.
 */
public class OrderHandler implements EventMessageHandler {

    private static Logger log = LoggerFactory.getLogger(OrderHandler.class);

    @Override
    public void execute(Event event) {
        Object result = event.getEvent();
        log.info("result:" + event.toString() + ";time:" + System.currentTimeMillis() * 1000);
    }
}
