package top.vs.forum.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import top.vs.forum.constant.ForumConstant;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.po.User;
import top.vs.forum.service.UserService;

import javax.servlet.http.HttpSession;
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

    @GetMapping("/")
    public String showPortalPage(ModelMap modelMap) {

        // 查询首页数据并封装

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
                // log.info("存入spring session：" + session.getAttribute("loginUser"));
                return "redirect:http://www.huanforum.com/ident/to/center/page";
            } else {
                map.addAttribute(ForumConstant.ATTR_NAME_MESSAGE, ForumConstant.MESSAGE_PASSWORD_ERROR);
                return "login";
            }
        }
    }

    @GetMapping("/ident/logout")
    public String userLogOut(HttpSession session) {
        session.removeAttribute(ForumConstant.ATTR_NAME_LOGIN_USER);
        return "redirect:http://www.huanforum.com";
    }
}
