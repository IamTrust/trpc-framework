package org.trpc.framework.core.client;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trpc.framework.core.common.RpcDecoder;
import org.trpc.framework.core.common.RpcEncoder;
import org.trpc.framework.core.common.RpcInvocation;
import org.trpc.framework.core.common.RpcProtocol;
import org.trpc.framework.core.common.event.TRpcListenerLoader;
import org.trpc.framework.core.common.util.CommonUtils;
import org.trpc.framework.core.config.ClientConfig;
import org.trpc.framework.core.config.PropertiesBootstrap;
import org.trpc.framework.core.proxy.jdk.JDKProxyFactory;
import org.trpc.framework.core.registry.AbstractRegister;
import org.trpc.framework.core.registry.URL;
import org.trpc.framework.core.registry.zookeeper.ZookeeperRegister;
import org.trpc.framework.core.router.RandomRouterImpl;
import org.trpc.framework.core.router.RotateRouterImpl;
import org.trpc.framework.core.serialize.fastjson.FastJsonSerializeFactory;
import org.trpc.framework.core.serialize.jdk.JDKSerializeFactory;
import org.trpc.framework.interfaces.DataService;

import java.util.List;

import static org.trpc.framework.core.cache.CommonClientCache.*;
import static org.trpc.framework.core.constant.RpcConstants.*;

/**
 * 测试用远程调用客户端
 */
public class Client {
    private Logger logger = LoggerFactory.getLogger(Client.class);

    public static EventLoopGroup clientGroup = new NioEventLoopGroup();

    private ClientConfig clientConfig;

    private AbstractRegister abstractRegister;

    private TRpcListenerLoader iRpcListenerLoader;

    private Bootstrap bootstrap = new Bootstrap();

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }


    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public RpcReference initClientApplication() {
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new RpcDecoder());
                ch.pipeline().addLast(new ClientHandler());
            }
        });
        iRpcListenerLoader = new TRpcListenerLoader();
        iRpcListenerLoader.init();
        this.clientConfig = PropertiesBootstrap.loadClientConfigFromLocal();
        RpcReference rpcReference;
        if (JAVASSIST_PROXY_TYPE.equals(clientConfig.getProxyType())) {
            // TODO 实现 Javassist 动态代理
            //rpcReference = new RpcReference(new JavassistProxyFactory());
            throw new RuntimeException("Javaassist 动态代理未实现, 请使用 JDK 动态代理!");
        } else if (JDK_PROXY_TYPE.equals(clientConfig.getProxyType())) {
            rpcReference = new RpcReference(new JDKProxyFactory());
        } else {
            throw new RuntimeException("不支持的动态代理类型: " + clientConfig.getProxyType());
        }
        return rpcReference;
    }

    /**
     * 启动服务之前需要预先订阅对应的dubbo服务
     *
     * @param serviceBean
     */
    public void doSubscribeService(Class serviceBean) {
        if (abstractRegister == null) {
            abstractRegister = new ZookeeperRegister(clientConfig.getRegisterAddr());
        }
        URL url = new URL();
        url.setApplicationName(clientConfig.getApplicationName());
        url.setServiceName(serviceBean.getName());
        url.addParam("host", CommonUtils.getIpAddress());
        abstractRegister.subscribe(url);
    }

    /**
     * 开始和各个provider建立连接
     */
    public void doConnectServer() {
        for (URL providerURL : SUBSCRIBE_SERVICE_LIST) {
            List<String> providerIps = abstractRegister.getProviderIps(providerURL.getServiceName());
            for (String providerIp : providerIps) {
                try {
                    ConnectionHandler.connect(providerURL.getServiceName(), providerIp);
                } catch (InterruptedException e) {
                    logger.error("[doConnectServer] connect fail ", e);
                }
            }
            URL url = new URL();
            url.addParam("servicePath",providerURL.getServiceName()+"/provider");
            url.addParam("providerIps", JSON.toJSONString(providerIps));
            abstractRegister.doAfterSubscribe(url);
        }
    }


    /**
     * 开启发送线程
     *
     * @param
     */
    public void startClient() {
        Thread asyncSendJob = new Thread(new AsyncSendJob());
        asyncSendJob.start();
    }

    class AsyncSendJob implements Runnable {

        public AsyncSendJob() {
        }

        @Override
        public void run() {
            while (true) {
                try {
                    //阻塞模式
                    RpcInvocation data = SEND_QUEUE.take();
                    RpcProtocol rpcProtocol = new RpcProtocol(CLIENT_SERIALIZE_FACTORY.serialize(data));
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(data.getTargetServiceName());
                    channelFuture.channel().writeAndFlush(rpcProtocol);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initClientConfig() {
        // 初始化路由负载均衡策略
        String routerStrategy = clientConfig.getRouterStrategy();
        if (routerStrategy.equals(RANDOM_ROUTER_TYPE)) {
            IROUTER = new RandomRouterImpl();
        } else if (routerStrategy.equals(ROTATE_ROUTER_TYPE)) {
            IROUTER = new RotateRouterImpl();
        } else {
            throw new RuntimeException("不支持的路由层负载均衡策略: " + routerStrategy);
        }
        // 初始化序列化策略
        String clientSerialize = clientConfig.getClientSerialize();
        switch (clientSerialize) {
            case JDK_SERIALIZE:
                CLIENT_SERIALIZE_FACTORY = new JDKSerializeFactory();
                break;
            case FAST_JSON_SERIALIZE:
                CLIENT_SERIALIZE_FACTORY = new FastJsonSerializeFactory();
                break;
            default:
                throw new RuntimeException("不支持的序列化策略: " + clientSerialize);
        }
    }


    public static void main(String[] args) throws Throwable {
        Client client = new Client();
        RpcReference rpcReference = client.initClientApplication();
        client.initClientConfig();
        DataService dataService = rpcReference.get(DataService.class);
        client.doSubscribeService(DataService.class);
        ConnectionHandler.setBootstrap(client.getBootstrap());
        client.doConnectServer();
        client.startClient();
        for (int i = 0; i < 100; i++) {
            try {
                String result = dataService.sendData("test");
                System.out.println(result);
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
