package top.vs.forum.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 消息表
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Message extends Model<Message> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 关注者id
     */
    private Integer subscribeId;

    /**
     * 被关注者发表的帖子id
     */
    private Long postId;

    /**
     * 回复者id
     */
    private Integer replyUserId;

    /**
     * 回复帖子id
     */
    private Long replyPostId;

    /**
     * 回复评论id
     */
    private Long replyCommentId;

    /**
     * 回复评论回复id
     */
    private Long replyReplyId;

    /**
     * 被关注者id
     */
    private Integer toSubscribeId;

    /**
     * 消息时间
     */
    private LocalDateTime time;

    /**
     * 是否已读
     */
    private Integer readed;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
