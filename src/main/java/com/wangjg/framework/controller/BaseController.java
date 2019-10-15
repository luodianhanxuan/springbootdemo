package com.wangjg.framework.controller;

import com.wangjg.framework.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author wangjg
 * 2019-06-04
 */
@Slf4j
public class BaseController {
    @ExceptionHandler(Exception.class)
    public Result dataCheckExceptionHandler(Exception e) {
        log.error("==============================================================================");
        log.error("", e);
        log.error("==============================================================================");
        return Result.error(e);
    }
}
