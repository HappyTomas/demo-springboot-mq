package win.leizhang.mqcommon.kafka.utils;

import org.nustaq.serialization.FSTConfiguration;

/**
 * Created by wanfu on 2017/6/13.
 */
public class FstUtil {

    private static FSTConfiguration fstConfiguration = FSTConfiguration.createStructConfiguration();

    public static byte[] serialize(Object obj) {
        return fstConfiguration.asByteArray(obj);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Object obj = fstConfiguration.asObject(bytes);
        return (T) obj;
    }

    public static Object deserializa(byte[] bytes) {
        return fstConfiguration.asObject(bytes);
    }


}
