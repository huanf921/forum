package top.vs.forum.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 具体评论信息
 */
@Data
public class PostCommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识评论内容的id
     */
    private String commentId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 是否楼主/发帖人
     */
    private boolean isPoster;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户头像地址
     */
    private String headUrl;

    /**
     * 粉丝数
     */
    private Integer fans;

    /**
     * 评论内容
     */
    private String body;

    /**
     * 评论时间
     */
    private LocalDateTime time;

    /**
     * 点赞数
     */
    private Integer thumbs;

    /**
     * 覆盖lombok的set方法？
     * @param commentId
     */
    public void setCommentId(Long commentId) {
        this.commentId = commentId + "";
    }
}
