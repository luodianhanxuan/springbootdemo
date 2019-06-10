package com.wangjg.framework.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wangjg
 * 2019-06-06
 */
@SuppressWarnings("unused")
@Data
@EqualsAndHashCode(callSuper = true)
public class DataCheckException extends Exception {
    /**
     * 用户提示语
     */
    private String tips;

    public DataCheckException(String message, String tips) {
        super(message);
        this.tips = tips;
    }

    public DataCheckException(String message) {
        super(message);
    }
}
