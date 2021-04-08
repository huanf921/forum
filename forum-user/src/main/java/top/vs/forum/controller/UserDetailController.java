package top.vs.forum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.vs.forum.api.ForumPostFeignClient;
import top.vs.forum.constant.ForumConstant;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.dto.UserBriefInfoDTO;
import top.vs.forum.dto.UserDetailInfoDTO;
import top.vs.forum.dto.UserPostBriefInfoDTO;
import top.vs.forum.po.User;
import top.vs.forum.po.UserDetail;
import top.vs.forum.service.UserDetailService;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 * 用户详情表 前端控制器
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Controller
@Slf4j
public class UserDetailController {

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private ForumPostFeignClient forumPostFeignClient;

    @GetMapping("/zone/page/{userId}")
    public String toZonePage(@PathVariable("userId") Integer userId, ModelMap map) {
        // 存放当前进入的空间所属用户id
        map.addAttribute(ForumConstant.ATTR_NAME_CUR_QRY_USER_ID, userId);

        // 对缓存中的用户访问量进行维护
        userDetailService.updRedisUserVisits(userId);
        return "user-zone";
    }

    /**
     * 查询用户简要信息（主要用于空间页面）
     * @param userId
     * @return
     */
    @GetMapping("/get/user/brief/info/{userId}")
    @ResponseBody
    public ResultDTO<UserBriefInfoDTO> getUserBriefInfo(@PathVariable("userId") Integer userId) {
        try {
            UserBriefInfoDTO userBriefInfoDTO = userDetailService.getUserBriefInfo(userId);
            return ResultDTO.ok(userBriefInfoDTO);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 查询用户简要论坛信息
     * @param userId
     * @return
     */
    @GetMapping("/get/user/post/brief/info/{userId}")
    @ResponseBody
    public ResultDTO<List<UserPostBriefInfoDTO>> getUserPostBriefInfo(@PathVariable("userId") Integer userId) {
        ResultDTO<List<UserPostBriefInfoDTO>> res = forumPostFeignClient.getUserPostBriefInfo(userId);
        if (res.getCode().equals("200")) {
            return res;
        } else {
            return ResultDTO.error("500", res.getMessage());
        }
    }

    /**
     * 查询用户详细信息（主要用于用户管理页面）
     * @param userId
     * @return
     */
    @GetMapping("/get/user/detail/info/{userId}")
    @ResponseBody
    public ResultDTO<UserDetailInfoDTO> getUserDetailInfo(@PathVariable("userId") Integer userId) {
        try {
            UserDetailInfoDTO userDetailInfoDTO = userDetailService.getUserDetailInfo(userId);
            return ResultDTO.ok(userDetailInfoDTO);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 初始化用户详情信息
     * @param userId
     * @return
     */
    @GetMapping("/save/user/detail/info/{userId}")
    public ResultDTO saveUserDetailInfo(@PathVariable("userId") Integer userId){
        UserDetail userDetail = new UserDetail();
        try {
            userDetail.setHeadUrl("https://static.runoob.com/images/mix/img_avatar.png");
            userDetailService.save(userDetail);
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 修改用户详情信息（不管是用户个人信息还是手机、邮箱等账户信息）
     * @param userDetailInfo
     * @return
     */
    @PostMapping("/upd/user/detail/info")
    @ResponseBody
    public ResultDTO updUserDetailInfo(@RequestBody UserDetailInfoDTO userDetailInfo) {
        log.info(userDetailInfo.toString());
        try {
            userDetailService.updUserDetailInfo(userDetailInfo);
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 增加相册图片
     * @param file
     * @return
     */
    @PostMapping("/save/user/album")
    @ResponseBody
    public ResultDTO saveUserAlbum(MultipartFile file, HttpSession session) {
        try {
            User user = (User) session.getAttribute(ForumConstant.ATTR_NAME_LOGIN_USER);
            userDetailService.saveUserAlbum(file, user.getId());
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }

    }

    /**
     * 删除相册图片
     * @param albumUrl
     * @param session
     * @return
     */
    @GetMapping("/remove/user/album")
    @ResponseBody
    public ResultDTO removeUserAlbum(@RequestParam("albumUrl") String albumUrl, HttpSession session) {
        User loginUser = (User) session.getAttribute(ForumConstant.ATTR_NAME_LOGIN_USER);
        Integer userId = loginUser.getId();

        try {
            userDetailService.removeUserAlbum(userId, albumUrl);
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }

    }

    /**
     * 用户修改头像
     * @param file
     * @param session
     * @return
     */
    @PostMapping("/upd/user/head/url")
    @ResponseBody
    public ResultDTO updUserHeadUrl(MultipartFile file, HttpSession session) {
        User user = (User) session.getAttribute(ForumConstant.ATTR_NAME_LOGIN_USER);
        Integer userId = user.getId();

        try {
            userDetailService.updUserHeadUrl(file, userId);
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }
}
