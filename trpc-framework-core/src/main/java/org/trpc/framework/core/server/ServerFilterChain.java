package org.trpc.framework.core.server;

import org.trpc.framework.core.common.RpcInvocation;
import org.trpc.framework.core.filter.IServerFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端过滤器链
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public class ServerFilterChain {

    private static List<IServerFilter> serverFilters = new ArrayList<>();

    public void addServerFilter(IServerFilter serverFilter) {
        serverFilters.add(serverFilter);
    }

    public void doServerFilter(RpcInvocation rpcInvocation) {
        serverFilters.forEach(serverFilter -> serverFilter.doFilter(rpcInvocation));
    }

}
