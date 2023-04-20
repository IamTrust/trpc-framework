package org.trpc.framework.core.proxy;

public interface ProxyFactory {
    <T> T getProxy(final Class<T> clazz) throws Throwable;
}
