package win.leizhang.demo.mq.rabbitmq.test.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import win.leizhang.demo.mq.rabbitmq.mq.RabbitmqSender;
import win.leizhang.demo.mq.rabbitmq.test.BaseTestCase;

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 压测，生产者方法
 * Created by zealous on 2018/8/2.
 */
public class TestQueueFactory extends BaseTestCase {

    @Autowired
    private RabbitmqSender sender;

    @PostConstruct
    public void initStt() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int threads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executorService.execute(() -> {
                try {
                    start.await();
                    for (int j = 0; j < 1000; j++) {
                        sender.sendDirect("demo", "发送消息----demo-----" + j);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    end.countDown();
                }
            });
        }
        start.countDown();
        try {
            end.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        stopWatch.stop();
        log.info("发送消息耗时：{}ms", stopWatch.getTotalTimeMillis());
    }

    // 消息过多会造成服务端crash，客户端异常是org.springframework.amqp.AmqpIOException: java.net.SocketException: Broken pipe (Write failed)

}
