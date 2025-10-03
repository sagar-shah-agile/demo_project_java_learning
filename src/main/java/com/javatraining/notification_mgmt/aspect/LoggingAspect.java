package com.javatraining.notification_mgmt.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.javatraining.notification_mgmt.service.impl.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("➡ Entering method: {}", methodName);
    }

    @AfterReturning(
            pointcut = "execution(* com.javatraining.notification_mgmt.service.impl.*.*(..))",
            returning = "result"
    )
    public void logAfter(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("⬅ Exiting method: {} | Returned: {}", methodName, result);
    }

    @AfterThrowing(
            pointcut = "execution(* com.javatraining.notification_mgmt.service.impl.*.*(..))",
            throwing = "ex"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().toShortString();
        log.error("💥 Exception in method: {} | Message: {}", methodName, ex.getMessage(), ex);
    }
}
