package top.vs.forum.service.impl;

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
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.dto.TypePostDTO;
import top.vs.forum.dto.UserPostBriefInfoDTO;
import top.vs.forum.dto.UserToPostDTO;
import top.vs.forum.mapper.PostMapper;
import top.vs.forum.po.Post;
import top.vs.forum.po.PostContent;
import top.vs.forum.po.User;
import top.vs.forum.service.PostContentService;
import top.vs.forum.service.PostService;
import top.vs.forum.util.ForumFileUtil;

import java.io.File;
import java.io.IOException;
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

        // 在缓存中新建论帖回复数的记录
        redisTemplate.opsForZSet().add("post:week:rank", postId, 0);
    }

    @Override
    public List<UserPostBriefInfoDTO> getUserPostBriefInfo(Integer userId) {
        return this.list(new LambdaQueryWrapper<Post>()
                .select(Post::getUserId, Post::getTitle, Post::getType, Post::getTime)
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

                    typePostDTO.setPostId(post.getId());

                    // 远程调用获取用户名称
                    ResultDTO<User> userBaseInfoRes = forumIdentFeignClient.getUserBaseInfo(userId);
                    if (!userBaseInfoRes.getCode().equals("200")) {
                        throw new RuntimeException(userBaseInfoRes.getMessage());
                    }
                    User userBaseInfo = userBaseInfoRes.getData();
                    String name = userBaseInfo.getName();
                    typePostDTO.setName(name);

                    // 远程调用获取用户头像地址
                    ResultDTO<String> userHeadUrlRes = forumUserFeignClient.getUserHeadUrl(userId);
                    if (!userHeadUrlRes.getCode().equals("200")) {
                        throw new RuntimeException(userHeadUrlRes.getMessage());
                    }
                    String headUrl = userHeadUrlRes.getData();
                    typePostDTO.setHeadUrl(headUrl);

                    return typePostDTO;
                }).collect(Collectors.toList());
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
}
