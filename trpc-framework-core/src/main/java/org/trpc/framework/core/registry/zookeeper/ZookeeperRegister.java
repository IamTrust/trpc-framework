package org.trpc.framework.core.registry.zookeeper;

import com.alibaba.fastjson.JSON;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.trpc.framework.core.common.event.TRpcEvent;
import org.trpc.framework.core.common.event.TRpcListenerLoader;
import org.trpc.framework.core.common.event.TRpcNodeChangeEvent;
import org.trpc.framework.core.common.event.TRpcUpdateEvent;
import org.trpc.framework.core.common.event.data.URLChangeWrapper;
import org.trpc.framework.core.registry.AbstractRegister;
import org.trpc.framework.core.registry.RegistryService;
import org.trpc.framework.core.registry.URL;

import java.util.List;

/**
 * 采用 Zookeeper 实现服务注册
 *
 * @author Trust会长
 * @Date 2023/4/23
 */
public class ZookeeperRegister extends AbstractRegister implements RegistryService {
    private AbstractZookeeperClient zkClient;

    private static final String ROOT = "/trpc";

    private String getProviderPath(URL url) {
        return ROOT + "/" + url.getServiceName() + "/provider/"
                + url.getParams().get("host") + ":" + url.getParams().get("port");
    }

    private String getConsumerPath(URL url) {
        return ROOT + "/" + url.getServiceName() + "/consumer/"
                + url.getApplicationName() + ":" + url.getParams().get("host")+":";
    }

    public ZookeeperRegister(String address) {
        this.zkClient = new CuratorZookeeperClient(address);
    }

    @Override
    public void register(URL url) {
        if (!this.zkClient.existNode(ROOT)) {
            zkClient.createPersistentData(ROOT, "");
        }
        String urlStr = URL.buildProviderUrlStr(url);
        if (!zkClient.existNode(getProviderPath(url))) {
            zkClient.createTemporaryData(getProviderPath(url), urlStr);
        } else {
            zkClient.deleteNode(getProviderPath(url));
            zkClient.createTemporaryData(getProviderPath(url), urlStr);
        }
        super.register(url);
    }

    @Override
    public void unRegister(URL url) {
        zkClient.deleteNode(getProviderPath(url));
        super.unRegister(url);
    }

    @Override
    public void subscribe(URL url) {
        if (!this.zkClient.existNode(ROOT)) {
            zkClient.createPersistentData(ROOT, "");
        }
        String urlStr = URL.buildConsumerUrlStr(url);
        if (!zkClient.existNode(getConsumerPath(url))) {
            zkClient.createTemporarySeqData(getConsumerPath(url), urlStr);
        } else {
            zkClient.deleteNode(getConsumerPath(url));
            zkClient.createTemporarySeqData(getConsumerPath(url), urlStr);
        }
        super.subscribe(url);
    }

    @Override
    public void doAfterSubscribe(URL url) {
        //监听是否有新的服务注册
        //String newServerNodePath = ROOT + "/" + url.getServiceName() + "/provider";
        //watchChildNodeData(newServerNodePath);

        //监听是否有新的服务注册
        String servicePath = url.getParams().get("servicePath");
        String newServerNodePath = ROOT + "/" + servicePath;
        watchChildNodeData(newServerNodePath);
        String providerIpStrJson = url.getParams().get("providerIps");
        List<String> providerIpList = JSON.parseObject(providerIpStrJson, List.class);
        for (String providerIp : providerIpList) {
            this.watchNodeDataChange(ROOT + "/" + servicePath + "/" + providerIp);
        }
    }

    /**
     * 订阅服务节点内部的数据变化
     *
     * @param newServerNodePath
     */
    public void watchNodeDataChange(String newServerNodePath) {
        zkClient.watchNodeData(newServerNodePath, watchedEvent -> {
            String path = watchedEvent.getPath();
            String nodeData = zkClient.getNodeData(path);
            nodeData = nodeData.replace(";","/");
            ProviderNodeInfo providerNodeInfo = URL.buildURLFromUrlStr(nodeData);
            TRpcEvent iRpcEvent = new TRpcNodeChangeEvent(providerNodeInfo);
            TRpcListenerLoader.sendEvent(iRpcEvent);
            watchNodeDataChange(newServerNodePath);
        });
    }

    public void watchChildNodeData(String newServerNodePath){
        zkClient.watchChildNodeData(newServerNodePath, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent);
                String path = watchedEvent.getPath();
                List<String> childrenDataList = zkClient.getChildrenData(path);
                URLChangeWrapper urlChangeWrapper = new URLChangeWrapper();
                urlChangeWrapper.setProviderUrl(childrenDataList);
                urlChangeWrapper.setServiceName(path.split("/")[2]);
                //自定义的一套事件监听组件
                TRpcEvent iRpcEvent = new TRpcUpdateEvent(urlChangeWrapper);
                TRpcListenerLoader.sendEvent(iRpcEvent);
                //收到回调之后在注册一次监听，这样能保证一直都收到消息
                watchChildNodeData(path);
            }
        });
    }

    @Override
    public void doBeforeSubscribe(URL url) {

    }

    @Override
    public List<String> getProviderIps(String serviceName) {
        return this.zkClient.getChildrenData(ROOT + "/" + serviceName + "/provider");
    }

    @Override
    public void doUnSubscribe(URL url) {
        this.zkClient.deleteNode(getConsumerPath(url));
        super.doUnSubscribe(url);
    }
}
