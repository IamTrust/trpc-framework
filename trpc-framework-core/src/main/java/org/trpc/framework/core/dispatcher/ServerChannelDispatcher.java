package org.trpc.framework.core.dispatcher;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.trpc.framework.core.common.RpcInvocation;
import org.trpc.framework.core.common.RpcProtocol;
import org.trpc.framework.core.server.ServerChannelReadData;

import java.lang.reflect.Method;
import java.util.concurrent.*;

import static org.trpc.framework.core.common.cache.CommonServerCache.*;

/**
 * 请求分发器
 * 当请求抵达服务端的时候，IO线程交给请求分发器，由请求分发器负责将请求分发给业务线程进行处理
 * 即用于 IO 线程和业务线程分离，异步处理请求，提高并发度
 *
 * @author Trust会长
 * @Date 2023/5/7
 */
public class ServerChannelDispatcher {
    /**
     * 阻塞队列
     * IO线程将请求放入阻塞队列一端, 线程池从队列另一段消费
     */
    private BlockingQueue<ServerChannelReadData> RPC_DATA_QUEUE;
    /**
     * 线程池
     */
    private ExecutorService executorService;

    public void init(int queueLength, int businessThreadNum) {
        RPC_DATA_QUEUE = new ArrayBlockingQueue<>(queueLength);
        executorService = new ThreadPoolExecutor(
                businessThreadNum, // 核心线程和最大线程一样，即没有更多的线程
                businessThreadNum,
                0L,   // 超过核心线程的线程的存活时间，没有
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(512));
    }

    /**
     * 添加任务到队列
     * @param serverChannelReadData
     */
    public void add(ServerChannelReadData serverChannelReadData) {
        RPC_DATA_QUEUE.add(serverChannelReadData);
    }

    /**
     * 消费阻塞队列中的任务
     */
    class ServerJobCoreHandle implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    ServerChannelReadData serverChannelReadData = RPC_DATA_QUEUE.take();
                    executorService.submit(() -> {
                        try {
                            RpcProtocol rpcProtocol = serverChannelReadData.getRpcProtocol();
                            ChannelHandlerContext ctx = serverChannelReadData.getChannelHandlerContext();
                            RpcInvocation rpcInvocation = SERVER_SERIALIZE_FACTORY.deserialize(rpcProtocol.getContent(), RpcInvocation.class);

                            // 执行过滤链路
                            SERVER_FILTER_CHAIN.doServerFilter(rpcInvocation);

                            Object aimObject = PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
                            Method[] methods = aimObject.getClass().getDeclaredMethods();
                            Object result = null;
                            for (Method method : methods) {
                                if (method.getName().equals(rpcInvocation.getTargetMethod())) {
                                    if (method.getReturnType().equals(Void.TYPE)) {
                                        try {
                                            method.invoke(aimObject, rpcInvocation.getArgs());
                                        } catch (Throwable e) {
                                            // 方法调用抛出的异常
                                            rpcInvocation.setE(e);
                                        }
                                    } else {
                                        try {
                                            result = method.invoke(aimObject, rpcInvocation.getArgs());
                                        } catch (Throwable e) {
                                            rpcInvocation.setE(e);
                                        }
                                    }
                                    break;
                                }
                            }
                            rpcInvocation.setResponse(result);
                            // TODO 这里的序列化应该也要用序列化层来做
                            RpcProtocol respRpcProtocol = new RpcProtocol(JSON.toJSONString(rpcInvocation).getBytes());
                            ctx.writeAndFlush(respRpcProtocol);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startDataConsume() {
        // 这个线程专门负责提交任务给线程池
        new Thread(new ServerJobCoreHandle()).start();
    }
}
