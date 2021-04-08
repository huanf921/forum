package top.vs.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.vs.forum.dto.TypePostDTO;
import top.vs.forum.dto.UserPostBriefInfoDTO;
import top.vs.forum.dto.UserToPostDTO;
import top.vs.forum.po.Post;

import java.util.List;

/**
 * <p>
 * 论贴表 服务类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
public interface PostService extends IService<Post> {

    void saveUserToPostInfo(UserToPostDTO userToPostDTO);

    List<UserPostBriefInfoDTO> getUserPostBriefInfo(Integer userId);

    List<Post> listPostReplies();

    Post getSimpleInfoById(Long postId);

    List<TypePostDTO> listTypePosts(String type);
}
