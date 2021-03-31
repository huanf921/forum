package top.vs.forum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import top.vs.forum.service.UserService;

/**
 * <p>
 * 用户开放服务控制器
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Controller
@Slf4j
public class UserOpenController {

    @Autowired
    private UserService userService;

    // TODO: 2021/3/31 提供给外部 查询用户接口

    // TODO: 2021/3/31 提供 修改密码接口

}
