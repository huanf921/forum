package top.vs.forum.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
import top.vs.forum.util.ForumFileUtil;

import java.io.File;
import java.io.IOException;
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

    @Value("${file.album.upload}")
    private String albumUploadPath;

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

    @Override
    public void updUserDetailInfo(UserDetailInfoDTO userDetailInfo) {
        if (userDetailInfo.getName() != null || userDetailInfo.getPassword() != null) {
            User user = new User();
            user.setId(userDetailInfo.getUserId());
            user.setName(userDetailInfo.getName());
            user.setPassword(userDetailInfo.getPassword());
            forumIdentFeignClient.updateUserBaseInfo(user);
        }
        UserDetail updUserDetailDTO = new UserDetail();
        BeanUtils.copyProperties(userDetailInfo, updUserDetailDTO);
        this.update(updUserDetailDTO, new LambdaQueryWrapper<UserDetail>()
                .eq(UserDetail::getUserId, userDetailInfo.getUserId()));
    }

    @Override
    public void saveUserAlbum(MultipartFile file, Integer userId) {
        // 保存相册图片，获取其当前存储的相对路径
        String userAlbumPath = saveUserFile(file, userId);

        // 新增相册表信息
        UserAlbum userAlbum = new UserAlbum();
        userAlbum.setUserId(userId);
        userAlbum.setPicUrl(userAlbumPath);
        userAlbumService.save(userAlbum);
    }

    @Override
    public void removeUserAlbum(Integer userId, String albumUrl) {
        // 删除相册表信息
        userAlbumService.remove(new LambdaQueryWrapper<UserAlbum>()
                .eq(UserAlbum::getUserId, userId)
                .eq(UserAlbum::getPicUrl, albumUrl));

        // 删除用户对应存储路径下的相册图片
        removeUserFile(albumUrl);
    }

    @Override
    public void updUserHeadUrl(MultipartFile file, Integer userId) {
        String originUrl = this.getUserHeadUrlById(userId);

        // 删除原用户头像
        removeUserFile(originUrl);

        // 上传新头像到对应文件目录中
        String userHeadPath = saveUserFile(file, userId);

        // 更新用户详情表信息
        UserDetail updUserDetail = new UserDetail();
        updUserDetail.setHeadUrl(userHeadPath);
        this.update(updUserDetail, new LambdaQueryWrapper<UserDetail>().eq(UserDetail::getUserId, userId));

    }

    @Override
    public void updRedisUserVisits(Integer userId) {
        // log.warn("访问"+userId+"空间，人气+1");
        UserSimpleRedisDTO userSimpleRedisDTO = (UserSimpleRedisDTO) redisTemplate.opsForHash().get("user:simple:info", userId);
        // 由于是反序列化出来的一个新对象，所以改变值之后还需要设置回去
        userSimpleRedisDTO.setWeekVisits(userSimpleRedisDTO.getWeekVisits() + 1);
        userSimpleRedisDTO.setAllVisits(userSimpleRedisDTO.getAllVisits() + 1);
        redisTemplate.opsForHash().put("user:simple:info", userId, userSimpleRedisDTO);

        // 对排行榜中的访问量值也进行维护
        redisTemplate.opsForZSet().incrementScore("user:week:rank", userId, 1);
        redisTemplate.opsForZSet().incrementScore("user:all:rank", userId, 1);
    }

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    private UserDetailInfoDTO getUserInfo(Integer userId, boolean isBrief) {
        UserDetailInfoDTO userDetailInfoDTO = new UserDetailInfoDTO();
        // 1、远程获取用户基础信息
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
            // 2、数据库获取用户详细信息
            UserDetail userDetail = this.getOne(new LambdaQueryWrapper<UserDetail>()
                    .eq(UserDetail::getUserId, userId));
            BeanUtils.copyProperties(userDetail, userDetailInfoDTO);

            userDetailInfoDTO.setPassword(user.getPassword());
        }
        // 相册图片地址列表
        List<String> albumUrls = userAlbumService.list(new LambdaQueryWrapper<UserAlbum>()
                .select(UserAlbum::getPicUrl)
                .eq(UserAlbum::getUserId, userId))
                .stream()
                .map(UserAlbum::getPicUrl)
                .collect(Collectors.toList());
        userDetailInfoDTO.setAlbumUrls(albumUrls);

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

    /**
     * 根据数据库文件地址删除用户对应存储路径下的文件
     * @param fileDbUrl
     */
    private void removeUserFile(String fileDbUrl){
        int firstIdx = fileDbUrl.indexOf("/");
        String subUrl = fileDbUrl.substring(firstIdx + 1);
        int secIdx = subUrl.indexOf("/");
        String relativePath = fileDbUrl.substring(secIdx + 1);

        FileUtil.del(albumUploadPath + relativePath);
    }

    /**
     * 保存上传的文件并返回文件存储时的相对路径
     * @param file
     * @param userId
     */
    private String saveUserFile(MultipartFile file, Integer userId) {
        String originalFilename = file.getOriginalFilename();
        String filePath = albumUploadPath + userId;
        File randomFile = ForumFileUtil.getRandomFile(originalFilename, filePath);

        // 保存图片
        try {
            file.transferTo(randomFile);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        // 获取当前存储路径
        String randomFileName = randomFile.getName();
        String userFilePath = "/album/" + userId + "/" + randomFileName;

        return userFilePath;
    }
}
