package win.leizhang.demo.springboot.mq.rocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:spring/applicationContext.xml"})
public class Application {

    public static void main(String[] args) {

        // fluent API 方式
/*
        new SpringApplicationBuilder()
                .sources(Application.class)
                .registerShutdownHook(true)
                .run(args);
*/

        // simple方式
        SpringApplication.run(Application.class, args);

    }

}
