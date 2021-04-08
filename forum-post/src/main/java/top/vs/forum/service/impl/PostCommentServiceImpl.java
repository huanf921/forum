package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.vs.forum.mapper.PostCommentMapper;
import top.vs.forum.po.PostComment;
import top.vs.forum.service.PostCommentService;

/**
 * <p>
 * 论贴评论表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment> implements PostCommentService {

}
