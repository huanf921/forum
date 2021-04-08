package top.vs.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import top.vs.forum.dto.UserBriefInfoDTO;
import top.vs.forum.dto.UserDetailInfoDTO;
import top.vs.forum.dto.UserSimpleDTO;
import top.vs.forum.po.UserDetail;

import java.util.List;

/**
 * <p>
 * 用户详情表 服务类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
public interface UserDetailService extends IService<UserDetail>{

    UserSimpleDTO getUserSimpleInfoById(Integer userId);

    List<UserDetail> listUserSimples();

    String getUserHeadUrlById(Integer userId);

    UserBriefInfoDTO getUserBriefInfo(Integer userId);

    UserDetailInfoDTO getUserDetailInfo(Integer userId);

    void updUserDetailInfo(UserDetailInfoDTO userDetailInfo);

    void saveUserAlbum(MultipartFile file, Integer userId);

    void removeUserAlbum(Integer userId, String albumUrl);

    void updUserHeadUrl(MultipartFile file, Integer userId);

    void updRedisUserVisits(Integer userId);
}
