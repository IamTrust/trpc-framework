package org.trpc.framework.core.filter;

import org.trpc.framework.core.common.RpcInvocation;

/**
 * 服务端过滤器接口
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public interface IServerFilter extends IFilter {

    /**
     * 执行服务端过滤逻辑
     * @param rpcInvocation
     */
    void doFilter(RpcInvocation rpcInvocation);

}
