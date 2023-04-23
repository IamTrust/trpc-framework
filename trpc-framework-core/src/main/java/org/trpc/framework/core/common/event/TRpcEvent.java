package org.trpc.framework.core.common.event;

/**
 * 事件
 *
 * @author Trust会长
 * @Date 2023/4/23
 */
public interface TRpcEvent {
    Object getData();

    TRpcEvent setData(Object data);
}
