package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.vs.forum.api.ForumIdentFeignClient;
import top.vs.forum.api.ForumPostFeignClient;
import top.vs.forum.api.ForumUserFeignClient;
import top.vs.forum.mapper.MessageMapper;
import top.vs.forum.po.Message;
import top.vs.forum.po.Subscribe;
import top.vs.forum.service.MessageService;
import top.vs.forum.service.UserDetailService;
import top.vs.forum.vo.PersonalMessageVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 消息表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private ForumIdentFeignClient forumIdentFeignClient;

    @Autowired
    private ForumPostFeignClient forumPostFeignClient;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private ForumUserFeignClient forumUserFeignClient;

    @Override
    public List<PersonalMessageVO> getUserPersonalMessages(Integer userId) {
        return this.list(new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .orderByDesc(Message::getTime))
                .stream()
                .map(message -> {
                    PersonalMessageVO personalMessageVO = new PersonalMessageVO();
                    BeanUtils.copyProperties(message, personalMessageVO);

                    // 获取源用户
                    Integer srcUserId = message.getSrcUserId();
                    String srcUserName = forumIdentFeignClient.getUserBaseInfo(srcUserId).getData().getName();
                    personalMessageVO.setSrcUserName(srcUserName);
                    String srcUserHeadUrl = userDetailService.getUserHeadUrlById(srcUserId);
                    personalMessageVO.setSrcUserHeadUrl(srcUserHeadUrl);

                    // 如果是帖子相关的消息
                    if (!message.getType().equals(1)) {
                        String title = forumPostFeignClient.getPostBriefInfo(message.getPostId()).getData().getTitle();
                        personalMessageVO.setTitle(title);
                    }

                    return personalMessageVO;
                }).collect(Collectors.toList());
    }

    @Override
    public void saveToUserMessage(Subscribe subscribe) {
        Message message = new Message();
        message.setType(1);
        message.setUserId(subscribe.getSubscribeId());
        message.setSrcUserId(subscribe.getUserId());
        forumUserFeignClient.saveToUserMessage(message);
    }
}
