package top.vs.forum.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import top.vs.forum.constant.AccessPassResources;
import top.vs.forum.constant.ForumConstant;
import org.springframework.stereotype.Component;
import top.vs.forum.po.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
@Slf4j
public class ForumAccessFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        // 放行无需登录检查的请求路径（一些注册、登录的请求等）
        RequestContext requestContext = RequestContext.getCurrentContext(); // 获取请求上下文
        HttpServletRequest request = requestContext.getRequest(); // 获取当前request对象
        //（框架底层是借助ThreadLocal从当前线程上获取事先绑定的Request对象）
        if (AccessPassResources.PASS_RES_SET.contains(request.getServletPath())) {
            return false;
        }

        // 放行静态资源的访问（否则页面样式都无法显示）
        return !AccessPassResources.judgeCurrentServletPathWithinStaticResource(request.getServletPath());
    }

    @Override
    public Object run() throws ZuulException {
        // 1、获取到当前共享的session域中该用户的登录信息
        RequestContext requestContext = RequestContext.getCurrentContext(); // 获取请求上下文
        HttpServletRequest request = requestContext.getRequest(); // 获取当前request对象
        HttpSession session = request.getSession();
        Object loginUser = session.getAttribute(ForumConstant.ATTR_NAME_LOGIN_USER);
        // log.info("网关获取的用户信息：" + (User)loginUser);

        // 2、有这个信息则放行，否则重定向到认证模块的登录页面，并封装未登陆信息提示
        if (loginUser == null) {
            // 从请求上下文获取response
            HttpServletResponse response = requestContext.getResponse();
            session.setAttribute(ForumConstant.ATTR_NAME_SESSION_MESSAGE, ForumConstant.MESSAGE_ACCESS_FORBIDDEN);
            try {
                response.sendRedirect("/ident/login/page");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
