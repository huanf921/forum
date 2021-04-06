package top.vs.forum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.dto.UserSimpleDTO;
import top.vs.forum.po.User;

@FeignClient("forum-identification")
public interface ForumIdentFeignClient {

    /**
     * 查询用户基础信息
     * @param userId
     * @return
     */
    @GetMapping("/get/user/base/info/{userId}")
    ResultDTO<User> getUserBaseInfo(@PathVariable("userId") Integer userId);

    /**
     * 修改用户基础信息
     * @return
     */
    @PostMapping("/update/user/base/info")
    ResultDTO updateUserBaseInfo(@RequestBody User user);
}
