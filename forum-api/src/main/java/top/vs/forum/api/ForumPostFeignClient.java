package top.vs.forum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.dto.UserPostBriefInfoDTO;
import top.vs.forum.po.Post;

import java.util.List;

@FeignClient("forum-post")
public interface ForumPostFeignClient {

    /**
     * 查询用户发表的论帖
     * @param userId
     * @return
     */
    @GetMapping("/get/user/post/brief/info/{userId}")
    ResultDTO<List<UserPostBriefInfoDTO>> getUserPostBriefInfo(@PathVariable("userId") Integer userId);

    /**
     * 查询论贴信息
     * @param postId
     * @return
     */
    @GetMapping("/get/post/brief/info/{postId}")
    ResultDTO<Post> getPostBriefInfo(@PathVariable("postId") Long postId);

}
