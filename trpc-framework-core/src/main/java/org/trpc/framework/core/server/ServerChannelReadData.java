package org.trpc.framework.core.server;

import io.netty.channel.ChannelHandlerContext;
import org.trpc.framework.core.common.RpcProtocol;

/**
 * 封装请求对象
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public class ServerChannelReadData {

    private RpcProtocol rpcProtocol;

    private ChannelHandlerContext channelHandlerContext;

    public ServerChannelReadData() {
    }

    public ServerChannelReadData(RpcProtocol rpcProtocol, ChannelHandlerContext channelHandlerContext) {
        this.rpcProtocol = rpcProtocol;
        this.channelHandlerContext = channelHandlerContext;
    }

    public RpcProtocol getRpcProtocol() {
        return rpcProtocol;
    }

    public void setRpcProtocol(RpcProtocol rpcProtocol) {
        this.rpcProtocol = rpcProtocol;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
}
