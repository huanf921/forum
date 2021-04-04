package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.vs.forum.api.ForumIdentFeignClient;
import top.vs.forum.dto.UserBriefInfoDTO;
import top.vs.forum.dto.UserDetailInfoDTO;
import top.vs.forum.dto.UserSimpleDTO;
import top.vs.forum.dto.UserSimpleRedisDTO;
import top.vs.forum.mapper.UserDetailMapper;
import top.vs.forum.po.User;
import top.vs.forum.po.UserAlbum;
import top.vs.forum.po.UserDetail;
import top.vs.forum.service.UserAlbumService;
import top.vs.forum.service.UserDetailService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户详情表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class UserDetailServiceImpl extends ServiceImpl<UserDetailMapper, UserDetail> implements UserDetailService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ForumIdentFeignClient forumIdentFeignClient;

    @Autowired
    private UserAlbumService userAlbumService;

    @Override
    public UserSimpleDTO getUserSimpleInfoById(Integer userId) {
        UserSimpleDTO userSimpleDTO = new UserSimpleDTO();

        UserSimpleRedisDTO userSimpleRedisDTO =
                (UserSimpleRedisDTO) redisTemplate.opsForHash().get("user:simple:info", userId);
        BeanUtils.copyProperties(userSimpleRedisDTO, userSimpleDTO);
        return userSimpleDTO;
    }

    @Override
    public List<UserDetail> listUserSimples() {
        // 粉丝数、总访问量、周访问量
        LambdaQueryWrapper<UserDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(UserDetail::getUserId, UserDetail::getFans,
                UserDetail::getAllVisits, UserDetail::getWeekVisits);

        return this.list(queryWrapper);
    }

    @Override
    public String getUserHeadUrlById(Integer userId) {
        UserDetail userDetail = this.getOne(new LambdaQueryWrapper<UserDetail>()
                // .select(UserDetail::getHeadUrl)
                .eq(UserDetail::getUserId, userId));
        return userDetail.getHeadUrl();
    }

    @Override
    public UserBriefInfoDTO getUserBriefInfo(Integer userId) {
        UserBriefInfoDTO userBriefInfoDTO = new UserBriefInfoDTO();
        UserDetailInfoDTO userInfo = getUserInfo(userId, true);
        BeanUtils.copyProperties(userInfo, userBriefInfoDTO);

        return userBriefInfoDTO;
    }

    @Override
    public UserDetailInfoDTO getUserDetailInfo(Integer userId) {
        return getUserInfo(userId, false);
    }

    /**
     * 封装复用方法
     * @param userId
     * @return
     */
    private UserDetailInfoDTO getUserInfo(Integer userId, boolean isBrief) {
        UserDetailInfoDTO userDetailInfoDTO = new UserDetailInfoDTO();
        // 1、远程获取用户名
        User user = forumIdentFeignClient.getUserBaseInfo(userId).getData();
        userDetailInfoDTO.setName(user.getName());
        if (isBrief) {
            // 2、数据库获取头像+简介
            UserDetail userDetail = this.getOne(new LambdaQueryWrapper<UserDetail>()
                    .select(UserDetail::getHeadUrl, UserDetail::getIntroduction)
                    .eq(UserDetail::getUserId, userId));
            userDetailInfoDTO.setHeadUrl(userDetail.getHeadUrl());
            userDetailInfoDTO.setIntroduction(userDetail.getIntroduction());
        } else {
            // 2、数据库获取用户详细信息以及相册图片地址列表
            UserDetail userDetail = this.getById(userId);
            BeanUtils.copyProperties(userDetail, userDetailInfoDTO);
            List<String> albumUrls = userAlbumService.list(new LambdaQueryWrapper<UserAlbum>()
                    .select(UserAlbum::getPicUrl)
                    .eq(UserAlbum::getUserId, userId))
                    .stream()
                    .map(UserAlbum::getPicUrl)
                    .collect(Collectors.toList());
            userDetailInfoDTO.setAlbumUrls(albumUrls);
        }
        // 3、缓存获取两排名+总访问量+粉丝数
        UserSimpleRedisDTO userSimpleRedisDTO =
                (UserSimpleRedisDTO) redisTemplate.opsForHash().get("user:simple:info", userId);
        Long allRanking = redisTemplate.opsForZSet().reverseRank("user:all:rank", userId);
        Long weekRanking = redisTemplate.opsForZSet().reverseRank("user:week:rank", userId);

        BeanUtils.copyProperties(userSimpleRedisDTO, userDetailInfoDTO);
        userDetailInfoDTO.setAllRanking(allRanking);
        userDetailInfoDTO.setWeekRanking(weekRanking);

        return userDetailInfoDTO;
    }
}
