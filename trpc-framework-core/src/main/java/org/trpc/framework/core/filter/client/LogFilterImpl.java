package org.trpc.framework.core.filter.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trpc.framework.core.common.ChannelFutureWrapper;
import org.trpc.framework.core.common.RpcInvocation;
import org.trpc.framework.core.filter.IClientFilter;

import java.util.List;

import static org.trpc.framework.core.common.cache.CommonClientCache.CLIENT_CONFIG;

/**
 * 客户端日志记录过滤器
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public class LogFilterImpl implements IClientFilter {
    private static Logger logger = LoggerFactory.getLogger(LogFilterImpl.class);

    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        rpcInvocation.getAttachments().put("c_app_name",CLIENT_CONFIG.getApplicationName());
        logger.info(rpcInvocation.getAttachments().get("c_app_name")+" do invoke -----> "+rpcInvocation.getTargetServiceName());
    }
}
