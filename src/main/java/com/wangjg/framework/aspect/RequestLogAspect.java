package com.wangjg.framework.aspect;

import com.wangjg.framework.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author wangjg
 * 2019-07-29
 */
@Component
@Aspect
@Slf4j
public class RequestLogAspect {
    private static final String TAG = "请求耗时日志切片";

    @Pointcut("execution(public * com.wangjg.framework.controller.*.*(..)) && @target(org.springframework.web.bind.annotation.RestController)")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }

        Date begin = new Date();
        Object result = pjp.proceed();
        Date end = new Date();
        if (request == null) {
            log.info(String.format("%s：请求耗时 %s ms", TAG, end.getTime() - begin.getTime()));
        } else {
            log.info(String.format("%s：client【%s】请求方式【%s】请求url【%s】耗时【%s ms】"
                    , TAG
                    , IpUtil.getIpAddress(request)
                    , request.getMethod()
                    , request.getRequestURL()
                    , end.getTime() - begin.getTime()));
        }
        return result;
    }
}
