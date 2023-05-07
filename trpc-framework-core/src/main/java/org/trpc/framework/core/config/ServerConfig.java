package org.trpc.framework.core.config;

/**
 * 服务器配置，配置用于测试的服务端
 *
 * @author Trust会长
 * @Date 2023/4/20
 */
public class ServerConfig {
    private Integer serverPort;

    private String registerAddr;

    private String applicationName;

    private String serverSerialize;

    private Integer blockingQueueLength;

    private Integer businessThreadNum;

    public String getRegisterAddr() {
        return registerAddr;
    }

    public void setRegisterAddr(String registerAddr) {
        this.registerAddr = registerAddr;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getServerSerialize() {
        return serverSerialize;
    }

    public void setServerSerialize(String serverSerialize) {
        this.serverSerialize = serverSerialize;
    }

    public Integer getBlockingQueueLength() {
        return blockingQueueLength;
    }

    public void setBlockingQueueLength(Integer blockingQueueLength) {
        this.blockingQueueLength = blockingQueueLength;
    }

    public Integer getBusinessThreadNum() {
        return businessThreadNum;
    }

    public void setBusinessThreadNum(Integer businessThreadNum) {
        this.businessThreadNum = businessThreadNum;
    }
}
