package org.trpc.framework.core.common.event;

/**
 * @author Trust会长
 * @Date 2023/5/6
 */
public class TRpcNodeChangeEvent implements TRpcEvent {
    private Object data;

    public TRpcNodeChangeEvent(Object data) {
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
