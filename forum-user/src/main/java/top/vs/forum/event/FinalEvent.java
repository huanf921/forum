package top.vs.forum.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.vs.forum.task.PortalUserInfoTask;

import javax.annotation.PreDestroy;

/**
 * user工程销毁时执行事件
 */
@Component
@Slf4j
public class FinalEvent{

    /**
     * 进行缓存刷库，避免项目关闭时未到定时器时间，导致缓存与数据库不一致
     */
    @PreDestroy
    public void updDB() {
        log.info("Servlet被彻底卸载之前...");
        new PortalUserInfoTask().updUserSimpleInfo2DbFromRedis();
    }
}
