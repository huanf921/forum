package top.vs.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.vs.forum.dto.UserBriefInfoDTO;
import top.vs.forum.dto.UserDetailInfoDTO;
import top.vs.forum.dto.UserSimpleDTO;
import top.vs.forum.po.UserDetail;
import top.vs.forum.vo.UserInfoVO;

import java.util.List;

/**
 * <p>
 * 用户详情表 服务类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
public interface UserDetailService extends IService<UserDetail> {

    UserSimpleDTO getUserSimpleInfoById(Integer userId);

    List<UserDetail> listUserSimples();

    String getUserHeadUrlById(Integer userId);

    UserBriefInfoDTO getUserBriefInfo(Integer userId);

    UserDetailInfoDTO getUserDetailInfo(Integer userId);
}
