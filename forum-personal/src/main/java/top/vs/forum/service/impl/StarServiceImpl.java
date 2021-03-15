package top.vs.forum.service.impl;

import top.vs.forum.mapper.StarMapper;
import top.vs.forum.po.Star;
import top.vs.forum.service.StarService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 收藏表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class StarServiceImpl extends ServiceImpl<StarMapper, Star> implements StarService {

}
