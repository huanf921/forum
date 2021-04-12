package top.vs.forum.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubscribeUserVO implements Serializable {

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
     * 粉丝数
     */
    private Integer fans;
}
