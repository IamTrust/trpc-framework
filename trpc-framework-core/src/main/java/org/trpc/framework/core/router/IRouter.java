package org.trpc.framework.core.router;

import org.trpc.framework.core.common.ChannelFutureWrapper;
import org.trpc.framework.core.registry.URL;

/**
 * 路由层接口
 *
 * @author Trust会长
 * @Date 2023/5/6
 */
public interface IRouter {
    /**
     * 刷新路由数组
     *
     * @param selector
     */
    void refreshRouterArr(Selector selector);

    /**
     * 获取到请求到连接通道
     *
     * @return
     */
    ChannelFutureWrapper select(Selector selector);

    /**
     * 更新权重信息
     *
     * @param url
     */
    void updateWeight(URL url);
}
