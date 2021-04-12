package top.vs.forum.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 动态信息展示实体
 */
@Data
public class SubscribeActivityVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 关注者id
     */
    private Integer subscribeId;

    /**
     * 关注者名称
     */
    private String subscribeName;

    /**
     * 关注者头像地址
     */
    private String subscribeHeadUrl;

    /**
     * 动态类型（被关注者 1、发帖 2、藏帖 3、回帖 4、关注）
     */
    private Integer type;

    /**
     * 发表/收藏/回复帖子id
     */
    private Long postId;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 被关注者id
     */
    private Integer toSubscribeId;

    /**
     * 关注者名称
     */
    private String toSubscribeName;

    /**
     * 关注者头像地址
     */
    private String toSubscribeHeadUrl;

    /**
     * 动态时间
     */
    private LocalDateTime time;

    /**
     * 是否已读（0：未读；1：已读）
     */
    private Integer readed;


}
