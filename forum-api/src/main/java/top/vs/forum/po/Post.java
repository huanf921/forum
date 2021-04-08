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
 * 论贴表
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Post extends Model<Post> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发表用户id
     */
    private Integer userId;

    /**
     * 论帖标题
     */
    private String title;

    /**
     * 论贴分类类型
     */
    private String type;

    /**
     * 论帖附件路径
     */
    private String attachmentUrl;

    /**
     * 回复数
     */
    private Integer replies;

    /**
     * 发表时间
     */
    private LocalDateTime time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
