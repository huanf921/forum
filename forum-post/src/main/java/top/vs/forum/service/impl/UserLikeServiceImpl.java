package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import top.vs.forum.dto.UserLikeDTO;
import top.vs.forum.po.UserLike;
import top.vs.forum.mapper.UserLikeMapper;
import top.vs.forum.service.UserLikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户点赞表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-04-06
 */
@Service
public class UserLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike> implements UserLikeService {

    @Override
    public boolean hasThumbPostOrCommentOrReply(UserLikeDTO userLikeDTO) {
        Integer userId = userLikeDTO.getUserId();
        Integer type = userLikeDTO.getType();
        if (type.equals(1)) {
            UserLike userLike = this.getOne(new LambdaQueryWrapper<UserLike>()
                    .eq(UserLike::getUserId, userId)
                    .eq(UserLike::getPostId, userLikeDTO.getHasThumbId()));
            return userLike == null ? false : true;
        } else if (type.equals(2)){
            UserLike userLike = this.getOne(new LambdaQueryWrapper<UserLike>()
                    .eq(UserLike::getUserId, userId)
                    .eq(UserLike::getCommentId, userLikeDTO.getHasThumbId()));
            return userLike == null ? false : true;
        } else {
            UserLike userLike = this.getOne(new LambdaQueryWrapper<UserLike>()
                    .eq(UserLike::getUserId, userId)
                    .eq(UserLike::getReplyId, userLikeDTO.getHasThumbId()));
            return userLike == null ? false : true;
        }
    }

    @Override
    public void userThumb(UserLikeDTO userLikeDTO) {
        Integer type = userLikeDTO.getType();
        Integer userId = userLikeDTO.getUserId();
        Long thumbId = userLikeDTO.getHasThumbId();
        UserLike userLike = new UserLike();
        userLike.setType(type);
        userLike.setUserId(userId);
        if (type.equals(1)) {
            userLike.setPostId(thumbId);
        } else if (type.equals(2)) {
            userLike.setCommentId(thumbId);
        } else {
            userLike.setReplyId(thumbId);
        }
        this.save(userLike);
    }
}
