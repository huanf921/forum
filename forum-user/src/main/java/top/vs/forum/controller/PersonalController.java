package top.vs.forum.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.dto.UserPostBriefInfoDTO;
import top.vs.forum.po.Activity;
import top.vs.forum.po.Message;
import top.vs.forum.po.Star;
import top.vs.forum.po.Subscribe;
import top.vs.forum.service.ActivityService;
import top.vs.forum.service.MessageService;
import top.vs.forum.service.StarService;
import top.vs.forum.service.SubscribeService;
import top.vs.forum.vo.PersonalMessageVO;
import top.vs.forum.vo.SubscribeActivityVO;
import top.vs.forum.vo.SubscribeUserVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户个人服务控制器
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Controller
@RequestMapping("/personal")
public class PersonalController {

    @Autowired
    private SubscribeService subscribeService;

    @Autowired
    private StarService starService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private MessageService messageService;

    /**
     * 获取用户所关注的所有用户
     * @param userId
     * @return
     */
    @GetMapping("/get/user/all/subscribes/{userId}")
    @ResponseBody
    public ResultDTO<List<SubscribeUserVO>> getUserAllSubscribes(@PathVariable("userId") Integer userId) {
        try {
            List<SubscribeUserVO> subscribeUsers = subscribeService.listSubUsers(userId);
            return ResultDTO.ok(subscribeUsers);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 获取用户所关注的用户id
     * @param userId
     * @return
     */
    @GetMapping("/get/user/subscribes/{userId}")
    @ResponseBody
    public ResultDTO<List<Integer>> getUserSubscribes(@PathVariable("userId") Integer userId) {
        try {
            List<Integer> subscribeIds = subscribeService.list(new LambdaQueryWrapper<Subscribe>()
                    .eq(Subscribe::getUserId, userId))
                    .stream()
                    .map(Subscribe::getSubscribeId)
                    .collect(Collectors.toList());
            return ResultDTO.ok(subscribeIds);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 用户进行关注
     * @return
     */
    @PostMapping("/save/user/subscribes")
    @ResponseBody
    public ResultDTO saveUserSubscribes(Subscribe subscribe) {
        try {
            // 为关注该用户的粉丝新增动态
            activityService.saveFanActivity(subscribe);

            // 为关注对象新增消息
            messageService.saveToUserMessage(subscribe);

            // 对缓存中粉丝数进行维护
            subscribeService.updRedisUserSimpleInfo(subscribe.getSubscribeId());

            subscribeService.save(subscribe);
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 用户取消关注
     * @return
     */
    @PostMapping("/rem/user/subscribes")
    @ResponseBody
    public ResultDTO remUserSubscribes(Subscribe subscribe) {
        try {
            // 对缓存中粉丝数进行维护
            subscribeService.reduceRedisUserSimpleInfo(subscribe.getSubscribeId());

            subscribeService.remove(new LambdaQueryWrapper<Subscribe>()
                    .eq(Subscribe::getUserId, subscribe.getUserId())
                    .eq(Subscribe::getSubscribeId, subscribe.getSubscribeId()));
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 获取用户收藏的所有帖子
     * @return
     */
    @GetMapping("/get/star/posts/{userId}")
    @ResponseBody
    public ResultDTO<List<UserPostBriefInfoDTO>> getStarPosts(@PathVariable("userId") Integer userId) {
        try {
            List<UserPostBriefInfoDTO> starPosts = starService.listStarPosts(userId);
            return ResultDTO.ok(starPosts);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 获取用户是否收藏了帖子
     * @return
     */
    @PostMapping("/has/star/post")
    @ResponseBody
    public ResultDTO<Boolean> hasStar(Star star) {
        try {
            return ResultDTO.ok(starService.getOne(new LambdaQueryWrapper<Star>()
                            .eq(Star::getUserId, star.getUserId())
                            .eq(Star::getStarId, star.getStarId())) == null);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 用户进行收藏
     * @return
     */
    @PostMapping("/save/user/star")
    @ResponseBody
    public ResultDTO saveUserStar(Star star) {
        try {
            // 为粉丝新增收藏帖子动态
            starService.saveFanActivity(star.getUserId(), star.getStarId());

            starService.save(star);
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 用户取消收藏
     * @return
     */
    @PostMapping("/rem/user/star")
    @ResponseBody
    public ResultDTO remUserStar(Star star) {
        try {
            starService.remove(new LambdaQueryWrapper<Star>()
                    .eq(Star::getUserId,star.getUserId())
                    .eq(Star::getStarId, star.getStarId()));
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 查询用户订阅（关注）的用户动态
     * @param userId
     * @return
     */
    @GetMapping("/get/user/subscribe/activities/{userId}")
    @ResponseBody
    public ResultDTO<List<SubscribeActivityVO>> getUserSubscribeActivities(@PathVariable("userId") Integer userId){
        try {
            List<SubscribeActivityVO> subscribeActivityVOS = activityService.getUserSubscribeActivities(userId);
            return ResultDTO.ok(subscribeActivityVOS);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 查询用户个人消息
     * @param userId
     * @return
     */
    @GetMapping("/get/user/personal/messages/{userId}")
    @ResponseBody
    public ResultDTO<List<PersonalMessageVO>> getUserPersonalMessages(@PathVariable("userId") Integer userId){
        try {
            List<PersonalMessageVO> personalMessageVOS = messageService.getUserPersonalMessages(userId);
            return ResultDTO.ok(personalMessageVOS);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 获取用户未读的动态和消息数
     * @param userId
     * @return
     */
    @GetMapping("/get/user/unread/{userId}")
    @ResponseBody
    public ResultDTO<Pair<Integer, Integer>> getUserUnread(@PathVariable("userId") Integer userId) {
        try {
            Pair<Integer, Integer> unreadPair = new Pair<>(
                    activityService.count(new LambdaQueryWrapper<Activity>()
                            .eq(Activity::getUserId, userId)
                            .eq(Activity::getReaded, 0)),
                    messageService.count(new LambdaQueryWrapper<Message>()
                            .eq(Message::getUserId, userId)
                            .eq(Message::getReaded, 0))
            );
            return ResultDTO.ok(unreadPair);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 全部标为已读--动态
     * @param userId
     * @return
     */
    @GetMapping("/save/user/act/read/{userId}")
    @ResponseBody
    public ResultDTO saveUserActRead(@PathVariable("userId") Integer userId) {
        try {
            Activity activity = new Activity();
            activity.setReaded(1);
            activityService.update(activity, new LambdaQueryWrapper<Activity>().eq(Activity::getUserId, userId));
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 全部标为已读--消息
     * @param userId
     * @return
     */
    @GetMapping("/save/user/msg/read/{userId}")
    @ResponseBody
    public ResultDTO saveUserMsgRead(@PathVariable("userId") Integer userId) {
        try {
            Message message = new Message();
            message.setReaded(1);
            messageService.update(message, new LambdaQueryWrapper<Message>().eq(Message::getUserId, userId));
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }
}
