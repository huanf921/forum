package top.vs.forum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.po.User;
import top.vs.forum.service.UserService;

/**
 * <p>
 * 用户开放服务控制器
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@RestController
@Slf4j
public class UserOpenController {

    @Autowired
    private UserService userService;

    /**
     * 查询用户基础信息
     * @param userId
     * @return
     */
    @GetMapping("/get/user/base/info/{userId}")
    public ResultDTO<User> getUserBaseInfo(@PathVariable("userId") Integer userId) {
        try {
            User user = userService.getById(userId);
            return ResultDTO.ok(user);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 修改用户密码
     * @param userId
     * @return
     */
    @GetMapping("/update/user/pwd/{userId}")
    public ResultDTO updateUserPwd(@PathVariable("userId") Integer userId) {
        User condition = new User();
        condition.setId(userId);
        try {
            userService.updateById(condition);
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }

    }

}
