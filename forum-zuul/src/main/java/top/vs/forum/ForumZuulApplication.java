package top.vs.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class ForumZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumZuulApplication.class, args);
    }

}
