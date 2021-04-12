package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.vs.forum.mapper.CommentReplyMapper;
import top.vs.forum.po.CommentReply;
import top.vs.forum.po.UserLike;
import top.vs.forum.service.CommentReplyService;
import top.vs.forum.service.UserLikeService;
import top.vs.forum.vo.CommentReplyVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 评论回复表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class CommentReplyServiceImpl extends ServiceImpl<CommentReplyMapper, CommentReply> implements CommentReplyService {

    @Autowired
    private PostServiceImpl postService;

    @Autowired
    private UserLikeService userLikeService;

    @Override
    public List<CommentReplyVO> getCommentReplyInfo(Long commentId) {
        return this.list(new LambdaQueryWrapper<CommentReply>()
                .eq(CommentReply::getCommentId, commentId))
                .stream()
                .map(commentReply -> {
                    CommentReplyVO commentReplyVO = new CommentReplyVO();
                    // 设置基本信息
                    BeanUtils.copyProperties(commentReply,commentReplyVO);
                    commentReplyVO.setReplyReplyId(commentReply.getId());
                    // 获取回复者的相关信息（头像地址 + 名称）
                    commentReplyVO.setUserName(postService.getUserName(commentReply.getUserId()));
                    commentReplyVO.setHeadUrl(postService.getUserHeadUrl(commentReply.getUserId()));

                    // 被回复者（若有）
                    Long commentReplyId = commentReply.getCommentReplyId();
                    if (commentReplyId != null) {
                        // 由评论回复id获取到当前欲回复的那条评论回复的回复者id
                        Integer toReplyId = this.getById(commentReplyId).getUserId();
                        commentReplyVO.setToReplyUserId(toReplyId);
                        commentReplyVO.setToReplyName(postService.getUserName(toReplyId));
                        commentReplyVO.setToReplyHeadUrl(postService.getUserHeadUrl(toReplyId));
                    }

                    // 点赞数
                    int thumbs = userLikeService.count(new LambdaQueryWrapper<UserLike>()
                            .eq(UserLike::getReplyId, commentReply.getId()));
                    commentReplyVO.setThumbs(thumbs);
                    return commentReplyVO;
                }).collect(Collectors.toList());
    }
}
