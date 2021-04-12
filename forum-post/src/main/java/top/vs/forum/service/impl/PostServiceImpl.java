package top.vs.forum.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.vs.forum.api.ForumIdentFeignClient;
import top.vs.forum.api.ForumUserFeignClient;
import top.vs.forum.dto.*;
import top.vs.forum.mapper.PostMapper;
import top.vs.forum.po.*;
import top.vs.forum.service.*;
import top.vs.forum.util.ForumFileUtil;
import top.vs.forum.vo.PostCommentVO;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 论贴表 服务实现类
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Value("${file.post.file}")
    private String postFilePath;

    @Autowired
    private PostContentService postContentService;

    @Autowired
    private PostCommentService postCommentService;

    @Autowired
    private CommentReplyService commentReplyService;

    @Autowired
    private UserLikeService userLikeService;

    @Autowired
    private ForumIdentFeignClient forumIdentFeignClient;

    @Autowired
    private ForumUserFeignClient forumUserFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void saveUserToPostInfo(UserToPostDTO userToPostDTO) {
        // 文件非空，上传文件到本地存储路径
        MultipartFile postFile = userToPostDTO.getPostFile();
        if (postFile != null) {
            String attachmentUrl = savePostFile(postFile, userToPostDTO.getUserId());
            userToPostDTO.setAttachmentUrl(attachmentUrl);
        }
        // 存储非文件信息（不包含帖子内容）
        Post post = new Post();
        BeanUtils.copyProperties(userToPostDTO, post);
        this.save(post);

        // 保存论贴内容
        Long postId = post.getId();

        String content = userToPostDTO.getContent();
        PostContent postContent = new PostContent();
        postContent.setPostId(postId);
        postContent.setContent(content);
        postContentService.save(postContent);

        // 为粉丝新增当前用户的发帖动态
        this.saveFanToPostActivity(userToPostDTO.getUserId(), postId);
        // 在缓存中新建论帖回复数的记录
        redisTemplate.opsForZSet().add("post:week:rank", postId, 0);
    }

    @Override
    public List<UserPostBriefInfoDTO> getUserPostBriefInfo(Integer userId) {
        return this.list(new LambdaQueryWrapper<Post>()
                .select(Post::getId, Post::getUserId, Post::getTitle, Post::getType, Post::getTime)
                .eq(Post::getUserId, userId))
                .stream()
                .map(post -> {
                    UserPostBriefInfoDTO userPostBriefInfoDTO = new UserPostBriefInfoDTO();
                    BeanUtils.copyProperties(post, userPostBriefInfoDTO);
                    userPostBriefInfoDTO.setPostId(post.getId());
                    return userPostBriefInfoDTO;
                }).collect(Collectors.toList());
    }

    @Override
    public List<Post> listPostReplies() {
        return this.list(new LambdaQueryWrapper<Post>()
                .select(Post::getId, Post::getReplies));
    }

    @Override
    public Post getSimpleInfoById(Long postId) {
        return this.getOne(new LambdaQueryWrapper<Post>()
                .select(Post::getUserId, Post::getTitle, Post::getType)
                .eq(Post::getId, postId));
    }

    @Override
    public List<TypePostDTO> listTypePosts(String type) {
        return this.list(new LambdaQueryWrapper<Post>()
                .eq(Post::getType, type))
                .stream()
                .map(post -> {
                    Integer userId = post.getUserId();
                    TypePostDTO typePostDTO = new TypePostDTO();
                    BeanUtils.copyProperties(post, typePostDTO);

                    typePostDTO.setUserId(userId);
                    typePostDTO.setPostId(post.getId());
                    typePostDTO.setName(getUserName(userId));
                    typePostDTO.setHeadUrl(getUserHeadUrl(userId));

                    return typePostDTO;
                }).collect(Collectors.toList());
    }

    @Override
    public void getPostInfoById(Long postId, PostCommentVO postCommentVO, boolean isFirst) {
        Post post = this.getOne(new LambdaQueryWrapper<Post>()
                .select(Post::getUserId, Post::getType, Post::getTitle, Post::getTime, Post::getAttachmentUrl)
                .eq(Post::getId, postId));
        // 标题、附件路径、类型
        String title = post.getTitle();
        postCommentVO.setTitle(title);
        String attachmentUrl = post.getAttachmentUrl();
        postCommentVO.setAttachmentUrl(attachmentUrl);
        String type = post.getType();
        postCommentVO.setType(type);

        List<PostCommentDTO> postCommentDTOS = new ArrayList<>();
        Integer userId = post.getUserId();
        postCommentVO.setPosterId(userId);
        postCommentVO.setName(getUserName(userId));
        if (isFirst) {
            PostCommentDTO postCommentDTO = new PostCommentDTO();
            // postCommentDTO.setCommentId(postId);
            // 内容
            PostContent pc = postContentService.getOne(new LambdaQueryWrapper<PostContent>()
                    .select(PostContent::getContent).eq(PostContent::getPostId, postId));
            String body = pc.getContent();
            postCommentDTO.setBody(body);
            // 发帖人
            postCommentDTO.setUserId(userId);
            postCommentDTO.setUserName(getUserName(userId));
            postCommentDTO.setHeadUrl(getUserHeadUrl(userId));
            postCommentDTO.setPoster(true);
            // 从缓存获取粉丝量
            UserSimpleRedisDTO userSimpleRedisDTO = (UserSimpleRedisDTO) redisTemplate.opsForHash().get("user:simple:info", userId);
            Integer fans = userSimpleRedisDTO.getFans();
            postCommentDTO.setFans(fans);
            postCommentDTO.setTime(post.getTime());

            // 点赞数
            int thumbs = userLikeService.count(new LambdaQueryWrapper<UserLike>()
                    .eq(UserLike::getPostId, postId));
            postCommentDTO.setThumbs(thumbs);
            postCommentDTOS.add(postCommentDTO);
        }

        postCommentVO.setRecords(postCommentDTOS);
    }

    @Override
    public void updRedisUserVisits(Long postId) {
        Integer userId = this.getOne(new LambdaQueryWrapper<Post>().select(Post::getUserId)
                .eq(Post::getId, postId)).getUserId();
        // log.warn("访问"+userId+"帖子，人气+1");

        UserSimpleRedisDTO userSimpleRedisDTO = (UserSimpleRedisDTO) redisTemplate.opsForHash().get("user:simple:info", userId);
        // 由于是反序列化出来的一个新对象，所以改变值之后还需要设置回去
        userSimpleRedisDTO.setWeekVisits(userSimpleRedisDTO.getWeekVisits() + 1);
        userSimpleRedisDTO.setAllVisits(userSimpleRedisDTO.getAllVisits() + 1);
        redisTemplate.opsForHash().put("user:simple:info", userId, userSimpleRedisDTO);

        // 对排行榜中的访问量值也进行维护
        redisTemplate.opsForZSet().incrementScore("user:week:rank", userId, 1);
        redisTemplate.opsForZSet().incrementScore("user:all:rank", userId, 1);
    }

    @Override
    public void updRedisPostReplies(Long postId) {
        redisTemplate.opsForZSet().incrementScore("post:week:rank", postId, 1);
    }

    @Override
    public byte[] fileDownLoad(String attachmentUrl, HttpServletResponse response) {
        String filePath = postFilePath + attachmentUrl;
        String fileName = attachmentUrl.substring(attachmentUrl.lastIndexOf("/") + 1);

        byte[] postFileBytes = FileUtil.readBytes(filePath);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        return postFileBytes;
    }

    @Override
    public void saveFanToPostActivity(Integer userId, Long postId) {
        List<Integer> fanIds = forumUserFeignClient.getUserFans(userId).getData();
        fanIds.forEach(fanId -> {
            Activity activity = new Activity();
            activity.setUserId(fanId);
            activity.setType(1);
            activity.setSubscribeId(userId);
            activity.setPostId(postId);
            forumUserFeignClient.saveFanActivity(activity);
        });

    }

    @Override
    public void saveFanToReplyActivity(Integer userId, Long postId) {
        List<Integer> fanIds = forumUserFeignClient.getUserFans(userId).getData();
        fanIds.forEach(fanId -> {
            Activity activity = new Activity();
            activity.setUserId(fanId);
            activity.setType(3);
            activity.setSubscribeId(userId);
            activity.setPostId(postId);
            forumUserFeignClient.saveFanActivity(activity);
        });
    }

    @Override
    public void saveToUserReplyMessage(Integer userId, Long postId) {
        Integer posterId = this.getById(postId).getUserId();
        Message message = new Message();
        message.setUserId(posterId);
        message.setType(3);
        message.setSrcUserId(userId);
        message.setPostId(postId);
        forumUserFeignClient.saveToUserMessage(message);
    }

    @Override
    public void saveThumbMessage(UserLikeDTO userLikeDTO) {
        Message message = new Message();

        Integer type = userLikeDTO.getType();
        Long postId = 0l;
        if (type.equals(1)) {
            // 点赞帖子
            postId = userLikeDTO.getHasThumbId();
        } else if (type.equals(2)){
            // 点赞评论
            Long commentId = userLikeDTO.getHasThumbId();
            postId = postCommentService.getById(commentId).getPostId();
        } else {
            // 点赞回复
            Long replyId = userLikeDTO.getHasThumbId();
            Long commentId = commentReplyService.getById(replyId).getCommentId();
            postId = postCommentService.getById(commentId).getPostId();
        }
        Integer userId = this.getById(postId).getUserId();
        message.setUserId(userId);//给谁点的赞
        message.setType(2);
        message.setSrcUserId(userLikeDTO.getUserId());
        message.setPostId(postId);//在哪篇帖子点的赞
        forumUserFeignClient.saveToUserMessage(message);
    }

    @Override
    public void saveReplyMessage(Integer userId, Long postId, Long commentReplyId) {
        Integer replyerId = commentReplyService.getById(commentReplyId).getUserId();
        Message message = new Message();
        message.setUserId(replyerId);
        message.setType(3);
        message.setSrcUserId(userId);
        message.setPostId(postId);
        forumUserFeignClient.saveToUserMessage(message);
    }

    @Override
    public void saveCommentMessage(Integer userId, Long postId, Long commentId) {
        Integer commenterId = postCommentService.getById(commentId).getUserId();
        Message message = new Message();
        message.setUserId(commenterId);
        message.setType(3);
        message.setSrcUserId(userId);
        message.setPostId(postId);
        forumUserFeignClient.saveToUserMessage(message);
    }

    /**
     * 保存上传的文件并返回文件存储时的相对路径
     * @param file
     * @param userId
     */
    private String savePostFile(MultipartFile file, Integer userId) {
        String originalFilename = file.getOriginalFilename();
        String filePath = postFilePath + userId;
        File randomFile = ForumFileUtil.getRandomFile(originalFilename, filePath);

        // 保存图片
        try {
            file.transferTo(randomFile);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        // 获取当前存储路径
        String randomFileName = randomFile.getName();
        String userPostFilePath = "/post-file/" + userId + "/" + randomFileName;

        return userPostFilePath;
    }

    /**
     * 远程调用获取用户名称
     * @param userId
     * @return
     */
    public String getUserName(Integer userId) {
        ResultDTO<User> userBaseInfoRes = forumIdentFeignClient.getUserBaseInfo(userId);
        if (!userBaseInfoRes.getCode().equals("200")) {
            throw new RuntimeException(userBaseInfoRes.getMessage());
        }
        User userBaseInfo = userBaseInfoRes.getData();
        String name = userBaseInfo.getName();
        return name;
    }

    /**
     * 远程调用获取用户头像地址
     * @param userId
     * @return
     */
    public String getUserHeadUrl(Integer userId) {
        ResultDTO<String> userHeadUrlRes = forumUserFeignClient.getUserHeadUrl(userId);
        if (!userHeadUrlRes.getCode().equals("200")) {
            throw new RuntimeException(userHeadUrlRes.getMessage());
        }
        String headUrl = userHeadUrlRes.getData();
        return headUrl;
    }

}
