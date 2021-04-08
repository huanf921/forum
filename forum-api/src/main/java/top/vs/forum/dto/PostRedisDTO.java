package top.vs.forum.dto;

import lombok.Data;

/**
 * 存储在缓存中的热帖信息实体
 */
@Data
public class PostRedisDTO {

    /**
     * 论贴id
     */
    private Long postId;

    /**
     * 论帖标题
     */
    private String title;

    /**
     * 论贴分类类型
     */
    private String type;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 回复数
     */
    private Integer replies;
}
