package com.qst.dms.aspect;

import com.qst.dms.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Component
@Aspect
@Slf4j
public class LoggingAspect {
    @Autowired
    private HttpServletRequest request;

    @Before("execution(* com.qst.dms.controller.*.*(..))")
    public void beforeAdvice(JoinPoint joinPoint) {
        // 获取访问路径
        String path = "";
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Class<?> controllerClass = method.getDeclaringClass();
        RequestMapping controllerRequestMapping = controllerClass.getAnnotation(RequestMapping.class);
        String controllerPath = controllerRequestMapping.value()[0];
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (getMapping != null) {
            path = controllerPath + getMapping.value()[0];
        } else if (postMapping != null) {
            path = controllerPath + postMapping.value()[0];
        } else if (putMapping != null) {
            path = controllerPath + putMapping.value()[0];
        } else if (deleteMapping != null) {
            path = controllerPath + deleteMapping.value()[0];
        }

        // 记录日志
        log.info("[{}] {} 访问了接口: {}", DateTimeUtils.now(), request.getRemoteAddr(), path);
    }
}
