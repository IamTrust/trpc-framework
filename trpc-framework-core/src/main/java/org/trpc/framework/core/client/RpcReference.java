package org.trpc.framework.core.client;

import org.trpc.framework.core.proxy.ProxyFactory;

/**
 * RPC 代理
 * 获取到经过 RPC 代理后的对象
 * 通过该对象调用方法后即会发起 RPC 调用
 *
 * @author Trust会长
 * @Date 2023/4/20
 */
public class RpcReference {

    public ProxyFactory proxyFactory;

    public RpcReference(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    /**
     * 根据接口类型获取代理对象
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T get(Class<T> tClass) throws Throwable {
        return proxyFactory.getProxy(tClass);
    }
}
