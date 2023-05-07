package org.trpc.framework.core.filter;

import org.trpc.framework.core.common.ChannelFutureWrapper;
import org.trpc.framework.core.common.RpcInvocation;

import java.util.List;

/**
 * 客户端过滤器接口
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public interface IClientFilter extends IFilter {

    /**
     * 执行客户端过滤逻辑
     * @param src
     * @param rpcInvocation
     */
    void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation);

}
