package top.vs.forum.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserToPostDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 论帖标题
     */
    private String title;

    /**
     * 论贴分类类型
     */
    private String type;

    /**
     * 论帖内容
     */
    private String content;

    /**
     * 上传的附件原件
     */
    private MultipartFile postFile;

    /**
     * 论帖附件路径
     */
    private String attachmentUrl;

    /**
     * 发表时间
     */
    private LocalDateTime time;

}
