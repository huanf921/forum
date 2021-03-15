package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.vs.forum.mapper.UserDetailMapper;
import top.vs.forum.po.UserDetail;
import top.vs.forum.service.UserDetailService;

/**
 * <p>
 * 用户详情表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class UserDetailServiceImpl extends ServiceImpl<UserDetailMapper, UserDetail> implements UserDetailService {

}
