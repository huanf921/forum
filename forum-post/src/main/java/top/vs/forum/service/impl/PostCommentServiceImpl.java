package top.vs.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.vs.forum.dto.PostCommentDTO;
import top.vs.forum.dto.UserSimpleRedisDTO;
import top.vs.forum.mapper.PostCommentMapper;
import top.vs.forum.po.PostComment;
import top.vs.forum.po.UserLike;
import top.vs.forum.service.PostCommentService;
import top.vs.forum.service.UserLikeService;
import top.vs.forum.vo.PostCommentVO;

import java.util.List;

/**
 * <p>
 * 论贴评论表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment> implements PostCommentService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PostServiceImpl postService;

    @Autowired
    private UserLikeService userLikeService;

    @Override
    public void pagePostComments(Long postId, Page<PostComment> page, PostCommentVO postCommentVO) {
        Page<PostComment> postCommentPage = this.page(page,
                new LambdaQueryWrapper<PostComment>().eq(PostComment::getPostId, postId));
        // 获取基本分页信息
        postCommentVO.setPages(postCommentPage.getPages());
        postCommentVO.setTotal(postCommentPage.getTotal());
        // 获取用户相关信息
        List<PostCommentDTO> postCommentDTOS = postCommentVO.getRecords();
        List<PostComment> postComments = postCommentPage.getRecords();
        Integer posterId = postCommentVO.getPosterId();
        postComments.forEach(postComment -> {
            PostCommentDTO postCommentDTO = new PostCommentDTO();
            postCommentDTO.setCommentId(postComment.getId());
            // 基本信息设置
            Integer userId = postComment.getUserId();//评论者id
            postCommentDTO.setUserId(userId);
            postCommentDTO.setBody(postComment.getContent());
            postCommentDTO.setTime(postComment.getTime());
            // 获取用户名、头像地址信息
            postCommentDTO.setUserName(postService.getUserName(userId));
            postCommentDTO.setHeadUrl(postService.getUserHeadUrl(userId));
            // 是否楼主
            postCommentDTO.setPoster(userId.equals(posterId));
            // 点赞数
            int thumbs = userLikeService.count(new LambdaQueryWrapper<UserLike>()
                    .eq(UserLike::getCommentId, postComment.getId()));
            postCommentDTO.setThumbs(thumbs);
            // 从缓存获取粉丝量
            UserSimpleRedisDTO userSimpleRedisDTO = (UserSimpleRedisDTO) redisTemplate.opsForHash().get("user:simple:info", userId);
            Integer fans = userSimpleRedisDTO.getFans();
            postCommentDTO.setFans(fans);

            postCommentDTOS.add(postCommentDTO);
        });
        // log.warn(postCommentVO.toString());
    }
}
