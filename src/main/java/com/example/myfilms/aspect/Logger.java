package com.example.myfilms.aspect;

import lombok.extern.java.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Log
@Component
@Aspect
public class Logger {

    @Before("execution(* com.example.myfilms.controllers..*.*(..))")
    public void printMethod(JoinPoint joinPoint) {
        log.info(joinPoint.getTarget().getClass().getSimpleName()
                + " Metoda: " + joinPoint.getSignature().getName()
                + " z parametrami: " + Arrays.toString(joinPoint.getArgs()));
    }
}
