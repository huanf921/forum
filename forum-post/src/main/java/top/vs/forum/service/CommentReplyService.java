package top.vs.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.vs.forum.po.CommentReply;
import top.vs.forum.vo.CommentReplyVO;

import java.util.List;

/**
 * <p>
 * 评论回复表 服务类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
public interface CommentReplyService extends IService<CommentReply> {

    List<CommentReplyVO> getCommentReplyInfo(Long commentId);
}
