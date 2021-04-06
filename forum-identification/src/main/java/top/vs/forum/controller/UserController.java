package top.vs.forum.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import top.vs.forum.api.ForumUserFeignClient;
import top.vs.forum.constant.ForumConstant;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.dto.UserSimpleDTO;
import top.vs.forum.po.User;
import top.vs.forum.service.UserService;
import top.vs.forum.vo.UserInfoVO;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * 用户主表 前端控制器
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Controller
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ForumUserFeignClient forumUserFeignClient;

    @Value("${file.post.file}")
    private String postFilePath;

    @Value("${file.album.upload}")
    private String albumUploadPath;

    @GetMapping("/")
    public String showPortalPage(ModelMap modelMap, HttpSession session) {
        setPageHotData(modelMap);
        if (session.getAttribute(ForumConstant.ATTR_NAME_LOGIN_USER) != null) {
            return "center";
        }
        return "home";
    }

    @PostMapping("/ident/register")
    public String userRegister(User user, ModelMap map) {
        String nameInput = user.getName();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", nameInput);
        User userDB = userService.getOne(wrapper);
        if (userDB == null) {
            user.setPassword(SecureUtil.md5(user.getPassword()));
            userService.save(user);
            // 在缓存中初始化用户的粉丝数及其访问量
            Integer userId = userService.getOne(wrapper).getId();
            userService.initRedisUserSimpleInfo(userId);
            // 在文件管理器中初始化用户的相册目录和资料文件目录
            FileUtil.mkdir(postFilePath + userId);
            FileUtil.mkdir(albumUploadPath + userId);
            map.addAttribute(ForumConstant.ATTR_NAME_MESSAGE, ForumConstant.MESSAGE_TO_LOGIN);
        } else {
            map.addAttribute(ForumConstant.ATTR_NAME_MESSAGE, ForumConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
        }
        return "login";
    }

    @PostMapping("/ident/login")
    public String userLogin(User user,
                            ModelMap map,
                            HttpSession session) {
        // 清除session中的登录访问信息
        session.removeAttribute(ForumConstant.ATTR_NAME_SESSION_MESSAGE);

        String nameInput = user.getName();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", nameInput);
        User userDB = userService.getOne(wrapper);
        if (userDB == null) {
            // 账户是否存在
            map.addAttribute(ForumConstant.ATTR_NAME_MESSAGE, ForumConstant.MESSAGE_LOGIN_ACCT_NOT_FOUND);
            return "login";
        } else {
            // 密码是否正确
            String passwordDB = userDB.getPassword();
            String passwordInput = user.getPassword();
            if (StrUtil.equals(SecureUtil.md5(passwordInput), passwordDB)) {
                session.setAttribute(ForumConstant.ATTR_NAME_LOGIN_USER, userDB);
                return "redirect:http://www.huanforum.com/ident/to/center/page";
            } else {
                map.addAttribute(ForumConstant.ATTR_NAME_MESSAGE, ForumConstant.MESSAGE_PASSWORD_ERROR);
                return "login";
            }
        }
    }

    @GetMapping("ident/to/center/page")
    public String showCenterPage(ModelMap map) {
        setPageHotData(map);
        return "center";
    }

    @GetMapping("/ident/get/user/info/{userId}")
    @ResponseBody
    public ResultDTO<UserInfoVO> getUserInfo(@PathVariable("userId") Integer userId) {
        // 封装首页用户展示数据
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserId(userId);
        // 调用用户远程服务获取相关信息
        ResultDTO<UserSimpleDTO> userSimpleInfoResult = forumUserFeignClient.getUserSimpleInfo(userId);
        if (!userSimpleInfoResult.getCode().equals("200")) {
            return ResultDTO.error("500", userSimpleInfoResult.getMessage());
        } else {
            UserSimpleDTO userSimpleDTO = userSimpleInfoResult.getData();
            BeanUtils.copyProperties(userSimpleDTO, userInfoVO);
            return ResultDTO.ok(userInfoVO);
        }

    }

    @GetMapping("/ident/logout")
    public String userLogOut(HttpSession session) {
        session.removeAttribute(ForumConstant.ATTR_NAME_LOGIN_USER);
        return "redirect:http://www.huanforum.com";
    }

    /**
     * 将首页热门数据注入请求域
     * @param map
     */
    private void setPageHotData(ModelMap map) {
        // 查询热帖、周名人数据并封装
        List<UserInfoVO> userInfoVOS = userService.getWeekHotUsers();
        map.addAttribute("weekHotUsers", userInfoVOS);
    }
}
