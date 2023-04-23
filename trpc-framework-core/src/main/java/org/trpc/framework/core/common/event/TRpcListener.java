package org.trpc.framework.core.common.event;

/**
 * 事件监听器
 * @param <T>
 * @author Trust会长
 * @Date 2023/4/23
 */
public interface TRpcListener<T> {
    void callback(Object t);
}
