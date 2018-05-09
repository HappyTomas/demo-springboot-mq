package win.leizhang.demo.springboot.mq.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;
import win.leizhang.demo.springboot.mq.service.KafkaSender;

@SpringBootApplication(scanBasePackages = "win.leizhang.demo.springboot.mq")
//@SpringBootApplication
//@ImportResource({"classpath:spring/applicationContext.xml"})
public class Application {

    public static void main(String[] args) {

        // 写log4j用
        //System.setProperty("crtDubboPort", dubboPort);

        // fluent API 方式
/*
        new SpringApplicationBuilder()
                .sources(Application.class)
                .bannerMode(Banner.Mode.OFF)
                .web(false)
                .registerShutdownHook(true)
                .run(args);
*/

        // simple方式
        //SpringApplication.run(Application.class, args);


        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        KafkaSender sender = context.getBean(KafkaSender.class);

        for (int i = 0; i < 2; i++) {
            //调用消息发送类中的消息发送方法
            sender.send();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
