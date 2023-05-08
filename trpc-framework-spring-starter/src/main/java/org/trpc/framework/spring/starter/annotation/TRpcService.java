package org.trpc.framework.spring.starter.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 用于服务提供者暴露服务
 *
 * @author Trust会长
 * @Date 2023/5/8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface TRpcService {
    /**
     * 服务访问限流
     */
    int limit() default 0;

    /**
     * 服务分组
     */
    String group() default "default";

    /**
     * 服务访问鉴权
     */
    String serviceToken() default "";
}
