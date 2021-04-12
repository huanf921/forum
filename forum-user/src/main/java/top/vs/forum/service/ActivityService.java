package top.vs.forum.service;

import top.vs.forum.po.Activity;
import com.baomidou.mybatisplus.extension.service.IService;
import top.vs.forum.po.Subscribe;
import top.vs.forum.vo.SubscribeActivityVO;

import java.util.List;

/**
 * <p>
 * 动态表 服务类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
public interface ActivityService extends IService<Activity> {

    List<SubscribeActivityVO> getUserSubscribeActivities(Integer userId);

    void saveFanActivity(Subscribe subscribe);
}
