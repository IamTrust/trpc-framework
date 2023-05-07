package org.trpc.framework.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.trpc.framework.core.common.RpcDecoder;
import org.trpc.framework.core.common.RpcEncoder;
import org.trpc.framework.core.common.event.TRpcListenerLoader;
import org.trpc.framework.core.common.util.CommonUtils;
import org.trpc.framework.core.config.PropertiesBootstrap;
import org.trpc.framework.core.config.ServerConfig;
import org.trpc.framework.core.filter.IServerFilter;
import org.trpc.framework.core.filter.server.LogFilterImpl;
import org.trpc.framework.core.filter.server.TokenFilterImpl;
import org.trpc.framework.core.registry.RegistryService;
import org.trpc.framework.core.registry.URL;
import org.trpc.framework.core.registry.zookeeper.ZookeeperRegister;
import org.trpc.framework.core.serialize.SerializeFactory;
import org.trpc.framework.core.serialize.fastjson.FastJsonSerializeFactory;
import org.trpc.framework.core.serialize.jdk.JDKSerializeFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.trpc.framework.core.common.cache.CommonClientCache.CLIENT_SERIALIZE_FACTORY;
import static org.trpc.framework.core.common.cache.CommonClientCache.EXTENSION_LOADER;
import static org.trpc.framework.core.common.cache.CommonServerCache.*;
import static org.trpc.framework.core.common.constant.RpcConstants.FAST_JSON_SERIALIZE;
import static org.trpc.framework.core.common.constant.RpcConstants.JDK_SERIALIZE;
import static org.trpc.framework.core.spi.ExtensionLoader.EXTENSION_LOADER_CLASS_CACHE;

/**
 * 远程调用服务端
 *
 * @author Trust会长
 */
public class Server {
    private static EventLoopGroup bossGroup = null;

    private static EventLoopGroup workerGroup = null;

    private ServerConfig serverConfig;

    private RegistryService registryService;

    private static TRpcListenerLoader tRpcListenerLoader;

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void startApplication() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_SNDBUF, 16 * 1024)
                .option(ChannelOption.SO_RCVBUF, 16 * 1024)
                .option(ChannelOption.SO_KEEPALIVE, true);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                System.out.println("初始化provider过程");
                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new RpcDecoder());
                ch.pipeline().addLast(new ServerHandler());
            }
        });
        this.batchExportUrl();
        bootstrap.bind(serverConfig.getServerPort()).sync();
    }

    public void initServerConfig() {
        ServerConfig serverConfig = PropertiesBootstrap.loadServerConfigFromLocal();
        this.setServerConfig(serverConfig);
        SERVER_CONFIG = this.serverConfig;
        // 序列化策略, 采用 SPI 进行加载
        String serverSerialize = serverConfig.getServerSerialize();
        try {
            EXTENSION_LOADER.loadExtension(SerializeFactory.class);
            LinkedHashMap<String, Class> serializeMap = EXTENSION_LOADER_CLASS_CACHE.get(SerializeFactory.class.getName());
            Class serializeClass = serializeMap.get(serverSerialize);
            SERVER_SERIALIZE_FACTORY = (SerializeFactory) serializeClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("serializeType unknown, error is ", e);
        }
        // 初始化过滤器链, 通过 SPI 加载
        SERVER_FILTER_CHAIN = new ServerFilterChain();
        try {
            EXTENSION_LOADER.loadExtension(IServerFilter.class);
            LinkedHashMap<String, Class> serverFilterMap = EXTENSION_LOADER_CLASS_CACHE.get(IServerFilter.class.getName());
            for (Map.Entry<String, Class> entry : serverFilterMap.entrySet()) {
                Class serverFilterClass = entry.getValue();
                SERVER_FILTER_CHAIN.addServerFilter((IServerFilter) serverFilterClass.newInstance());
            }
        } catch (Exception e) {
            throw new RuntimeException("server filter unknown, error is ", e);
        }
    }

    /**
     * 暴露服务信息
     *
     * @param serviceWrapper
     */
    public void exportService(ServiceWrapper serviceWrapper) {
        Object serviceBean = serviceWrapper.getServiceObj();
        if (serviceBean.getClass().getInterfaces().length == 0) {
            throw new RuntimeException("service must had interfaces!");
        }
        Class[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1) {
            throw new RuntimeException("service must only had one interfaces!");
        }
        if (registryService == null) {
            registryService = new ZookeeperRegister(serverConfig.getRegisterAddr());
        }
        //默认选择该对象的第一个实现接口
        Class interfaceClass = classes[0];
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);
        URL url = new URL();
        url.setServiceName(interfaceClass.getName());
        url.setApplicationName(serverConfig.getApplicationName());
        url.addParam("host", CommonUtils.getIpAddress());
        url.addParam("port", String.valueOf(serverConfig.getServerPort()));
        url.addParam("group", serviceWrapper.getGroup());
        url.addParam("limit", String.valueOf(serviceWrapper.getLimit()));
        PROVIDER_URL_SET.add(url);
        if (CommonUtils.isNotEmpty(serviceWrapper.getServiceToken())) {
            PROVIDER_SERVICE_WRAPPER_MAP.put(interfaceClass.getName(), serviceWrapper);
        }
    }

    public void batchExportUrl(){
        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (URL url : PROVIDER_URL_SET) {
                    registryService.register(url);
                }
            }
        });
        task.start();
    }


    public static void main(String[] args) throws InterruptedException {
        //Server server = new Server();
        //server.initServerConfig();
        //server.exportService(new DataServiceImpl());
        //server.startApplication();
        // 引入了分组和 Token 鉴权, 因此服务需要设置更多信息
        Server server = new Server();
        server.initServerConfig();
        tRpcListenerLoader = new TRpcListenerLoader();
        tRpcListenerLoader.init();
        ServiceWrapper dataServiceServiceWrapper = new ServiceWrapper(new DataServiceImpl(), "dev");
        dataServiceServiceWrapper.setServiceToken("token-a");
        dataServiceServiceWrapper.setLimit(2);
        server.exportService(dataServiceServiceWrapper);
        ApplicationShutdownHook.registryShutdownHook();
        server.startApplication();
    }
}
