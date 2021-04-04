package top.vs.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ForumEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumEurekaApplication.class, args);
    }

}
