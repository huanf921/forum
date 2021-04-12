package top.vs.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.vs.forum.dto.TypePostDTO;
import top.vs.forum.dto.UserLikeDTO;
import top.vs.forum.dto.UserPostBriefInfoDTO;
import top.vs.forum.dto.UserToPostDTO;
import top.vs.forum.po.Post;
import top.vs.forum.vo.PostCommentVO;

import javax.servlet.http.HttpServletResponse;
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

    void getPostInfoById(Long postId, PostCommentVO postCommentVO, boolean isFirst);

    void updRedisUserVisits(Long postId);

    void updRedisPostReplies(Long postId);

    byte[] fileDownLoad(String attachmentUrl, HttpServletResponse response);

    void saveFanToPostActivity(Integer userId, Long postId);

    void saveFanToReplyActivity(Integer userId, Long postId);

    void saveToUserReplyMessage(Integer userId, Long postId);

    void saveThumbMessage(UserLikeDTO userLikeDTO);

    void saveReplyMessage(Integer userId, Long postId, Long commentReplyId);

    void saveCommentMessage(Integer userId, Long postId, Long commentId);
}
