package org.trpc.framework.core.filter.client;

import org.trpc.framework.core.common.ChannelFutureWrapper;
import org.trpc.framework.core.common.RpcInvocation;
import org.trpc.framework.core.common.util.CommonUtils;
import org.trpc.framework.core.filter.IClientFilter;

import java.util.List;

/**
 * 客户端服务分组过滤器
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public class GroupFilterImpl implements IClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        String group = String.valueOf(rpcInvocation.getAttachments().get("group"));
        for (ChannelFutureWrapper channelFutureWrapper : src) {
            if (!channelFutureWrapper.getGroup().equals(group)) {
                src.remove(channelFutureWrapper);
            }
        }
        if (CommonUtils.isEmptyList(src)) {
            throw new RuntimeException("no provider match for group " + group);
        }
    }
}
