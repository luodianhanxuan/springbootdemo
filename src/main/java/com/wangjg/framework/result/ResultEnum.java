package com.wangjg.framework.result;

import lombok.Getter;

/**
 * @author wangjg
 * 2019/10/14
 */
@Getter
enum ResultEnum {
    SUCCESS("000000", "OK"),
    FAIL("100100", "数据异常"),
    ERROR("100200", "系统异常"),
    ;

    ResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 响应码
     */
    private String code;
    /**
     * 提示信息
     */
    private String msg;
}
