package org.trpc.framework.core.common.cache;

import org.trpc.framework.core.client.ClientFilterChain;
import org.trpc.framework.core.common.ChannelFuturePollingRef;
import org.trpc.framework.core.config.ClientConfig;
import org.trpc.framework.core.common.ChannelFutureWrapper;
import org.trpc.framework.core.common.RpcInvocation;
import org.trpc.framework.core.registry.URL;
import org.trpc.framework.core.router.IRouter;
import org.trpc.framework.core.serialize.SerializeFactory;
import org.trpc.framework.core.spi.ExtensionLoader;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端缓存
 * 存储 RPC 调用过程中调用方需要暂时存储的数据
 *
 * @author Trust会长
 * @Date 2023/4/20
 */
public class CommonClientCache {
    public static BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue(100);
    public static Map<String,Object> RESP_MAP = new ConcurrentHashMap<>();
    public static ClientConfig CLIENT_CONFIG;
    //服务列表
    public static List<URL> SUBSCRIBE_SERVICE_LIST = new ArrayList<>();
    //Key为服务名，Value为该服务的URL，可能有多个(集群)
    public static Map<String, Map<String, String>> URL_MAP = new ConcurrentHashMap<>();
    public static Set<String> SERVER_ADDRESS = new HashSet<>();
    //每次进行远程调用的时候都是从这里面去选择服务提供者
    public static Map<String, List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();
    //随机请求的map
    public static Map<String, ChannelFutureWrapper[]> SERVICE_ROUTER_MAP = new ConcurrentHashMap<>();
    public static ChannelFuturePollingRef CHANNEL_FUTURE_POLLING_REF = new ChannelFuturePollingRef();
    //路由层所用负载均衡策略
    public static IRouter IROUTER;
    //序列化层所用序列化技术
    public static SerializeFactory CLIENT_SERIALIZE_FACTORY;
    //客户端过滤器链
    public static ClientFilterChain CLIENT_FILTER_CHAIN;
    // SPI加载器
    public static final ExtensionLoader EXTENSION_LOADER = new ExtensionLoader();
}
