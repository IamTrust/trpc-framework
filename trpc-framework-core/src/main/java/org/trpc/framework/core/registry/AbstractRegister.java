package org.trpc.framework.core.registry;

import java.util.List;

import static org.trpc.framework.core.cache.CommonClientCache.SUBSCRIBE_SERVICE_LIST;
import static org.trpc.framework.core.cache.CommonServerCache.PROVIDER_URL_SET;

/**
 * 注册层 抽象注册
 * 统一的操作在抽象类完成，具体的注册中心负责具体实现，实现可扩展多个注册中心
 *
 * @author Trust会长
 * @Date 2023/4/21
 */
public abstract class AbstractRegister implements RegistryService {
    @Override
    public void register(URL url) {
        PROVIDER_URL_SET.add(url);
    }

    @Override
    public void unRegister(URL url) {
        PROVIDER_URL_SET.remove(url);
    }

    @Override
    public void subscribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.add(url);
    }

    /**
     * 留给子类扩展
     *
     * @param url
     */
    public abstract void doAfterSubscribe(URL url);

    /**
     * 留给子类扩展
     *
     * @param url
     */
    public abstract void doBeforeSubscribe(URL url);

    /**
     * 留给子类扩展
     *
     * @param serviceName
     * @return
     */
    public abstract List<String> getProviderIps(String serviceName);


    @Override
    public void doUnSubscribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.remove(url.getServiceName());
    }
}
