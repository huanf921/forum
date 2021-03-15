package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.vs.forum.mapper.PostMapper;
import top.vs.forum.po.Post;

/**
 * <p>
 * 论贴表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements IService<Post> {

}
