package org.trpc.framework.core.config;

/**
 * 客户端配置，用于配置测试的客户端
 *
 * @author Trust会长
 * @Date 2023/4/20
 */
public class ClientConfig {
    private String serverAddr;
    private int port;

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
