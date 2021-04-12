package top.vs.forum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.po.Activity;
import top.vs.forum.po.Message;
import top.vs.forum.service.ActivityService;
import top.vs.forum.service.MessageService;
import top.vs.forum.service.SubscribeService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 用户个人服务开放控制器
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@RestController
@Slf4j
public class PersonalOpenController {

    @Autowired
    private SubscribeService subscribeService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private MessageService messageService;

    /**
     * 为粉丝新增动态
     * @return
     */
    @PostMapping("/save/fan/activity")
    public ResultDTO saveFanActivity(@RequestBody Activity activity) {
        try {
            activity.setTime(LocalDateTime.now());
            activity.setReaded(0);
            log.info(activity.toString());
            activityService.save(activity);
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 为操作（关注、点赞、回复）对象用户新增消息
     */
    @PostMapping("/save/to/user/message")
    public ResultDTO saveToUserMessage(@RequestBody Message message) {
        try {
            message.setTime(LocalDateTime.now());
            message.setReaded(0);
            messageService.save(message);
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 返回粉丝id列表
     * @param userId
     * @return
     */
    @GetMapping("/get/user/fans/{userId}")
    public ResultDTO<List<Integer>> getUserFans(@PathVariable("userId") Integer userId) {
        try {
            List<Integer> res = subscribeService.getUserFans(userId);
            return ResultDTO.ok(res);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }
}
