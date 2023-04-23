package org.trpc.framework.core.common.event;

import org.trpc.framework.core.common.event.listener.ServiceUpdateListener;
import org.trpc.framework.core.common.util.CommonUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TRpcListenerLoader {

    private static List<TRpcListener> iRpcListenerList = new ArrayList<>();

    private static ExecutorService eventThreadPool = Executors.newFixedThreadPool(2);

    public static void registerListener(TRpcListener iRpcListener) {
        iRpcListenerList.add(iRpcListener);
    }

    public void init() {
        registerListener(new ServiceUpdateListener());
    }

    /**
     * 获取接口上的泛型T
     *
     * @param o 接口
     */
    public static Class<?> getInterfaceT(Object o) {
        Type[] types = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) types[0];
        Type type = parameterizedType.getActualTypeArguments()[0];
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }

    public static void sendEvent(TRpcEvent tRpcEvent) {
        if(CommonUtils.isEmptyList(iRpcListenerList)){
            return;
        }
        for (TRpcListener<?> tRpcListener : iRpcListenerList) {
            Class<?> type = getInterfaceT(tRpcListener);
            if(type.equals(tRpcEvent.getClass())){
                eventThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tRpcListener.callback(tRpcEvent.getData());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

}
