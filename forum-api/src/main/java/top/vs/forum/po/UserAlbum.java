package top.vs.forum.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户相册表
 * </p>
 *
 * @author visional
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAlbum extends Model<UserAlbum> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 相册图片地址
     */
    private String picUrl;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
