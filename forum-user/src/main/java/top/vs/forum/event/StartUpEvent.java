package top.vs.forum.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.vs.forum.dto.UserSimpleRedisDTO;
import top.vs.forum.po.UserDetail;
import top.vs.forum.service.UserDetailService;

import java.util.List;

/**
 * 启动时事件
 */
@Component
@Slf4j
public class StartUpEvent implements ApplicationRunner {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserDetailService userDetailService;

    /**
     * 启动时进行系统常用的用户初始数据缓存操作
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // log.info("启动时进行系统常用的用户初始数据缓存操作 -- 是否先于 @PostConstruct 注解执行？");
        // 粉丝数、总访问量、周访问量
        // 1、查询所有用户相关的上述信息
        List<UserDetail> userSimples = userDetailService.listUserSimples();
        userSimples.forEach(userDetail -> {
            // 2、将信息存到redis
            Integer userId = userDetail.getUserId();
            UserSimpleRedisDTO userSimpleRedisDTO = new UserSimpleRedisDTO();
            BeanUtils.copyProperties(userDetail, userSimpleRedisDTO);
            redisTemplate.opsForHash().put("user:simple:info", userId, userSimpleRedisDTO);

            // 3、redis存储周榜总榜排名
            Integer weekVisits = userDetail.getWeekVisits();
            Integer allVisits = userDetail.getAllVisits();
            redisTemplate.opsForZSet().add("user:week:rank", userId, Double.valueOf(weekVisits));
            redisTemplate.opsForZSet().add("user:all:rank", userId, Double.valueOf(allVisits));
        });

    }
}
