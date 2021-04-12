package top.vs.forum.service;

import top.vs.forum.dto.UserPostBriefInfoDTO;
import top.vs.forum.po.Star;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 收藏表 服务类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
public interface StarService extends IService<Star> {

    List<UserPostBriefInfoDTO> listStarPosts(Integer userId);

    void saveFanActivity(Integer userId, Long starId);
}
