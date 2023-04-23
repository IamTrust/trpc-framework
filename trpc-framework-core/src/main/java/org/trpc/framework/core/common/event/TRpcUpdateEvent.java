package org.trpc.framework.core.common.event;

/**
 * 节点更新事件
 *
 * @author Trust会长
 * @Date 2023/4/23
 */
public class TRpcUpdateEvent implements TRpcEvent {
    private Object data;

    public TRpcUpdateEvent(Object data) {
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public TRpcEvent setData(Object data) {
        this.data = data;
        return this;
    }
}
