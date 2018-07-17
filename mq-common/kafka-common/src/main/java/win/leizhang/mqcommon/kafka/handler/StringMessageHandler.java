package win.leizhang.mqcommon.kafka.handler;

/**
 * Created by wanfu on 2017/6/13.
 */
public interface StringMessageHandler {

    /**
     * 消息处理
     *
     * @param data 数据对象
     */
    void execute(String data);

}
