package win.leizhang.demo.springboot.mq.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages = "win.leizhang.demo.springboot.mq")
@ImportResource({"classpath:spring/applicationContext.xml"})
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
        SpringApplication.run(Application.class, args);

    }

}
