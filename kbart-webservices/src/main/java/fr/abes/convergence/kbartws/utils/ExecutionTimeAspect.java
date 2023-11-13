package fr.abes.convergence.kbartws.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Slf4j
public class ExecutionTimeAspect {

    @Around("@annotation(ExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        double executionTime = (endTime - startTime) / 1000;

        log.debug("Thread: ", Thread.currentThread().getName());
        log.debug("Methode: ", Thread.currentThread().getStackTrace()[1].getMethodName());
        log.debug("Temps d'exécution : " + executionTime + " secondes");
        return result;
    }
}
