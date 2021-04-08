package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.vs.forum.mapper.PostContentMapper;
import top.vs.forum.po.PostContent;
import top.vs.forum.service.PostContentService;

/**
 * <p>
 * 论贴内容表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class PostContentServiceImpl extends ServiceImpl<PostContentMapper, PostContent> implements PostContentService {

}
