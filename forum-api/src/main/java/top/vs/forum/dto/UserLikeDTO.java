package top.vs.forum.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLikeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 判断点赞类型
     */
    private Integer type;

    /**
     * 要进行判断的具体id
     */
    private Long hasThumbId;

    /**
     * 用户id
     */
    private Integer userId;
}
