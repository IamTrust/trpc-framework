package org.trpc.framework.core.common.cache;

import org.trpc.framework.core.config.ServerConfig;
import org.trpc.framework.core.registry.URL;
import org.trpc.framework.core.serialize.SerializeFactory;
import org.trpc.framework.core.server.ServerFilterChain;
import org.trpc.framework.core.server.ServiceWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端缓存
 * 存储 RPC 调用过程中服务端需要暂时存储的数据
 *
 * @author Trust会长
 * @Date 2023/4/20
 */
public class CommonServerCache {
    /**
     * 从注册中心获取的服务信息缓存到此
     */
    public static final Map<String, Object> PROVIDER_CLASS_MAP = new HashMap<>();
    /**
     * 所有服务的 URL
     */
    public static final Set<URL> PROVIDER_URL_SET = new HashSet<>();
    /**
     * 服务端序列化策略
     */
    public static SerializeFactory SERVER_SERIALIZE_FACTORY;
    /**
     * 服务端过滤器链
     */
    public static ServerFilterChain SERVER_FILTER_CHAIN;

    public static ServerConfig SERVER_CONFIG;

    public static final Map<String, ServiceWrapper> PROVIDER_SERVICE_WRAPPER_MAP = new ConcurrentHashMap<>();
}
