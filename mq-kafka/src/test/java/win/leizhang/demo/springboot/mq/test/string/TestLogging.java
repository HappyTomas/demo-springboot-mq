package win.leizhang.demo.springboot.mq.test.string;

import org.junit.Test;
import win.leizhang.demo.springboot.mq.test.BaseTestCase;

/**
 * Created by zealous on 2018/6/28.
 */

public class TestLogging extends BaseTestCase {

    @Test
    public void test1() {
        log.trace("这个trace日志...");
        log.debug("这个debug日志...");
        log.info("这个info日志...");
        log.warn("这个warn日志...");
        log.error("这个error日志...");
    }

}
