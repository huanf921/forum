package top.vs.forum.controller;


import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.dto.TypePostDTO;
import top.vs.forum.dto.UserLikeDTO;
import top.vs.forum.dto.UserToPostDTO;
import top.vs.forum.po.CommentReply;
import top.vs.forum.po.PostComment;
import top.vs.forum.service.CommentReplyService;
import top.vs.forum.service.PostCommentService;
import top.vs.forum.service.PostService;
import top.vs.forum.service.UserLikeService;
import top.vs.forum.task.PortalPostInfoTask;
import top.vs.forum.vo.CommentReplyVO;
import top.vs.forum.vo.PostCommentVO;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 论贴表 前端控制器
 * </p>
 *
 * @author visional
 * @since 2021-03-09
 */
@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostCommentService postCommentService;

    @Autowired
    private CommentReplyService commentReplyService;

    @Autowired
    private UserLikeService userLikeService;

    @Autowired
    private PortalPostInfoTask portalPostInfoTask;

    @GetMapping("/sync/redis/data/to/db")
    public String syncRedisDataToDb() {
        portalPostInfoTask.updPostReplies2DbFromRedis();
        return "redirect:http://www.huanforum.com/";
    }

    @ModelAttribute
    public Map<Integer, String> getTypeMap() {
        Map<Integer, String> typeMap = new HashMap<>();
        typeMap.put(1, "技术交流");
        typeMap.put(2, "校闻趣事");
        typeMap.put(3, "社团活动");
        typeMap.put(4, "校园一角");
        typeMap.put(5, "美食佳肴");
        typeMap.put(6, "政史评谈");

        return typeMap;
    }

    /**
     * 保存用户发帖信息
     *
     * @param userToPostDTO
     * @return
     */
    @PostMapping("/save/user/to/post/info")
    @ResponseBody
    public ResultDTO saveUserToPostInfo(UserToPostDTO userToPostDTO) {
        // 保存上传时间
        userToPostDTO.setTime(LocalDateTime.now());
        try {
            postService.saveUserToPostInfo(userToPostDTO);

            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 根据分类展示帖子
     *
     * @param typeNum
     * @param typeMap
     * @return
     */
    @GetMapping("/show/{type}")
    public ModelAndView showTypePosts(@PathVariable("type") Integer typeNum,
                                      @ModelAttribute Map<Integer, String> typeMap) {
        String type = typeMap.get(typeNum);
        List<TypePostDTO> posts = postService.listTypePosts(type);

        ModelAndView modelAndView = new ModelAndView("post-type");
        modelAndView.addObject("posts", posts);
        modelAndView.addObject("type", type);
        return modelAndView;
    }

    /**
     * 展示论贴页面
     *
     * @param postId
     * @param map
     * @return
     */
    @GetMapping("/show/post/page/{postId}/{current}")
    public String showPostPages(@PathVariable("postId") Long postId, @PathVariable("current") Integer current, ModelMap map) {
        // 对缓存中用户个人访问量做维护
        postService.updRedisUserVisits(postId);
        map.addAttribute("postId", postId);
        map.addAttribute("current", current);
        return "post";
    }

    /**
     * 获取论贴页面评论信息
     *
     * @param postId
     * @param size
     * @param current
     * @return
     */
    @PostMapping("/get/post/comment/info")
    @ResponseBody
    public ResultDTO<PostCommentVO> getPostCommentInfo(
            Long postId, Integer size, Integer current) {
        try {
            Page<PostComment> page = new Page<PostComment>();
            page.setCurrent(current);
            page.setSize(size);
            PostCommentVO postCommentVO = new PostCommentVO();
            // 获取标题、内容及发帖人个人信息
            postService.getPostInfoById(postId, postCommentVO, current == 1);

            // 获取帖子相关分页评论信息
            postCommentService.pagePostComments(postId, page, postCommentVO);
            // System.out.println(postCommentVO);
            // postCommentVO.getRecords().get(1).setCommentId(1374680395126729111L);
            return ResultDTO.ok(postCommentVO);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 获取评论的回复信息（包括对当前评论的回复及对属该评论的回复的再回复）
     *
     * @param commentId
     * @return
     */
    @PostMapping("/get/comment/reply/info")
    @ResponseBody
    public ResultDTO<List<CommentReplyVO>> getCommentReplyInfo(Long commentId) {
        try {
            List<CommentReplyVO> commentReplyVOS = commentReplyService.getCommentReplyInfo(commentId);
            return ResultDTO.ok(commentReplyVOS);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 回帖（即评论）
     *
     * @param postComment
     * @return
     */
    @PostMapping("/user/to/comment")
    @ResponseBody
    public ResultDTO userToComment(PostComment postComment) {
        try {
            // 对缓存中帖子回复数做维护
            postService.updRedisPostReplies(postComment.getPostId());

            // 为粉丝新增回帖动态
            postService.saveFanToReplyActivity(postComment.getUserId(), postComment.getPostId());
            // 为帖主新增回帖消息
            postService.saveToUserReplyMessage(postComment.getUserId(), postComment.getPostId());

            postComment.setTime(LocalDateTime.now());
            postCommentService.save(postComment);
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 回复评论或其回复
     * @param commentReply
     * @return
     */
    @PostMapping("/user/to/reply")
    @ResponseBody
    public ResultDTO<CommentReply> userToReply(CommentReply commentReply){
        try {
            // 对缓存中帖子回复数做维护
            PostComment postComment = postCommentService.getById(commentReply.getCommentId());
            postService.updRedisPostReplies(postComment.getPostId());

            // 为粉丝新增回帖动态
            postService.saveFanToReplyActivity(commentReply.getUserId(), postComment.getPostId());

            commentReply.setTime(LocalDateTime.now());
            commentReplyService.save(commentReply);
            // 为被回复者新增回复消息
            if (commentReply.getCommentReplyId() != null) {
                postService.saveReplyMessage(commentReply.getUserId(), postComment.getPostId(), commentReply.getCommentReplyId());
            } else {
                postService.saveCommentMessage(commentReply.getUserId(), postComment.getPostId(), commentReply.getCommentId());
            }
            return ResultDTO.ok(commentReply);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 判断论贴/评论/回复是否被当前用户点赞
     * @param userLikeDTO
     * @return
     */
    @PostMapping("/has/thumb")
    @ResponseBody
    public ResultDTO<Boolean> hasThumbPostOrComment(UserLikeDTO userLikeDTO){
        try {
            boolean hasThumb = userLikeService.hasThumbPostOrCommentOrReply(userLikeDTO);
            return ResultDTO.ok(hasThumb);
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

    /**
     * 论贴附带文件下载
     * @param attachmentUrl
     */
    @PostMapping("/file/download")
    public void postFileDownLoad(String attachmentUrl,
                                 HttpServletResponse response) throws IOException {
        byte[] postFileBytes = postService.fileDownLoad(attachmentUrl, response);

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(postFileBytes);
        IoUtil.close(outputStream);
    }

    /**
     * 用户进行点赞（对帖子/评论/评论回复）
     * @return
     */
    @PostMapping("/user/thumb")
    @ResponseBody
    public ResultDTO userThumb(UserLikeDTO userLikeDTO){
        try {
            // 为被点赞者新增点赞消息
            postService.saveThumbMessage(userLikeDTO);

            userLikeService.userThumb(userLikeDTO);
            return ResultDTO.ok();
        } catch (Exception e) {
            return ResultDTO.error("500", e.getMessage());
        }
    }

}
