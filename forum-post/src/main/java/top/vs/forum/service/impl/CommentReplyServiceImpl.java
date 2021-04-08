package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.vs.forum.mapper.CommentReplyMapper;
import top.vs.forum.po.CommentReply;
import top.vs.forum.service.CommentReplyService;

/**
 * <p>
 * 评论回复表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class CommentReplyServiceImpl extends ServiceImpl<CommentReplyMapper, CommentReply> implements CommentReplyService {

}
