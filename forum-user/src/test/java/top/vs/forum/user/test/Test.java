package top.vs.forum.user.test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.vs.forum.service.UserDetailService;

// @RunWith(SpringRunner.class)
// @SpringBootTest
public class Test {

    @Autowired
    private UserDetailService userDetailService;

    @org.junit.Test
    public void testListUserSimples(){
        System.out.println(userDetailService.listUserSimples());
    }

}
