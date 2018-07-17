package win.leizhang.mqcommon.kafka.test.core;

import org.junit.Test;
import win.leizhang.mqcommon.kafka.core.StringProducer;

/**
 * Created by zealous on 2018/7/17.
 */
public class TestStringProducer {

    @Test
    public void test1() {
        StringProducer producer = StringProducer.getStringProducer();

        for (int i = 0; i < 10; i++) {
            String data = "hellow world " + i;
            producer.send("str-test", data);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String trace = "[{\"avgDuration\":11.0,\"name\":\"com.crt.member.serviceapi.facade.memberbaseinfo.MemberBaseInfoServiceFacade.getMemberInfoByToken\",\"qps\":10,\"createTime\":";
        try {
            System.out.println("begin");
            for (int i = 0; i > -1; i++) {
                long createTime = System.currentTimeMillis();
                producer.send("cep", trace + "" + createTime + "}]");
                System.out.println(i);
            }
            System.out.println("end");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // 异常处理
        try {
            try {
                System.in.read();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            producer.close();
        }

    }

    @Test
    public void test2() {
        StringProducer producer = StringProducer.getStringProducer();

        StringBuffer msg = new StringBuffer();
        for (int i = 0; i < 999941; i++) {
            msg.append("a");

/*
            //生产端
            // cts mqProvider start
            String clientTraceToken = MqTraceTrack.cs(MemberApplication.APPLICATION_NAME, topic);
            StringBuilder sb = new StringBuilder();
            sb.append(clientTraceToken).append(CTS_SPLIT_CLIENT).append(str);
            str = sb.toString();

            // 业务代码1

            // cts mqProvider end
            MqTraceTrack.cr(clientTraceToken);


            //消费端
            // cts mqConsumer start
            String[] strs = str.split(CTS_SPLIT_CLIENT);
            String serverTraceToken = MqTraceTrack.sr(strs[0], MemberApplication.APPLICATION_NAME, record.topic());
            str = strs[1];

            // 业务代码2

            // cts mqConsumer end
            MqTraceTrack.ss(serverTraceToken);
*/

        }

        String json = "[{\"annotations\":[{\"endpoint\":{\"ipv4\":\"10.0.55.116\",\"port\":80,\"serviceName\":\"api.hfive.homepage.index\"},\"timestamp\":\"1528964967870000\",\"value\":\"cs\"},{\"endpoint\":{\"ipv4\":\"10.0.55.116\",\"port\":80,\"serviceName\":\"api.hfive.homepage.index\"},\"timestamp\":\"1528964967892000\",\"value\":\"cr\"}],\"binaryAnnotations\":[{\"endpoint\":{\"ipv4\":\"10.0.55.116\",\"port\":80,\"serviceName\":\"api.hfive.homepage.index\"},\"key\":\"clientAppName\",\"value\":\"hrtds.php\"}],\"debug\":false,\"duration\":22000,\"id\":\"6a386087ed0544d89919daec9f798ddd\",\"name\":\"queryProfile\",\"parentId\":\"6a386087ed0544d89919daec9f798ffe\",\"timestamp\":\"1528964967870000\",\"traceId\":\"05d1c48b79474e8dab62dad617d9f159\"}]";
        producer.send("zhang3", json);

        // 异常处理
        try {
            try {
                System.in.read();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            producer.close();
        }

    }
}
