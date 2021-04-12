package top.vs.forum.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息展示实体
 */
@Data
public class PersonalMessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 点赞/回复/关注者id
     */
    private Integer srcUserId;

    /**
     * 消息源用户名称
     */
    private String srcUserName;

    /**
     * 消息源用户头像地址
     */
    private String srcUserHeadUrl;

    /**
     * 消息类型（1：被关注 2：被点赞帖子 3：被回复帖子）
     */
    private Integer type;

    /**
     * 被点赞/回复的帖子id
     */
    private Long postId;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 消息时间
     */
    private LocalDateTime time;

    /**
     * 是否已读
     */
    private Integer readed;
}
