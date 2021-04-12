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
 * 评论回复表
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentReply extends Model<CommentReply> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 回复者id
     */
    private Integer userId;

    /**
     * 评论id
     */
    private Long commentId;

    /**
     * 评论回复id
     */
    private Long commentReplyId;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 回复时间
     */
    private LocalDateTime time;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
