package com.Downshifting.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={TYPE})
public @interface RpcService {
    String version() default "1.0";

    Class<?> serviceInterface() default void.class;
}
