package top.vs.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ForumUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumUserApplication.class, args);
    }

}
