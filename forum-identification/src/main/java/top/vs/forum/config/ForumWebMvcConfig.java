package top.vs.forum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ForumWebMvcConfig implements WebMvcConfigurer {

    @Value("${file.album.upload}")
    private String albumUploadPath;

    @Value("${file.post.file}")
    private String postFilePath;
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/ident/login/page").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/album/**").addResourceLocations("file:" + albumUploadPath);
        registry.addResourceHandler("/post-file/**").addResourceLocations("file:" + postFilePath);
    }

}
