package top.vs.forum.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 评论回复信息展示实体
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Data
public class CommentReplyVO {
    /**
     * 回复唯一id
     */
    private String replyReplyId;

    /**
     * 回复者id
     */
    private Integer userId;

    /**
     * 回复者名称
     */
    private String userName;

    /**
     * 回复者头像地址
     */
    private String headUrl;

    /**
     * 评论id
     */
    private String commentId;

    /**
     * 评论回复id
     */
    private String commentReplyId;

    /**
     * 被回复者id
     */
    private Integer toReplyUserId;

    /**
     * 被回复者名称
     */
    private String toReplyName;

    /**
     * 被回复者头像地址
     */
    private String toReplyHeadUrl;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 回复时间
     */
    private LocalDateTime time;

    /**
     * 该回复的点赞数
     */
    private Integer thumbs;

    /**
     * 覆盖
     * @param replyReplyId
     */
    public void setReplyReplyId(Long replyReplyId) {
        this.replyReplyId = replyReplyId + "";
    }

    /**
     * 覆盖
     * @param commentId
     */
    public void setCommentId(Long commentId) {
        this.commentId = commentId + "";
    }

    /**
     * 覆盖
     * @param commentReplyId
     */
    public void setCommentReplyId(Long commentReplyId) {
        this.commentReplyId = commentReplyId + "";
    }
}
