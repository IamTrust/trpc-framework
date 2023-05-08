package org.trpc.framework.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static org.trpc.framework.core.common.constant.RpcConstants.DEFAULT_DECODE_CHAR;

/**
 * 序列化
 *
 * @author Trust会长
 * @Date 2023/4/20
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          RpcProtocol rpcProtocol,
                          ByteBuf byteBuf) throws Exception {
        byteBuf.writeShort(rpcProtocol.getMagicNumber());
        byteBuf.writeInt(rpcProtocol.getContentLength());
        byteBuf.writeBytes(rpcProtocol.getContent());
        // 协议尾部的终结符，解决粘包问题
        byteBuf.writeBytes(DEFAULT_DECODE_CHAR.getBytes());
    }
}
