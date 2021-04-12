package top.vs.forum.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ForumWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/manage/page").setViewName("user-manage");
        registry.addViewController("/personal/star/page").setViewName("star");
        registry.addViewController("/personal/activity/page").setViewName("activity");
        registry.addViewController("/personal/message/page").setViewName("message");
    }

}
