package top.vs.forum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.dto.UserBriefInfoDTO;
import top.vs.forum.dto.UserDetailInfoDTO;
import top.vs.forum.dto.UserSimpleDTO;

@FeignClient("forum-user")
public interface ForumUserFeignClient {

    /**
     * 查询用户 粉丝数、访问量、排名等相关内容
     * @param userId
     * @return
     */
    @GetMapping("/get/user/simple/info/{userId}")
    ResultDTO<UserSimpleDTO> getUserSimpleInfo(@PathVariable("userId") Integer userId);

    /**
     * 查询用户简要信息（主要用于空间页面）
     * @param userId
     * @return
     */
    @GetMapping("/get/user/brief/info/{userId}")
    ResultDTO<UserBriefInfoDTO> getUserBriefInfo(@PathVariable("userId") Integer userId);

    /**
     * 查询用户详细信息（主要用于用户管理页面）
     * @param userId
     * @return
     */
    @GetMapping("/get/user/detail/info/{userId}")
    ResultDTO<UserDetailInfoDTO> getUserDetailInfo(@PathVariable("userId") Integer userId);

    /**
     * 初始化用户详情信息
     * @param userId
     * @return
     */
    @GetMapping("/save/user/detail/info/{userId}")
    ResultDTO saveUserDetailInfo(@PathVariable("userId") Integer userId);

    /**
     * 根据id获取头像
     * @param userId
     * @return
     */
    @GetMapping("/get/user/head/url/{userId}")
    ResultDTO<String> getUserHeadUrl(@PathVariable("userId") Integer userId);
}
