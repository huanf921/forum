package top.vs.forum.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import top.vs.forum.dto.ResultDTO;
import top.vs.forum.dto.TypePostDTO;
import top.vs.forum.dto.UserToPostDTO;
import top.vs.forum.service.PostService;

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


    @GetMapping("/show/post/page/{postId}")
    public String showTypePosts(@PathVariable("postId") Long postId){
        return "post";
    }

}
