package com.lsd.test.dynmic.source.config;

import com.lsd.test.dynmic.source.config.datasource.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Aspect
@Component
@Order(1) // 请注意：这里order一定要小于tx:annotation-driven的order，即先执行DynamicDataSourceAspectAdvice切面，再执行事务切面，才能获取到最终的数据源
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DynamicAspectAdvice {

    @Around("execution(* com.lsd.test.dynmic.source.controller.*.*(..))")
    public Object doAround(ProceedingJoinPoint jp) throws Throwable {

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        HttpServletResponse response = sra.getResponse();
        String tenantId = request.getHeader("tenant");
        // 前端必须传入tenant header, 否则返回400
        if (!StringUtils.hasText(tenantId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        log.info("当前租户Id:{}", tenantId);
        DataSourceContextHolder.setDataSourceKey(tenantId);
        Object result = jp.proceed();
        DataSourceContextHolder.clearDataSourceKey();
        return result;
    }
}

