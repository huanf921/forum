package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import top.vs.forum.mapper.UserMapper;
import top.vs.forum.po.User;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.vs.forum.service.UserService;

/**
 * <p>
 * 用户主表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
