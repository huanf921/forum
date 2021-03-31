package top.vs.forum.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户详情表
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDetail extends Model<UserDetail> {

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
     * 用户性别
     */
    private String sex;

    /**
     * 用户头像地址
     */
    private String headUrl;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 用户生日
     */
    private LocalDate birthday;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 总访问量(论贴+空间)
     */
    private Integer allVisits;

    /**
     * 周访问量(论贴+空间)
     */
    private Integer weekVisits;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
