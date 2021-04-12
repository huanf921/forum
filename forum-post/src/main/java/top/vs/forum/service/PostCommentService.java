package top.vs.forum.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.vs.forum.po.PostComment;
import top.vs.forum.vo.PostCommentVO;

/**
 * <p>
 * 论贴评论表 服务类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
public interface PostCommentService extends IService<PostComment> {

    void pagePostComments(Long postId, Page<PostComment> page, PostCommentVO postCommentVO);
}
