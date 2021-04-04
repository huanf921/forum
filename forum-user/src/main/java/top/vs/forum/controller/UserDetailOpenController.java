package top.vs.forum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.dto.UserBriefInfoDTO;
import top.vs.forum.dto.UserDetailInfoDTO;
import top.vs.forum.dto.UserSimpleDTO;
import top.vs.forum.service.UserDetailService;
import top.vs.forum.vo.UserInfoVO;

/**
 * <p>
 * 用户详情开放服务控制器
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@RestController
@Slf4j
public class UserDetailOpenController {

    @Autowired
    private UserDetailService userDetailService;

    /**
     * 查询用户 粉丝数、访问量、排名等相关内容
     * @param userId
     * @return
     */
    // 前提对redis中用户粉丝/访问等信息做初始化及日常维护更新
    @GetMapping("/get/user/simple/info/{userId}")
    public ResultDTO<UserSimpleDTO> getUserSimpleInfo(@PathVariable("userId") Integer userId) {
        try {
            UserSimpleDTO userSimpleDTO = userDetailService.getUserSimpleInfoById(userId);
            String headUrl = userDetailService.getUserHeadUrlById(userId);
            userSimpleDTO.setHeadUrl(headUrl);
            return ResultDTO.ok(userSimpleDTO);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

}
