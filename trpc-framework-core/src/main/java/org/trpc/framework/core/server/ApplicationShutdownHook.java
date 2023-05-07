package org.trpc.framework.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trpc.framework.core.common.event.TRpcDestroyEvent;
import org.trpc.framework.core.common.event.TRpcListenerLoader;

/**
 * 当 JVM 进程关闭时触发，记录日志
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public class ApplicationShutdownHook {
    public static Logger LOGGER = LoggerFactory.getLogger(ApplicationShutdownHook.class);

    /**
     * 注册一个shutdownHook的钩子，当jvm进程关闭的时候触发
     */
    public static void registryShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("[registryShutdownHook] ==== ");
            TRpcListenerLoader.sendSyncEvent(new TRpcDestroyEvent("destroy"));
            System.out.println("destroy");
        }));
    }
}
