package ru.t1.task_manager_aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.t1.task_manager_aop.dto.TaskRequestDto;

@Aspect
@Component
@Slf4j(topic = "LoggingAspect")
public class LoggingAspect {

    @AfterThrowing(
            pointcut = "execution(* ru.t1.task_manager_aop.service.impl.TaskServiceImpl.*(..))",
            throwing = "exception")
    public void exceptionLog(JoinPoint joinPoint, Throwable exception) {
        Signature signature = joinPoint.getSignature();
        String declaringType = signature.getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("ОШИБКА: Метод -> {}(), Класс -> {}, Причина -> {}", methodName, declaringType,
                exception.getMessage());
    }

    @Before("@annotation(ru.t1.task_manager_aop.annotation.ValidationTask)")
    public void beforeCreateTask(JoinPoint joinPoint) {
        if (joinPoint.getArgs().length > 0) {
            TaskRequestDto taskRequestDto = (TaskRequestDto) joinPoint.getArgs()[0];
            StringBuilder stringBuilder = new StringBuilder("Отсутствуют обязательные поля для Задачи: ");
            boolean nullFields = false;

            if (taskRequestDto.userId() == null) {
                stringBuilder.append("userId ");
                nullFields = true;
            }
            if (taskRequestDto.title() == null) {
                stringBuilder.append("title ");
                nullFields = true;
            }
            if (taskRequestDto.description() == null) {
                stringBuilder.append("description ");
                nullFields = true;
            }

            if (nullFields) {
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        } else {
            throw new IllegalArgumentException("Отсутствует тело запроса");
        }
    }

    @AfterReturning(
            pointcut = "@annotation(ru.t1.task_manager_aop.annotation.LogReturningObject)",
            returning = "result")
    public void afterReturningObjectLog(JoinPoint joinPoint, Object result) {
        Signature signature = joinPoint.getSignature();
        String declaringType = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        log.info("ВЫЗОВ: Метод {}(), Класс {}, Объект -> {}", methodName, declaringType, result);
    }

    @Around("@annotation(ru.t1.task_manager_aop.annotation.TimeTracking)")
    public Object executionTimeLog(ProceedingJoinPoint joinPoint) {
        long start = System.currentTimeMillis();

        Signature signature = joinPoint.getSignature();
        String declaringType = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException("TimeTracking Exception", e);
        } finally {
            long end = System.currentTimeMillis();
            long duration = end - start;

            log.info("ВРЕМЯ: Метод {}(), Аргументы {}, Класс {}: Время выполнения -> {}мс",
                    methodName, args, declaringType, duration
            );
        }
    }
}
