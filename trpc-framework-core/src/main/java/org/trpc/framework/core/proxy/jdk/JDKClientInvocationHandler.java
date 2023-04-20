package org.trpc.framework.core.proxy.jdk;

import org.trpc.framework.core.common.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.trpc.framework.core.cache.CommonClientCache.RESP_MAP;
import static org.trpc.framework.core.cache.CommonClientCache.SEND_QUEUE;

/**
 * JDK 动态代理的三个参数：1-目标类实现的接口，2-目标类的类加载器，3-代理后的具体处理
 * 这个类即定义 JDK 动态代理的具体处理
 * 主要为向服务端发起远程调用
 */
public class JDKClientInvocationHandler implements InvocationHandler {
    private final static Object OBJECT = new Object();

    private Class<?> clazz;

    public JDKClientInvocationHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setArgs(args);
        rpcInvocation.setTargetMethod(method.getName());
        rpcInvocation.setTargetServiceName(clazz.getName());
        rpcInvocation.setUuid(UUID.randomUUID().toString());
        RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);
        SEND_QUEUE.add(rpcInvocation);
        long beginTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - beginTime < 3*1000) { // 超时时间为3秒，后期用配置文件调整
            Object object = RESP_MAP.get(rpcInvocation.getUuid());
            if (object instanceof RpcInvocation) {
                return ((RpcInvocation)object).getResponse();
            }
        }
        throw new TimeoutException("client wait server's response timeout!");
    }
}
