package org.trpc.framework.spring.starter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.trpc.framework.core.common.event.TRpcListenerLoader;
import org.trpc.framework.core.server.ApplicationShutdownHook;
import org.trpc.framework.core.server.Server;
import org.trpc.framework.core.server.ServiceWrapper;
import org.trpc.framework.spring.starter.annotation.TRpcService;

import java.util.Map;

/**
 * 服务端的自动装配类会通过Spring上下文将带有@IRpcService注解的对象提取出来，
 * 然后调用TRPC框架内所提供的api进行服务暴露操作
 *
 * @author Trust会长
 * @Date 2023/5/8
 */
public class TRpcServerAutoConfiguration implements InitializingBean, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(TRpcServerAutoConfiguration.class);

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Server server = null;
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(TRpcService.class);
        if (beanMap.size() == 0) {
            //说明当前应用内部不需要对外暴露服务，无需执行下边多余的逻辑
            return;
        }
        //输出banner图案
        printBanner();
        long begin = System.currentTimeMillis();
        server = new Server();
        server.initServerConfig();
        TRpcListenerLoader iRpcListenerLoader = new TRpcListenerLoader();
        iRpcListenerLoader.init();
        for (String beanName : beanMap.keySet()) {
            Object bean = beanMap.get(beanName);
            TRpcService tRpcService = bean.getClass().getAnnotation(TRpcService.class);
            ServiceWrapper dataServiceServiceWrapper = new ServiceWrapper(bean, tRpcService.group());
            dataServiceServiceWrapper.setServiceToken(tRpcService.serviceToken());
            dataServiceServiceWrapper.setLimit(tRpcService.limit());
            server.exportService(dataServiceServiceWrapper);
            LOGGER.info(">>>>>>>>>>>>>>> [trpc] {} export success! >>>>>>>>>>>>>>> ",beanName);
        }
        long end = System.currentTimeMillis();
        ApplicationShutdownHook.registryShutdownHook();
        server.startApplication();
        LOGGER.info(" ================== [{}] started success in {}s ================== ",server.getServerConfig().getApplicationName(),((double)end-(double)begin)/1000);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void printBanner(){
        System.out.println();
        System.out.println("==============================================");
        System.out.println("|||---------- TRpc Starting Now! ----------|||");
        System.out.println("==============================================");
        System.out.println("version: 1.0.0");
        System.out.println();
    }
}
