package top.vs.forum.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

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
     * 点赞/回复/关注者id
     */
    private Integer srcUserId;

    /**
     * 消息类型（1：被关注 2：被点赞帖子 3：被回复帖子）
     */
    private Integer type;

    /**
     * 被点赞/回复的帖子id
     */
    private Long postId;

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
