package org.trpc.framework.spring.starter.annotation;

import java.lang.annotation.*;

/**
 * 用于服务调用者调用远程的服务
 *
 * @author Trust会长
 * @Date 2023/5/8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TRpcReference {
    String url() default "";

    String group() default "default";

    String serviceToken() default "";

    int timeOut() default 3000;

    int retry() default 1;

    boolean async() default false;
}
