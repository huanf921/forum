package top.vs.forum.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.vs.forum.api.ForumIdentFeignClient;
import top.vs.forum.dto.PostRedisDTO;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.po.Post;
import top.vs.forum.po.User;
import top.vs.forum.service.PostService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PortalPostInfoTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PostService postService;

    @Autowired
    private ForumIdentFeignClient forumIdentFeignClient;

    /**
     * 每隔30分钟将帖子回复数从缓存刷到数据库
     */
    @Scheduled(cron = "0 */30 * * * ?")
    public void updPostReplies2DbFromRedis() {
        Set<ZSetOperations.TypedTuple<Object>> tuples =
                redisTemplate.opsForZSet().rangeWithScores("post:week:rank", 0, -1);
        tuples.forEach(tuple -> {
            Long postId = (Long) tuple.getValue();
            double score = tuple.getScore();
            int replies = (int) score;
            Post updPost = new Post();
            updPost.setId(postId);
            updPost.setReplies(replies);
            postService.updateById(updPost);
        });
    }

    /**
     * 每隔2小时更新周热帖信息
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 2 * 60 * 60 * 1000)
    public void updWeekPostInfoAtRedis() {
        // log.info("更新周热帖信息 -- 任务定时执行");
        // 热帖相关信息
        Set<ZSetOperations.TypedTuple<Object>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores("post:week:rank", 0, 14);
        List<PostRedisDTO> weekHotPosts = tuples.stream().map(tuple -> {
            PostRedisDTO postRedisDTO = new PostRedisDTO();

            // 获取帖子id和回复数
            Long postId = (Long) tuple.getValue();
            double score = tuple.getScore();
            int replies = (int) score;

            // 由id获取标题、类型及发表者id
            Post post = postService.getSimpleInfoById(postId);
            Integer userId = post.getUserId();
            String type = post.getType();
            String title = post.getTitle();

            // 远程调用获取用户名称
            ResultDTO<User> userBaseInfoRes = forumIdentFeignClient.getUserBaseInfo(userId);
            if (!userBaseInfoRes.getCode().equals("200")) {
                throw new RuntimeException(userBaseInfoRes.getMessage());
            }
            User userBaseInfo = userBaseInfoRes.getData();
            String name = userBaseInfo.getName();

            // 设置热帖实体对象并返回
            postRedisDTO.setPostId(postId);
            postRedisDTO.setTitle(title);
            postRedisDTO.setType(type);
            postRedisDTO.setName(name);
            postRedisDTO.setReplies(replies);

            return postRedisDTO;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set("weekHotPosts", weekHotPosts);
    }

    /**
     * 每周一维护周排行榜
     */
    @Scheduled(cron = "0 0 0 ? * MON")
    public void updWeekPostRankAtRedis() {
        // 将回复数清零
        Set<ZSetOperations.TypedTuple<Object>> tuples =
                redisTemplate.opsForZSet().rangeWithScores("post:week:rank", 0, -1);
        tuples.forEach(tuple -> {
            Integer postId = (Integer) tuple.getValue();
            redisTemplate.opsForZSet().add("post:week:rank", postId, 0);
        });
    }
}
