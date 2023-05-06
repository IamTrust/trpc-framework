package org.trpc.framework.core.cache;

import org.trpc.framework.core.registry.URL;
import org.trpc.framework.core.serialize.SerializeFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
}
