package win.leizhang.mqcommon.kafka.serialization;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.leizhang.mqcommon.kafka.core.Event;
import win.leizhang.mqcommon.kafka.utils.FstUtil;

import java.util.Map;

/**
 * Created by wanfu on 2017/6/13.
 */
public class FstDeserializer implements Deserializer<Event> {

    private static Logger logger = LoggerFactory.getLogger(FstDeserializer.class);

    @Override
    public void configure(Map<String, ?> map, boolean b) {
    }

    @Override
    public Event deserialize(String topic, byte[] data) {
        Event event = null;
        try {
            event = FstUtil.deserialize(data, Event.class);
        } catch (Exception e) {
            logger.error("fst deserializer error, the topic is {}", topic);
        }
        return event;
    }

    @Override
    public void close() {
    }
}
