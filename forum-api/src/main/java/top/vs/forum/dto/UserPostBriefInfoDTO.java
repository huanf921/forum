package top.vs.forum.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPostBriefInfoDTO {

    private static final long serialVersionUID = 1L;

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
     * 发表时间
     */
    private LocalDateTime time;

}
