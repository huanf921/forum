package top.vs.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ForumIdentificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumIdentificationApplication.class, args);
    }

}
