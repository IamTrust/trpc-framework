package org.trpc.framework.core.proxy.jdk;

import org.trpc.framework.core.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * JDK 动态代理
 */
public class JDKProxyFactory implements ProxyFactory {
    @Override
    public <T> T getProxy(Class<T> clazz) throws Throwable {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                new JDKClientInvocationHandler(clazz));
    }
}
