package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.vs.forum.api.ForumIdentFeignClient;
import top.vs.forum.dto.UserSimpleRedisDTO;
import top.vs.forum.mapper.SubscribeMapper;
import top.vs.forum.po.Subscribe;
import top.vs.forum.service.SubscribeService;
import top.vs.forum.service.UserDetailService;
import top.vs.forum.vo.SubscribeUserVO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 关注表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class SubscribeServiceImpl extends ServiceImpl<SubscribeMapper, Subscribe> implements SubscribeService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private ForumIdentFeignClient forumIdentFeignClient;

    @Override
    public void updRedisUserSimpleInfo(Integer userId) {
        UserSimpleRedisDTO userSimpleRedisDTO = (UserSimpleRedisDTO) redisTemplate.opsForHash().get("user:simple:info", userId);
        // 由于是反序列化出来的一个新对象，所以改变值之后还需要设置回去
        userSimpleRedisDTO.setFans(userSimpleRedisDTO.getFans() + 1);
        redisTemplate.opsForHash().put("user:simple:info", userId, userSimpleRedisDTO);
    }

    @Override
    public void reduceRedisUserSimpleInfo(Integer subscribeId) {
        UserSimpleRedisDTO userSimpleRedisDTO = (UserSimpleRedisDTO) redisTemplate.opsForHash().get("user:simple:info", subscribeId);
        // 由于是反序列化出来的一个新对象，所以改变值之后还需要设置回去
        userSimpleRedisDTO.setFans(userSimpleRedisDTO.getFans() - 1);
        redisTemplate.opsForHash().put("user:simple:info", subscribeId, userSimpleRedisDTO);
    }

    @Override
    public List<SubscribeUserVO> listSubUsers(Integer userId) {
        List<Integer> subUserIds = this.list(new LambdaQueryWrapper<Subscribe>().eq(Subscribe::getUserId, userId))
                .stream()
                .map(Subscribe::getSubscribeId)
                .collect(Collectors.toList());

        List<SubscribeUserVO> subUsers = new ArrayList<>();
        subUserIds.forEach(subUserId -> {
            SubscribeUserVO subscribeUserVO = new SubscribeUserVO();
            subscribeUserVO.setUserId(subUserId);

            // 名称
            String name = forumIdentFeignClient.getUserBaseInfo(subUserId).getData().getName();
            subscribeUserVO.setName(name);

            // 头像
            String headUrl = userDetailService.getUserHeadUrlById(subUserId);
            subscribeUserVO.setHeadUrl(headUrl);

            // 粉丝数
            Integer fans = ((UserSimpleRedisDTO) redisTemplate
                    .opsForHash()
                    .get("user:simple:info", subUserId))
                    .getFans();
            subscribeUserVO.setFans(fans);

            subUsers.add(subscribeUserVO);
        });

        return subUsers;
    }

    @Override
    public List<Integer> getUserFans(Integer userId) {
        return this.list(new LambdaQueryWrapper<Subscribe>().eq(Subscribe::getSubscribeId, userId))
                .stream().map(Subscribe::getUserId).collect(Collectors.toList());
    }
}
