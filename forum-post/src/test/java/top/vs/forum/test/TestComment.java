package top.vs.forum.test;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.vs.forum.dto.PostCommentDTO;
import top.vs.forum.po.PostComment;
import top.vs.forum.service.PostCommentService;
import top.vs.forum.vo.PostCommentVO;

import java.util.ArrayList;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestComment {

    @Autowired
    private PostCommentService postCommentService;

    @Test
    public void testPagePostComments() {
        Page<PostComment> postCommentPage = new Page<>(1, 5);
        PostCommentVO postCommentVO = new PostCommentVO();
        postCommentVO.setRecords(new ArrayList<PostCommentDTO>());
        postCommentService.pagePostComments(92233720368569L, postCommentPage, postCommentVO);

        System.out.println(postCommentVO);
    }
}
