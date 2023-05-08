package org.trpc.framework.core.config;

import java.io.IOException;

public class PropertiesBootstrap {

    private volatile boolean configIsReady;
    public static final String SERVER_PORT = "trpc.serverPort";
    public static final String REGISTER_ADDRESS = "trpc.registerAddr";
    public static final String APPLICATION_NAME = "trpc.applicationName";
    public static final String PROXY_TYPE = "trpc.proxyType";
    public static final String ROUTER_STRATEGY = "trpc.routerStrategy";
    public static final String CLIENT_SERIALIZE = "trpc.clientSerialize";
    public static final String SERVER_SERIALIZE = "trpc.serverSerialize";
    public static final String REGISTRY_TYPE = "trpc.registry";
    public static final String BUSINESS_THREAD_NUM = "trpc.business.thread.num";
    public static final String BLOCKING_QUEUE_LENGTH = "trpc.blocking.queue.length";
    public static final String SERVER_REQUEST_DATA = "trpc.server.request.data";

    public static ServerConfig loadServerConfigFromLocal() {
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("loadServerConfigFromLocal fail,e is {}", e);
        }
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerPort(PropertiesLoader.getPropertiesInteger(SERVER_PORT));
        serverConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        serverConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        serverConfig.setServerSerialize(PropertiesLoader.getPropertiesStr(SERVER_SERIALIZE));
        serverConfig.setBusinessThreadNum(PropertiesLoader.getPropertiesInteger(BUSINESS_THREAD_NUM));
        serverConfig.setBlockingQueueLength(PropertiesLoader.getPropertiesInteger(BLOCKING_QUEUE_LENGTH));
        serverConfig.setMaxServerRequestData(PropertiesLoader.getPropertiesInteger(SERVER_REQUEST_DATA));
        return serverConfig;
    }

    public static ClientConfig loadClientConfigFromLocal(){
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("loadClientConfigFromLocal fail,e is {}", e);
        }
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        clientConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        clientConfig.setProxyType(PropertiesLoader.getPropertiesStr(PROXY_TYPE));
        clientConfig.setRouterStrategy(PropertiesLoader.getPropertiesStr(ROUTER_STRATEGY));
        clientConfig.setClientSerialize(PropertiesLoader.getPropertiesStr(CLIENT_SERIALIZE));
        clientConfig.setRegistryType(PropertiesLoader.getPropertiesStr(REGISTRY_TYPE));
        return clientConfig;
    }

}
