package org.trpc.framework.core.registry.zookeeper;

import org.apache.zookeeper.Watcher;

import java.util.List;

/**
 * 抽象的访问 Zookeeper 的客户端类
 *
 * @author Trust会长
 * @Date 2023/4/23
 */
public abstract class AbstractZookeeperClient {

    private String zkAddress;
    private int baseSleepTimes;
    private int maxRetryTimes;

    public AbstractZookeeperClient(String zkAddress) {
        this.zkAddress = zkAddress;
        //默认3000ms
        this.baseSleepTimes = 1000;
        this.maxRetryTimes = 3;
    }

    public AbstractZookeeperClient(String zkAddress, Integer baseSleepTimes, Integer maxRetryTimes) {
        this.zkAddress = zkAddress;
        if (baseSleepTimes == null) {
            this.baseSleepTimes = 1000;
        } else {
            this.baseSleepTimes = baseSleepTimes;
        }
        if (maxRetryTimes == null) {
            this.maxRetryTimes = 3;
        } else {
            this.maxRetryTimes = maxRetryTimes;
        }
    }

    public int getBaseSleepTimes() {
        return baseSleepTimes;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setBaseSleepTimes(int baseSleepTimes) {
        this.baseSleepTimes = baseSleepTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public abstract void updateNodeData(String address, String data);

    public abstract Object getClient();

    /**
     * 拉取节点的数据
     *
     * @param path 节点的路径
     * @return 节点的数据
     */
    public abstract String getNodeData(String path);

    /**
     * 获取指定目录下的子节点数据
     *
     * @param path 父节点的路径
     * @return 所有子节点的数据
     */
    public abstract List<String> getChildrenData(String path);

    /**
     * 创建持久化类型节点数据信息
     *
     * @param address 地址
     * @param data 数据
     */
    public abstract void createPersistentData(String address, String data);

    /**
     * 用序列化数据创建持久化类型节点数据信息
     *
     * @param address 地址
     * @param data 数据
     */
    public abstract void createPersistentWithSeqData(String address, String data);


    /**
     * 创建有序且临时类型节点数据信息
     *
     * @param address 地址
     * @param data 数据
     */
    public abstract void createTemporarySeqData(String address, String data);


    /**
     * 创建临时节点数据类型信息
     *
     * @param address 地址
     * @param data 数据
     */
    public abstract void createTemporaryData(String address, String data);

    /**
     * 设置某个节点的数值
     *
     * @param address 地址
     * @param data 数据
     */
    public abstract void setTemporaryData(String address, String data);

    /**
     * 断开zk的客户端链接
     */
    public abstract void destroy();


    /**
     * 展示节点下边的数据
     *
     * @param address 地址
     */
    public abstract List<String> listNode(String address);


    /**
     * 删除节点下边的数据
     *
     * @param address 地址
     * @return 删除是否成功
     */
    public abstract boolean deleteNode(String address);


    /**
     * 判断是否存在其他节点
     *
     * @param address 地址
     * @return 是否存在其他节点
     */
    public abstract boolean existNode(String address);


    /**
     * 监听path路径下某个节点的数据变化
     *
     * @param path 路径
     */
    public abstract void watchNodeData(String path, Watcher watcher);


    /**
     * 监听子节点下的数据变化
     *
     * @param path 路径
     * @param watcher
     */
    public abstract void watchChildNodeData(String path, Watcher watcher);

}
