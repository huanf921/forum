package top.vs.forum.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/**
 * user工程销毁时执行事件
 */
@Component
@Slf4j
public class DestroyEvent implements DisposableBean {

    /**
     * 进行redis删库操作避免下次启动读取到上次的持久化数据？
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        log.info("容器正在销毁...");
    }
}
