package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.vs.forum.api.ForumIdentFeignClient;
import top.vs.forum.api.ForumPostFeignClient;
import top.vs.forum.api.ForumUserFeignClient;
import top.vs.forum.dto.UserPostBriefInfoDTO;
import top.vs.forum.mapper.StarMapper;
import top.vs.forum.po.Activity;
import top.vs.forum.po.Post;
import top.vs.forum.po.Star;
import top.vs.forum.service.StarService;
import top.vs.forum.service.UserDetailService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 收藏表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class StarServiceImpl extends ServiceImpl<StarMapper, Star> implements StarService {

    @Autowired
    private ForumPostFeignClient forumPostFeignClient;

    @Autowired
    private ForumIdentFeignClient forumIdentFeignClient;

    @Autowired
    private ForumUserFeignClient forumUserFeignClient;

    @Autowired
    private UserDetailService userDetailService;

    @Override
    public List<UserPostBriefInfoDTO> listStarPosts(Integer userId) {
        List<UserPostBriefInfoDTO> starPosts = new ArrayList<>();
        this.list(new LambdaQueryWrapper<Star>().eq(Star::getUserId, userId))
                .stream()
                .map(Star::getStarId)
                .collect(Collectors.toList())
                .forEach(starPostId -> {
                    UserPostBriefInfoDTO userPostBriefInfoDTO = new UserPostBriefInfoDTO();
                    userPostBriefInfoDTO.setPostId(starPostId);

                    Post post = forumPostFeignClient.getPostBriefInfo(starPostId).getData();
                    BeanUtils.copyProperties(post, userPostBriefInfoDTO);

                    Integer posterId = post.getUserId();
                    userPostBriefInfoDTO.setUserId(posterId);
                    userPostBriefInfoDTO.setName(forumIdentFeignClient.getUserBaseInfo(posterId)
                            .getData().getName());
                    userPostBriefInfoDTO.setHeadUrl(userDetailService.getUserHeadUrlById(posterId));

                    starPosts.add(userPostBriefInfoDTO);
                });

        return starPosts;
    }

    @Override
    public void saveFanActivity(Integer userId, Long starId) {
        List<Integer> fanIds = forumUserFeignClient.getUserFans(userId).getData();
        String s = forumUserFeignClient.getUserFans(userId).getCode() + " " + forumUserFeignClient.getUserFans(userId).getMessage();
        // log.warn(s);
        // log.warn(fanIds.toString());
        fanIds.forEach(fanId -> {
            Activity activity = new Activity();
            activity.setUserId(fanId);
            activity.setType(2);
            activity.setSubscribeId(userId);
            activity.setPostId(starId);
            // log.warn(activity.toString());
            forumUserFeignClient.saveFanActivity(activity);
        });
    }
}
