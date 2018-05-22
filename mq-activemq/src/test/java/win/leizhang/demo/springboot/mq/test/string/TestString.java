package win.leizhang.demo.springboot.mq.test.string;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

public class TestString {

    @Test
    public void testJson() {
        String str = JSON.toJSONString(null);
        System.out.println(str);
    }

}
