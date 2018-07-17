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
        /*for (int i = 0; i < 10; i++) {
            String data = "hellow world " + i;
            producer.send("str-test", data);
            try {d
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        /*String trace = "[{\"avgDuration\":11.0,\"name\":\"com.crt.member.serviceapi.facade.memberbaseinfo.MemberBaseInfoServiceFacade.getMemberInfoByToken\",\"qps\":10,\"createTime\":";
        try{
            System.out.println("begin");
            for(int i=0;i>-1;i++){
                long createTime = System.currentTimeMillis();
                producer.send("cep", trace + "" + createTime + "}]");
                System.out.println(i);
            }
            System.out.println("end");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }*/
        StringBuffer msg = new StringBuffer();
        for (int i = 0; i < 999941; i++) {
            msg.append("a");
            //——————————————————发送消息前埋点————————————————————
            //1，发送消息前调用cs方法埋点并返回clientTraceToken，第一个参数为应用名称，第二个参数为topic名称
            //注意：clientTraceToken需要需要设置到业务消息体的头部，
            //当消费者订阅topic后，需要从头部取出clientTraceToken，然后调用sr ss方法进行消费端埋点

/*
            String clientTraceToken = MqTraceTrack.cs("crm","akka");
            producer.send("akka","message" + clientTraceToken);
            //2，发送消息完毕调用cr方法，传入cs方法返回跌clientTraceToken
            MqTraceTrack.cr(clientTraceToken);

            //————————————————————接收消息后埋点————————————————————
            //3，接收到topic的消息后，从消息头部取出clientTraceToken，调用sr方法埋点，第一个参数为mq头部的clientTraceToken，第二个参数为当前应用名称，第3个参数为topic名称
            String serverTraceToken = MqTraceTrack.sr("clientTraceToken","crm-consumer","akka");
            //业务代码执行
            //……………………
            //4，发送消息完毕调用ss方法，传入sr方法返回跌serverTraceToken
            MqTraceTrack.ss(serverTraceToken);
*/


        }
        String json = "[{\"annotations\":[{\"endpoint\":{\"ipv4\":\"10.0.55.116\",\"port\":80,\"serviceName\":\"api.hfive.homepage.index\"},\"timestamp\":\"1528964967870000\",\"value\":\"cs\"},{\"endpoint\":{\"ipv4\":\"10.0.55.116\",\"port\":80,\"serviceName\":\"api.hfive.homepage.index\"},\"timestamp\":\"1528964967892000\",\"value\":\"cr\"}],\"binaryAnnotations\":[{\"endpoint\":{\"ipv4\":\"10.0.55.116\",\"port\":80,\"serviceName\":\"api.hfive.homepage.index\"},\"key\":\"clientAppName\",\"value\":\"hrtds.php\"}],\"debug\":false,\"duration\":22000,\"id\":\"6a386087ed0544d89919daec9f798ddd\",\"name\":\"queryProfile\",\"parentId\":\"6a386087ed0544d89919daec9f798ffe\",\"timestamp\":\"1528964967870000\",\"traceId\":\"05d1c48b79474e8dab62dad617d9f159\"}]";
        producer.send("zipkin", json);

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
