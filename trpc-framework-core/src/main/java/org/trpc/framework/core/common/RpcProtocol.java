package org.trpc.framework.core.common;

import java.io.Serializable;

import static org.trpc.framework.core.constant.RpcConstants.MAGIC_NUMBER;

/**
 * 数据协议
 */
public class RpcProtocol implements Serializable {
    private static final long serialVersionUID = 5359096060555795690L;
    /**
     * 魔数, 作用类似 java class 字节码的 cafe babe , 用于验证
     */
    private final short magicNumber = MAGIC_NUMBER;
    /**
     * 内容长度, 用于解决 TCP 的粘包、半包问题
     */
    private int contentLength;
    /**
     * 具体内容数据
     */
    private byte[] content;

    public RpcProtocol() {
    }

    public RpcProtocol(byte[] content) {
        this.content = content;
        this.contentLength = content.length;
    }

    public short getMagicNumber() {
        return magicNumber;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
