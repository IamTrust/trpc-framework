package org.trpc.framework.core.cache;

import java.util.HashMap;
import java.util.Map;

public class CommonServerCache {
    /**
     * 从注册中心获取的服务信息缓存到此
     */
    public static final Map<String, Object> PROVIDER_CLASS_MAP = new HashMap<>();
}
