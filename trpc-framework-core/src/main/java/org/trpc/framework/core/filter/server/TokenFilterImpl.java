package org.trpc.framework.core.filter.server;

import org.trpc.framework.core.common.RpcInvocation;
import org.trpc.framework.core.common.util.CommonUtils;
import org.trpc.framework.core.filter.IServerFilter;
import org.trpc.framework.core.server.ServiceWrapper;

import static org.trpc.framework.core.common.cache.CommonServerCache.PROVIDER_SERVICE_WRAPPER_MAP;

/**
 * 服务端 Token 校验过滤器
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public class TokenFilterImpl implements IServerFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String token = String.valueOf(rpcInvocation.getAttachments().get("serviceToken"));
        ServiceWrapper serviceWrapper = PROVIDER_SERVICE_WRAPPER_MAP.get(rpcInvocation.getTargetServiceName());
        String matchToken = String.valueOf(serviceWrapper.getServiceToken());
        if (CommonUtils.isEmpty(matchToken)) {
            return;
        }
        if (!CommonUtils.isEmpty(token) && token.equals(matchToken)) {
            return;
        }
        throw new RuntimeException("token is " + token + " , verify result is false!");
    }
}
