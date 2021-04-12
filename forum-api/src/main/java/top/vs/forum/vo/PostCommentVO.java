package top.vs.forum.vo;

import lombok.Data;
import top.vs.forum.dto.PostCommentDTO;

import java.io.Serializable;
import java.util.List;

/**
 * 论贴评论信息展示实体
 */
@Data
public class PostCommentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 发帖人id
     */
    private Integer posterId;

    /**
     * 发帖人名称
     */
    private String name;

    /**
     * 论贴标题
     */
    private String title;

    /**
     * 论贴类型
     */
    private String type;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 总条数
     */
    private Long total;

    /**
     * 论帖附件路径
     */
    private String attachmentUrl;

    /**
     * 记录集合
     */
    private List<PostCommentDTO> records;
}
