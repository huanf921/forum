package top.vs.forum.service;

import top.vs.forum.po.Subscribe;
import com.baomidou.mybatisplus.extension.service.IService;
import top.vs.forum.vo.SubscribeUserVO;

import java.util.List;

/**
 * <p>
 * 关注表 服务类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
public interface SubscribeService extends IService<Subscribe> {

    void updRedisUserSimpleInfo(Integer userId);

    void reduceRedisUserSimpleInfo(Integer subscribeId);

    List<SubscribeUserVO> listSubUsers(Integer userId);

    List<Integer> getUserFans(Integer userId);
}
