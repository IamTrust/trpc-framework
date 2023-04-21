package org.trpc.framework.core.proxy.jdk;

import org.trpc.framework.core.common.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.trpc.framework.core.cache.CommonClientCache.RESP_MAP;
import static org.trpc.framework.core.cache.CommonClientCache.SEND_QUEUE;

/**
 * JDK 动态代理的三个参数：1-目标类实现的接口，2-目标类的类加载器，3-代理后的具体处理
 * 这个类即定义 JDK 动态代理的具体处理
 * 主要为向服务端发起远程调用
 *
 * @author Trust会长
 * @Date 2023/4/20
 */
public class JDKClientInvocationHandler implements InvocationHandler {
    private final static Object OBJECT = new Object();

    private Class<?> clazz;

    public JDKClientInvocationHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setArgs(args);
        rpcInvocation.setTargetMethod(method.getName());
        rpcInvocation.setTargetServiceName(clazz.getName());
        rpcInvocation.setUuid(UUID.randomUUID().toString());
        RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);
        //加入发送队列，此队列是阻塞队列，由专门的发送线程负责与服务端通信。
        //队列为空则发送线程会阻塞，队列不为空时发送线程会从队列中取出元素进行发送
        SEND_QUEUE.add(rpcInvocation);
        long beginTime = System.currentTimeMillis();
        //收到响应后在netty框架的响应处理中会将响应信息加入RESP_MAP，因此从该map中get到对应uuid的信息就说明响应到达了
        while (System.currentTimeMillis() - beginTime < 3*1000) { // 超时时间为3秒，后期用配置文件调整
            Object object = RESP_MAP.get(rpcInvocation.getUuid());
            if (object instanceof RpcInvocation) {
                return ((RpcInvocation)object).getResponse();
            }
        }
        throw new TimeoutException("client wait server's response timeout!");
    }
}
