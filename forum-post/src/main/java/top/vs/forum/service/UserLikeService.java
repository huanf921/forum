package top.vs.forum.service;

import top.vs.forum.dto.UserLikeDTO;
import top.vs.forum.po.UserLike;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户点赞表 服务类
 * </p>
 *
 * @author visional
 * @since 2021-04-06
 */
public interface UserLikeService extends IService<UserLike> {

    boolean hasThumbPostOrCommentOrReply(UserLikeDTO userLikeDTO);

    void userThumb(UserLikeDTO userLikeDTO);
}
