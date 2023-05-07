package org.trpc.framework.core.filter.client;

import org.trpc.framework.core.common.ChannelFutureWrapper;
import org.trpc.framework.core.common.RpcInvocation;
import org.trpc.framework.core.common.util.CommonUtils;
import org.trpc.framework.core.filter.IClientFilter;

import java.util.Iterator;
import java.util.List;

/**
 * 客户端IP直连过滤器
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public class DirectInvokeFilterImpl implements IClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        String url = (String) rpcInvocation.getAttachments().get("url");
        if(CommonUtils.isEmpty(url)){
            return;
        }
        Iterator<ChannelFutureWrapper> channelFutureWrapperIterator = src.iterator();
        while (channelFutureWrapperIterator.hasNext()){
            ChannelFutureWrapper channelFutureWrapper = channelFutureWrapperIterator.next();
            if(!(channelFutureWrapper.getHost()+":"+channelFutureWrapper.getPort()).equals(url)){
                channelFutureWrapperIterator.remove();
            }
        }
        if(CommonUtils.isEmptyList(src)){
            throw new RuntimeException("no match provider url for "+ url);
        }
    }
}
