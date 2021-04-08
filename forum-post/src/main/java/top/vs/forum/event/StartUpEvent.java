package top.vs.forum.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.vs.forum.po.Post;
import top.vs.forum.service.PostService;

import java.util.List;

/**
 * 启动时事件
 */
@Component
@Slf4j
public class StartUpEvent implements ApplicationRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PostService postService;

    /**
     * 启动时进行论帖回复数排行初始化工作
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1、查询所有论帖id及其回复数
        List<Post> posts = postService.listPostReplies();
        // 2、创建论帖回复数排行榜
        posts.forEach(post -> {
            Long postId = post.getId();
            Integer replies = post.getReplies();
            redisTemplate.opsForZSet().add("post:week:rank", postId, replies);
        });
    }
}
