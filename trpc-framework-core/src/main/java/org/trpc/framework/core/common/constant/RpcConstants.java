package org.trpc.framework.core.common.constant;

/**
 * 常量
 *
 * @author Trust会长
 * @Date 2023/4/20
 */
public class RpcConstants {
    public static short MAGIC_NUMBER = 4;

    public static final String ZOOKEEPER_REGISTRY_TYPE = "zookeeper";

    public static final String JDK_PROXY_TYPE = "jdk";

    public static final String JAVASSIST_PROXY_TYPE = "javassist";

    public static final String RANDOM_ROUTER_TYPE = "random";

    public static final String ROTATE_ROUTER_TYPE = "rotate";

    public static final String JDK_SERIALIZE = "jdk";

    public static final String FAST_JSON_SERIALIZE = "fastJson";

    /**
     * 默认代理配置
     */
    public static final String DEFAULT_PROXY_TYPE = JDK_PROXY_TYPE;

    /**
     * 默认负载均衡配置
     */
    public static final String DEFAULT_ROUTER_STRATEGY = ROTATE_ROUTER_TYPE;

    /**
     * 默认客户端序列化方式配置
     */
    public static final String DEFAULT_CLIENT_SERIALIZE_TYPE = FAST_JSON_SERIALIZE;

    /**
     * 默认服务端序列化方式
     */
    public static final String DEFAULT_SERVER_SERIALIZE_TYPE = FAST_JSON_SERIALIZE;

    /**
     * 默认注册中心配置
     */
    public static final String DEFAULT_REGISTRY_TYPE = ZOOKEEPER_REGISTRY_TYPE;

    /**
     * 默认服务提供者端口
     */
    public static final int DEFAULT_PORT = 9093;

    /**
     * 默认注册中心地址
     */
    public static final String DEFAULT_REGISTRY_ADDR = "localhost:2181";

    /**
     * 默认服务名
     */
    public static final String DEFAULT_APPLICATION_NAME = "default-provider";

    /**
     * 默认业务线程数量
     */
    public static final int DEFAULT_BUSINESS_THREAD_NUM = 4;

    /**
     * 默认存放待处理请求的阻塞队列的长度
     */
    public static final int DEFAULT_BLOCKING_QUEUE_LEN = 128;
}
