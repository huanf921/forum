package top.vs.forum.dto;

import lombok.Data;

/**
 * 简要用户信息传输实体
 */
@Data
public class UserSimpleDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户头像地址
     */
    private String headUrl;

    /**
     * 总访问量(论贴+空间)
     */
    private Integer allVisits;

    /**
     * 周访问量(论贴+空间)
     */
    private Integer weekVisits;

    /**
     * 粉丝数
     */
    private Integer fans;
}
