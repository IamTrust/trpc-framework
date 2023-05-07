package org.trpc.framework.core.common.event;

/**
 * 服务销毁事件
 *
 * @Author linhao
 * @Date created in 3:20 下午 2022/1/8
 */
public class TRpcDestroyEvent implements TRpcEvent{

    private Object data;

    public TRpcDestroyEvent(Object data) {
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
