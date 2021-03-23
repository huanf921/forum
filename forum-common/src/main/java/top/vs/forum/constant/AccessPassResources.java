package top.vs.forum.constant;

import java.util.HashSet;
import java.util.Set;

public class AccessPassResources {

    public static final Set<String> PASS_RES_SET = new HashSet<>();

    static {
        PASS_RES_SET.add("/");
        PASS_RES_SET.add("/user/login/page");
        PASS_RES_SET.add("/user/login");
        PASS_RES_SET.add("/user/register");
    }

    public static final Set<String> STATIC_RES_SET = new HashSet<>();

    static {
        STATIC_RES_SET.add("bootstrap");
        STATIC_RES_SET.add("css");
        STATIC_RES_SET.add("fonts");
        STATIC_RES_SET.add("img");
        STATIC_RES_SET.add("jquery");
        //STATIC_RES_SET.add("layer");
        STATIC_RES_SET.add("js");
    }

    /**
     * 用于判断某个ServletPath值是否对应一个静态资源
     * @param servletPath
     * @return
     * 		true：是静态资源
     * 		false：不是静态资源
     */
    public static boolean judgeCurrentServletPathWithinStaticResource(String servletPath) {

        // 1.排除字符串无效的情况
        if(servletPath == null || servletPath.length() == 0) {
            throw new RuntimeException(ForumConstant.MESSAGE_STRING_INVALIDATE);
        }

        // 2.根据“/”拆分ServletPath字符串
        String[] split = servletPath.split("/");

        // 3.考虑到第一个斜杠左边经过拆分后得到一个空字符串是数组的第一个元素，所以需要使用下标1取第二个元素
        String firstLevelPath = split[1];

        // 4.判断是否在静态资源集合中
        return STATIC_RES_SET.contains(firstLevelPath);
    }

}
