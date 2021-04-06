package top.vs.forum.service.impl;

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

}
