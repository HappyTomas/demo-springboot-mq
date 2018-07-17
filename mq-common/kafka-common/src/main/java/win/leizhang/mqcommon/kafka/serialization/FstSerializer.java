package win.leizhang.mqcommon.kafka.serialization;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.leizhang.mqcommon.kafka.utils.FstUtil;

import java.util.Map;

/**
 * Created by wanfu on 2017/6/13.
 */
public class FstSerializer implements Serializer<Object> {

    private static Logger logger = LoggerFactory.getLogger(FstSerializer.class);

    @Override
    public void configure(Map<String, ?> map, boolean b) {
    }

    @Override
    public byte[] serialize(String topic, Object event) {
        try {
            return FstUtil.serialize(event);
        } catch (Exception e) {
            logger.error("fst serializer error, the topic is {}", topic);
        }
        return null;
    }

    @Override
    public void close() {
    }

}
