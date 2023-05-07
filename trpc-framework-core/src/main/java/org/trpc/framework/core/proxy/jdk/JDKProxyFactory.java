package org.trpc.framework.core.proxy.jdk;

import org.trpc.framework.core.client.RpcReferenceWrapper;
import org.trpc.framework.core.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * JDK 动态代理
 *
 * @author Trust会长
 * @Date 2023/4/20
 */
public class JDKProxyFactory implements ProxyFactory {
    @Override
    public <T> T getProxy(RpcReferenceWrapper<T> rpcReferenceWrapper) throws Throwable {
        return (T) Proxy.newProxyInstance(rpcReferenceWrapper.getAimClass().getClassLoader(), new Class[]{rpcReferenceWrapper.getAimClass()},
                new JDKClientInvocationHandler(rpcReferenceWrapper));
    }
}
