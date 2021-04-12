package top.vs.forum.service;

import top.vs.forum.po.Message;
import com.baomidou.mybatisplus.extension.service.IService;
import top.vs.forum.po.Subscribe;
import top.vs.forum.vo.PersonalMessageVO;

import java.util.List;

/**
 * <p>
 * 消息表 服务类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
public interface MessageService extends IService<Message> {

    List<PersonalMessageVO> getUserPersonalMessages(Integer userId);

    void saveToUserMessage(Subscribe subscribe);
}
