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
import org.trpc.framework.core.common.util.CommonUtils;
import org.trpc.framework.core.config.PropertiesBootstrap;
import org.trpc.framework.core.config.ServerConfig;
import org.trpc.framework.core.registry.RegistryService;
import org.trpc.framework.core.registry.URL;
import org.trpc.framework.core.registry.zookeeper.ZookeeperRegister;
import org.trpc.framework.core.serialize.fastjson.FastJsonSerializeFactory;
import org.trpc.framework.core.serialize.jdk.JDKSerializeFactory;

import static org.trpc.framework.core.cache.CommonServerCache.*;
import static org.trpc.framework.core.constant.RpcConstants.FAST_JSON_SERIALIZE;
import static org.trpc.framework.core.constant.RpcConstants.JDK_SERIALIZE;

/**
 * 测试用远程调用服务端
 */
public class Server {
    private static EventLoopGroup bossGroup = null;

    private static EventLoopGroup workerGroup = null;

    private ServerConfig serverConfig;

    private RegistryService registryService;

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
        // 序列化策略
        String serverSerialize = serverConfig.getServerSerialize();
        switch (serverSerialize) {
            case JDK_SERIALIZE:
                SERVER_SERIALIZE_FACTORY = new JDKSerializeFactory();
                break;
            case FAST_JSON_SERIALIZE:
                SERVER_SERIALIZE_FACTORY = new FastJsonSerializeFactory();
                break;
            default:
                throw new RuntimeException("不支持的序列化策略: " + serverSerialize);
        }
    }

    /**
     * 暴露服务信息
     *
     * @param serviceBean
     */
    public void exportService(Object serviceBean) {
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
        PROVIDER_URL_SET.add(url);
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
        Server server = new Server();
        server.initServerConfig();
        server.exportService(new DataServiceImpl());
        server.startApplication();
    }
}
