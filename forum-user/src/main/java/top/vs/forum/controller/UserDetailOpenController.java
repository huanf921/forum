package top.vs.forum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import top.vs.forum.service.UserDetailService;

/**
 * <p>
 * 用户详情开放服务控制器
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Controller
@Slf4j
public class UserDetailOpenController {

    @Autowired
    private UserDetailService userDetailService;

    // TODO: 2021/3/31 提供给外部 查询用户访问量及粉丝数（均存储在redis）的接口

}
