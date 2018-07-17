package win.leizhang.mqcommon.kafka.handler;

import win.leizhang.mqcommon.kafka.core.Event;

/**
 * Created by wanfu on 2017/6/13.
 */
public interface EventMessageHandler {

    /**
     * 消息处理
     *
     * @param event 事件对象
     */
    void execute(Event event);

}
