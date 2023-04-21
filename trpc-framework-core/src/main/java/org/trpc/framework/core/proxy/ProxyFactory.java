package org.trpc.framework.core.proxy;

/**
 * 创建代理的工厂
 *
 * @author Trust会长
 * @Date 2023/4/20
 */
public interface ProxyFactory {
    <T> T getProxy(final Class<T> clazz) throws Throwable;
}
