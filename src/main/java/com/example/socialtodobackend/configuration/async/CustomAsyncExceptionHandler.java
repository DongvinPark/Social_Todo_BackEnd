package com.example.socialtodobackend.configuration.async;

import java.lang.reflect.Method;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class CustomAsyncExceptionHandler
    implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(
        Throwable throwable, Method method, Object... obj) {

        System.out.println("예외 메시지 - " + throwable.getMessage());
        System.out.println("예외 발생한 메서드 이름 - " + method.getName());
        for (Object param : obj) {
            System.out.println("파라미터 값 - " + param);
        }
    }

}
