package org.trpc.framework.core.filter.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trpc.framework.core.common.RpcInvocation;
import org.trpc.framework.core.filter.IServerFilter;

/**
 * 服务端日志记录过滤器
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public class LogFilterImpl implements IServerFilter {
    private static Logger logger = LoggerFactory.getLogger(LogFilterImpl.class);

    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        logger.info(rpcInvocation.getAttachments().get("c_app_name") + " do invoke -----> " + rpcInvocation.getTargetServiceName() + "#" + rpcInvocation.getTargetMethod());
    }
}
