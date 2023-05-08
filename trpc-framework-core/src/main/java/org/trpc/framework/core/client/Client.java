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
import org.trpc.framework.core.filter.IClientFilter;
import org.trpc.framework.core.proxy.ProxyFactory;
import org.trpc.framework.core.registry.AbstractRegister;
import org.trpc.framework.core.registry.RegistryService;
import org.trpc.framework.core.registry.URL;
import org.trpc.framework.core.router.IRouter;
import org.trpc.framework.core.serialize.SerializeFactory;
import org.trpc.framework.interfaces.DataService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.trpc.framework.core.common.cache.CommonClientCache.*;
import static org.trpc.framework.core.spi.ExtensionLoader.EXTENSION_LOADER_CLASS_CACHE;

/**
 * 远程调用客户端
 *
 * @author Trust会长
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
        CLIENT_CONFIG = this.clientConfig;
        RpcReference rpcReference;
        //使用自定义SPI机制去加载动态代理的配置
        try {
            EXTENSION_LOADER.loadExtension(ProxyFactory.class);
            LinkedHashMap<String, Class> proxyMap = EXTENSION_LOADER_CLASS_CACHE.get(ProxyFactory.class.getName());
            Class proxyClass = proxyMap.get(clientConfig.getProxyType());
            rpcReference = new RpcReference((ProxyFactory)proxyClass.newInstance());
        } catch (Exception e) {
            throw new RuntimeException("proxyType unknown, error is ", e);
        }
        return rpcReference;
    }

    /**
     * 启动服务之前需要预先订阅对应的注册中心中的服务
     *
     * @param serviceBean
     */
    public void doSubscribeService(Class serviceBean) {
        if (abstractRegister == null) {
            try {
                //使用自定义的SPI机制去加载注册中心的配置
                EXTENSION_LOADER.loadExtension(RegistryService.class);
                Map<String, Class> registerMap = EXTENSION_LOADER_CLASS_CACHE.get(RegistryService.class.getName());
                Class registerClass =  registerMap.get(clientConfig.getRegistryType());
                //真正实例化对象的位置
                abstractRegister = (AbstractRegister) registerClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("registryServiceType unknown, error is ", e);
            }
        }
        URL url = new URL();
        url.setApplicationName(clientConfig.getApplicationName());
        url.setServiceName(serviceBean.getName());
        url.addParam("host", CommonUtils.getIpAddress());
        Map<String, String> result = abstractRegister.getServiceWeightMap(serviceBean.getName());
        URL_MAP.put(serviceBean.getName(), result);
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
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(data);
                    if (channelFuture != null) {
                        RpcProtocol rpcProtocol = new RpcProtocol(CLIENT_SERIALIZE_FACTORY.serialize(data));
                        channelFuture.channel().writeAndFlush(rpcProtocol);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initClientConfig() {
        // 初始化路由负载均衡策略, 采用 SPI 进行加载
        String routerStrategy = clientConfig.getRouterStrategy();
        try {
            EXTENSION_LOADER.loadExtension(IRouter.class);
            LinkedHashMap<String, Class> routerMap = EXTENSION_LOADER_CLASS_CACHE.get(IRouter.class.getName());
            Class routerClass = routerMap.get(routerStrategy);
            IROUTER = (IRouter) routerClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("routerStrategy unknown, error is ", e);
        }
        // 初始化序列化策略, 采用 SPI 进行加载
        String clientSerialize = clientConfig.getClientSerialize();
        try {
            EXTENSION_LOADER.loadExtension(SerializeFactory.class);
            LinkedHashMap<String, Class> serializeMap = EXTENSION_LOADER_CLASS_CACHE.get(SerializeFactory.class.getName());
            Class serializeClass = serializeMap.get(clientSerialize);
            CLIENT_SERIALIZE_FACTORY = (SerializeFactory) serializeClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("serializeType unknown, error is ", e);
        }
        // 初始化过滤器链, 采用 SPI 进行加载
        CLIENT_FILTER_CHAIN = new ClientFilterChain();
        try {
            EXTENSION_LOADER.loadExtension(IClientFilter.class);
            LinkedHashMap<String, Class> clientFilterMap = EXTENSION_LOADER_CLASS_CACHE.get(IClientFilter.class.getName());
            // 遍历 clientFilterMap , 将其中所有 clientFilter 加入 CLIENT_FILTER_CHAIN
            for (Map.Entry<String, Class> entry : clientFilterMap.entrySet()) {
                Class clientFilterClass = entry.getValue();
                CLIENT_FILTER_CHAIN.addClientFilter((IClientFilter) clientFilterClass.newInstance());
            }
        } catch (Exception e) {
            throw new RuntimeException("client filter unknown, error is ", e);
        }
    }

    public static void main(String[] args) throws Throwable {
        Client client = new Client();
        RpcReference rpcReference = client.initClientApplication();
        client.initClientConfig();
        // 引入了分组和 Token 鉴权, 因此服务需要设置更多信息
        RpcReferenceWrapper<DataService> rpcReferenceWrapper = new RpcReferenceWrapper<>();
        rpcReferenceWrapper.setGroup("dev");
        rpcReferenceWrapper.setAimClass(DataService.class);
        rpcReferenceWrapper.setServiceToken("token-a");
        rpcReferenceWrapper.setAsync(false); // 是否异步调用
        DataService dataService = rpcReference.get(rpcReferenceWrapper);
        client.doSubscribeService(DataService.class);
        ConnectionHandler.setBootstrap(client.getBootstrap());
        client.doConnectServer();
        client.startClient();
        //for (int i = 0; i < 100; i++) {
        //    try {
        //        String result = dataService.sendData("test");
        //        System.out.println(result);
        //        Thread.sleep(1000);
        //    }catch (Exception e){
        //        e.printStackTrace();
        //    }
        //}
        String res = dataService.getDataWithException(false);
        System.out.println(res);
    }
}
