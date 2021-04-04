package top.vs.forum.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.vs.forum.api.ForumIdentFeignClient;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.po.User;
import top.vs.forum.service.UserDetailService;
import top.vs.forum.vo.UserInfoVO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PortalInfoTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private ForumIdentFeignClient forumIdentFeignClient;

    /**
     * 每隔30分钟将粉丝数、周访问量、总访问量从缓存刷到数据库
     */
    @Scheduled(cron = "0 */30 * * * ?")
    public void updUserSimpleInfo2DbFromRedis() {
        // log.info("测试增加第三名用户的人气 -- 定时执行");
        // redisTemplate.opsForZSet().incrementScore("user:week:rank", 1327685513, 1);

    }

    /**
     * 每隔一小时更新首页的周人气用户(需新增用户时进行缓存维护)
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 1 * 60 * 60 * 1000)
    public void updWeekUserInfoAtRedis() {
        // log.info("更新首页的周人气用户 -- 任务被启动时/定时执行");
        // 周人气榜信息缓存
        Set<ZSetOperations.TypedTuple<Object>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores("user:week:rank", 0, 3);
        List<UserInfoVO> weekHotUsers = tuples.stream().map(tuple -> {

            UserInfoVO userInfoVO = new UserInfoVO();

            // 1、获取用户id
            Integer userId = (Integer) tuple.getValue();

            // 2、获取用户周访问量（周人气）
            double score = tuple.getScore();
            int weekVisits = (int) score;

            // 3、获取用户名
            ResultDTO<User> userBaseInfoRes = forumIdentFeignClient.getUserBaseInfo(userId);
            if (!userBaseInfoRes.getCode().equals("200")) {
                throw new RuntimeException(userBaseInfoRes.getMessage());
            }
            User userBaseInfo = userBaseInfoRes.getData();
            String userName = userBaseInfo.getName();

            // 4、获取用户头像地址
            String headUrl = userDetailService.getUserHeadUrlById(userId);

            // 5、封装返回
            userInfoVO.setUserId(userId);
            userInfoVO.setHeadUrl(headUrl);
            userInfoVO.setName(userName);
            userInfoVO.setWeekVisits(weekVisits);
            return userInfoVO;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set("weekHotUsers", weekHotUsers);
    }

    /**
     * 每隔12小时更新周热帖信息（根据回复数排名前十）
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 12 * 60 * 60 * 1000)
    public void updWeekPostInfo2RedisFromDb() {
        // log.info("更新周热帖信息 -- 任务定时执行");
        // 热帖相关信息
    }
}
