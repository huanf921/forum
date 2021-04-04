package top.vs.forum.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息展示实体
 */
@Data
public class UserInfoVO implements Serializable {

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
     * 个人简介
     */
    private String introduction;

    /**
     * 总访问量(论贴+空间)
     */
    private Integer allVisits;

    /**
     * 周访问量(论贴+空间)
     */
    private Integer weekVisits;

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
}
