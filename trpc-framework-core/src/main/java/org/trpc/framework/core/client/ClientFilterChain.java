package org.trpc.framework.core.client;

import org.trpc.framework.core.common.ChannelFutureWrapper;
import org.trpc.framework.core.common.RpcInvocation;
import org.trpc.framework.core.filter.IClientFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端过滤器链
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public class ClientFilterChain {

    private static List<IClientFilter> clientFilters = new ArrayList<>();

    public void addClientFilter(IClientFilter clientFilter) {
        clientFilters.add(clientFilter);
    }

    public void doClientFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        clientFilters.forEach(clientFilter -> clientFilter.doFilter(src, rpcInvocation));
    }

}
