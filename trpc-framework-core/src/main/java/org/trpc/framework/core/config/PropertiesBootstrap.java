package org.trpc.framework.core.config;

import org.trpc.framework.core.common.util.CommonUtils;

import java.io.IOException;

import static org.trpc.framework.core.common.constant.RpcConstants.*;

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

    public static ServerConfig loadServerConfigFromLocal() {
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("loadServerConfigFromLocal fail,e is {}", e);
        }
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerPort(
                PropertiesLoader.getPropertiesInteger(SERVER_PORT) == null ?
                        DEFAULT_PORT :
                        PropertiesLoader.getPropertiesInteger(SERVER_PORT)
        );
        serverConfig.setApplicationName(
                CommonUtils.isEmpty(PropertiesLoader.getPropertiesStr(APPLICATION_NAME)) ?
                        DEFAULT_APPLICATION_NAME :
                        PropertiesLoader.getPropertiesStr(APPLICATION_NAME)
        );
        serverConfig.setRegisterAddr(
                CommonUtils.isEmpty(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS)) ?
                        DEFAULT_REGISTRY_ADDR :
                        PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS)
        );
        serverConfig.setServerSerialize(
                CommonUtils.isEmpty(PropertiesLoader.getPropertiesStr(SERVER_SERIALIZE)) ?
                        DEFAULT_SERVER_SERIALIZE_TYPE :
                        PropertiesLoader.getPropertiesStr(SERVER_SERIALIZE)
        );
        serverConfig.setBusinessThreadNum(
                PropertiesLoader.getPropertiesInteger(BUSINESS_THREAD_NUM) == null ?
                        DEFAULT_BUSINESS_THREAD_NUM :
                        PropertiesLoader.getPropertiesInteger(BUSINESS_THREAD_NUM)
        );
        serverConfig.setBlockingQueueLength(
                PropertiesLoader.getPropertiesInteger(BLOCKING_QUEUE_LENGTH) == null ?
                        DEFAULT_BLOCKING_QUEUE_LEN :
                        PropertiesLoader.getPropertiesInteger(BLOCKING_QUEUE_LENGTH)
        );
        return serverConfig;
    }

    public static ClientConfig loadClientConfigFromLocal(){
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("loadClientConfigFromLocal fail,e is {}", e);
        }
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setApplicationName(
                CommonUtils.isEmpty(PropertiesLoader.getPropertiesStr(APPLICATION_NAME)) ?
                        DEFAULT_APPLICATION_NAME :
                        PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        clientConfig.setRegisterAddr(
                CommonUtils.isEmpty(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS)) ?
                        DEFAULT_REGISTRY_ADDR :
                        PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        clientConfig.setProxyType(
                CommonUtils.isEmpty(PropertiesLoader.getPropertiesStr(PROXY_TYPE)) ?
                        DEFAULT_PROXY_TYPE :
                        PropertiesLoader.getPropertiesStr(PROXY_TYPE));
        clientConfig.setRouterStrategy(
                CommonUtils.isEmpty(PropertiesLoader.getPropertiesStr(ROUTER_STRATEGY)) ?
                        DEFAULT_ROUTER_STRATEGY :
                        PropertiesLoader.getPropertiesStr(ROUTER_STRATEGY)
                );
        clientConfig.setClientSerialize(
                CommonUtils.isEmpty(PropertiesLoader.getPropertiesStr(CLIENT_SERIALIZE)) ?
                        DEFAULT_CLIENT_SERIALIZE_TYPE :
                        PropertiesLoader.getPropertiesStr(CLIENT_SERIALIZE)
        );
        clientConfig.setRegistryType(
                CommonUtils.isEmpty(PropertiesLoader.getPropertiesStr(REGISTRY_TYPE)) ?
                        DEFAULT_REGISTRY_TYPE :
                        PropertiesLoader.getPropertiesStr(REGISTRY_TYPE)
        );
        return clientConfig;
    }

}
