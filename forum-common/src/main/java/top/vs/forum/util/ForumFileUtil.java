package top.vs.forum.util;

import java.io.File;
import java.util.UUID;

/**
 * 论坛文件工具类
 */
public class ForumFileUtil {

    /**
     * 新建随机文件名对象
     * @param fileName
     * @param filePath
     */
    public static File getRandomFile(String fileName, String filePath){
        // 创建保存路径的文件对象
        File saveDir = new File(filePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        // 生成随机文件名
        int beginIndex = fileName.lastIndexOf(".");
        String suffix ="";
        if(beginIndex != -1) {
            suffix = fileName.substring(beginIndex);
        }
        String randomFileName = UUID.randomUUID().toString() + suffix;

        // 返回生成的文件对象
        return new File(saveDir, randomFileName);
    }

}
