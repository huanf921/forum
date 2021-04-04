package top.vs.forum.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import top.vs.forum.dto.UserSimpleRedisDTO;
import top.vs.forum.mapper.UserMapper;
import top.vs.forum.po.User;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.vs.forum.service.UserService;
import top.vs.forum.vo.UserInfoVO;

import java.util.List;

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

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<UserInfoVO> getWeekHotUsers() {
        return (List<UserInfoVO>) redisTemplate.opsForValue().get("weekHotUsers");
    }

    @Override
    public void initRedisUserSimpleInfo(Integer id) {
        UserSimpleRedisDTO userSimpleRedisDTO = new UserSimpleRedisDTO();
        userSimpleRedisDTO.setUserId(id);
        userSimpleRedisDTO.setAllVisits(0);
        userSimpleRedisDTO.setWeekVisits(0);
        userSimpleRedisDTO.setFans(0);
        redisTemplate.opsForHash().put("user:simple:info", id, userSimpleRedisDTO);
    }
}
