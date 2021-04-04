package top.vs.forum.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserDetailInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户头像地址
     */
    private String headUrl;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户手机号
     */
    private String phone;

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
     * 总排名
     */
    private Long allRanking;

    /**
     * 周排名
     */
    private Long weekRanking;

    /**
     * 粉丝数
     */
    private Integer fans;

    /**
     * 相册地址列表
     */
    private List<String> albumUrls;
}
