package com.imho.authguard.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.StringJoiner;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.imho.authguard..*)")
    public void allMethods() {
    }

    @Before("allMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String formattedArgs = formatArguments(joinPoint);
        log.debug("Before: {} | Args: {}", getMethodName(joinPoint), formattedArgs);
    }

    @AfterReturning(pointcut = "allMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        log.debug("After: {} | Result: {}", getMethodName(joinPoint), result);
    }

    @AfterThrowing(pointcut = "allMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception occurred in method: {}. Exception: {}", getMethodName(joinPoint), exception.getLocalizedMessage());
    }

    @Around("allMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        log.debug("Method: {} executed in {} ms", getMethodName(joinPoint), endTime - startTime);
        return result;
    }

    private String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }

    private String[] getParameterNames(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return Arrays.stream(method.getParameters())
                .map(Parameter::getName)
                .toArray(String[]::new);
    }

    private String formatArguments(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = getParameterNames(joinPoint);

        if (args == null || parameterNames.length != args.length) {
            return Arrays.toString(args);
        }

        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < parameterNames.length; i++) {
            joiner.add(parameterNames[i] + ": " + args[i]);
        }
        return joiner.toString();
    }

}
