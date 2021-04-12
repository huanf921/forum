package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.vs.forum.api.ForumIdentFeignClient;
import top.vs.forum.api.ForumPostFeignClient;
import top.vs.forum.api.ForumUserFeignClient;
import top.vs.forum.mapper.ActivityMapper;
import top.vs.forum.po.Activity;
import top.vs.forum.po.Subscribe;
import top.vs.forum.service.ActivityService;
import top.vs.forum.service.SubscribeService;
import top.vs.forum.service.UserDetailService;
import top.vs.forum.vo.SubscribeActivityVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 动态表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Autowired
    private ForumIdentFeignClient forumIdentFeignClient;

    @Autowired
    private ForumPostFeignClient forumPostFeignClient;

    @Autowired
    private ForumUserFeignClient forumUserFeignClient;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private SubscribeService subscribeService;

    @Override
    public List<SubscribeActivityVO> getUserSubscribeActivities(Integer userId) {
        return this.list(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getUserId, userId)
                .orderByDesc(Activity::getTime))
                .stream()
                .map(activity -> {
                    SubscribeActivityVO subscribeActivityVO = new SubscribeActivityVO();
                    BeanUtils.copyProperties(activity, subscribeActivityVO);

                    // 获取关注者
                    Integer subscribeId = activity.getSubscribeId();
                    String subscribeName = forumIdentFeignClient.getUserBaseInfo(subscribeId).getData().getName();
                    subscribeActivityVO.setSubscribeName(subscribeName);
                    String subscribeHeadUrl = userDetailService.getUserHeadUrlById(subscribeId);
                    subscribeActivityVO.setSubscribeHeadUrl(subscribeHeadUrl);

                    // 判断类型决定获取帖子/被关注者信息
                    Integer type = activity.getType();
                    if (type.equals(4)) {
                        Integer toSubscribeId = activity.getToSubscribeId();
                        String toSubscribeName = forumIdentFeignClient.getUserBaseInfo(toSubscribeId).getData().getName();
                        subscribeActivityVO.setToSubscribeName(toSubscribeName);
                        String toSubscribeHeadUrl = userDetailService.getUserHeadUrlById(toSubscribeId);
                        subscribeActivityVO.setToSubscribeHeadUrl(toSubscribeHeadUrl);
                    } else {
                        String title = forumPostFeignClient.getPostBriefInfo(activity.getPostId()).getData().getTitle();
                        subscribeActivityVO.setTitle(title);
                    }

                    return subscribeActivityVO;
                }).collect(Collectors.toList());
    }

    @Override
    public void saveFanActivity(Subscribe subscribe) {
        List<Integer> fanUserIds = subscribeService.list(new LambdaQueryWrapper<Subscribe>()
                .eq(Subscribe::getSubscribeId, subscribe.getUserId()))
                .stream()
                .map(Subscribe::getUserId)
                .collect(Collectors.toList());

        fanUserIds.forEach(fanUserId -> {
            Activity activity = new Activity();
            activity.setType(4);
            activity.setUserId(fanUserId);
            activity.setSubscribeId(subscribe.getUserId());
            activity.setToSubscribeId(subscribe.getSubscribeId());
            forumUserFeignClient.saveFanActivity(activity);
        });
    }
}
