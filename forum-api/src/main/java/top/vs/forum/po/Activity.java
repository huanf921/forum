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
 * 动态表
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Activity extends Model<Activity> {

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
     * 关注者id
     */
    private Integer subscribeId;

    /**
     * 动态类型（被关注者 1、发帖 2、藏帖 3、回帖 4、关注）
     */
    private Integer type;

    /**
     * 发表/收藏/回复帖子id
     */
    private Long postId;

    /**
     * 被关注者id
     */
    private Integer toSubscribeId;

    /**
     * 动态时间
     */
    private LocalDateTime time;

    /**
     * 是否已读（0：未读；1：已读）
     */
    private Integer readed;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
