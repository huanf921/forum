package top.vs.forum.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类论帖信息实体
 */
@Data
public class TypePostDTO {

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
     * 发表时间
     */
    private LocalDateTime time;
}
