package top.vs.forum.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户基本信息传输实体
 */
@Data
public class UserBriefInfoDTO implements Serializable {

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
