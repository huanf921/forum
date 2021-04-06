package top.vs.forum.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户点赞表
 * </p>
 *
 * @author visional
 * @since 2021-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserLike extends Model<UserLike> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 点赞类型
     */
    private Integer type;

    /**
     * 点赞的帖子id
     */
    private Long postId;

    /**
     * 点赞的评论id
     */
    private Long commentId;

    /**
     * 点赞的评论回复id
     */
    private Long replyId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
