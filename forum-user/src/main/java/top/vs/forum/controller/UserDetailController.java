package top.vs.forum.controller;

import com.sun.xml.internal.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.dto.UserBriefInfoDTO;
import top.vs.forum.dto.UserDetailInfoDTO;
import top.vs.forum.po.User;
import top.vs.forum.service.UserDetailService;

import javax.servlet.http.HttpSession;

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

    // @GetMapping("/manage/page")
    // public String toManagePage() {
    //     // TODO: 2021/3/31 查询用户详情表+相册表+主表中的信息封装到vo
    //     return "user-manage";
    // }
    //
    // @GetMapping("/zone/page/{userId}")
    // public String toZonePage(@PathVariable("userId") Integer userId) {
    //     // log.info("访问空间");
    //     // TODO: 2021/3/31 查询用户详情表+相册表+主表中的信息封装到vo
    //     return "user-zone";
    // }

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

    // TODO: 2021/3/31 修改用户详情信息接口（不管是用户个人信息还是手机、邮箱等账户信息） 
    
    // TODO: 2021/3/31 相册图片查询接口（用于个人信息管理页面或者个人空间页面）

    // TODO: 2021/3/31 增加相册图片接口

    // TODO: 2021/3/31 删除相册图片接口

    // TODO: 2021/3/31 用户修改头像接口

}
