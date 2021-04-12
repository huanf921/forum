package top.vs.forum.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.vs.forum.task.PortalPostInfoTask;

/**
 * post工程销毁时执行事件
 */
@Component
@Slf4j
public class DestroyEvent implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 进行缓存刷库，避免项目关闭时未到定时器时间，导致缓存与数据库不一致
     * @param contextClosedEvent
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        log.info("容器正在关闭...");
        new PortalPostInfoTask().updPostReplies2DbFromRedis();
        log.info(redisTemplate.opsForZSet().rangeWithScores("post:week:rank",0, -1).toString());
    }
}
