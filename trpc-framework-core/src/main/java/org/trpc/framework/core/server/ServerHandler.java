package org.trpc.framework.core.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.trpc.framework.core.common.RpcProtocol;

import static org.trpc.framework.core.common.cache.CommonServerCache.*;

/**
 * 请求处理
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RpcProtocol rpcProtocol = (RpcProtocol) msg;
        ServerChannelReadData serverChannelReadData = new ServerChannelReadData(rpcProtocol, ctx);
        // 异步处理请求
        SERVER_CHANNEL_DISPATCHER.add(serverChannelReadData);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }
}
