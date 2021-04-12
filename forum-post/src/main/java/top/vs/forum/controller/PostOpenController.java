package top.vs.forum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.dto.UserPostBriefInfoDTO;
import top.vs.forum.po.Post;
import top.vs.forum.service.PostService;

import java.util.List;

/**
 * <p>
 * 论贴开放服务控制器
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@RestController
public class PostOpenController {

    @Autowired
    private PostService postService;

    /**
     * 查询用户发表的论帖
     * @param userId
     * @return
     */
    @GetMapping("/get/user/post/brief/info/{userId}")
    public ResultDTO<List<UserPostBriefInfoDTO>> getUserPostBriefInfo(@PathVariable("userId") Integer userId) {
        try {
            List<UserPostBriefInfoDTO> userPostBriefInfoDTOS = postService.getUserPostBriefInfo(userId);

            return ResultDTO.ok(userPostBriefInfoDTOS);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 查询论贴简要信息
     * @return
     */
    @GetMapping("/get/post/brief/info/{postId}")
    public ResultDTO<Post> getPostBriefInfo(@PathVariable("postId") Long postId) {
        try {
            Post post = postService.getById(postId);
            return ResultDTO.ok(post);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

}
