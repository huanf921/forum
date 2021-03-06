package top.vs.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.vs.forum.dto.PostRedisDTO;
import top.vs.forum.po.User;
import top.vs.forum.vo.UserInfoVO;

import java.util.List;

/**
 * <p>
 * 用户主表 服务类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
public interface UserService extends IService<User> {

    List<UserInfoVO> getWeekHotUsers();

    void initRedisUserSimpleInfo(Integer id);

    List<PostRedisDTO> getWeekHotPosts();
}
