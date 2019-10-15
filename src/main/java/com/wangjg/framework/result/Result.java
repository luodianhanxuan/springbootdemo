package com.wangjg.framework.result;

import com.wangjg.framework.exception.DataCheckException;
import com.wangjg.framework.util.StringUtil;
import lombok.Data;

import java.util.function.Consumer;

/**
 * @author wangjg
 * 2019/10/14
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Data
public class Result {

    /**
     * 响应吗
     */
    private String code;
    /**
     * 相应数据
     */
    private Object data;
    /**
     * 错误信息
     */
    private String msg;


    public static Result success(Object data) {
        return apply(result -> {
            result.setData(data);
            result.setCode(ResultEnum.SUCCESS.getCode());
            result.setMsg(ResultEnum.SUCCESS.getMsg());
        });
    }

    public static Result success(Object data, String msg) {
        return apply(result -> {
            result.setData(data);
            result.setCode(ResultEnum.SUCCESS.getCode());
            result.setMsg(msg);
        });
    }

    public static Result fail(String msg) {
        return apply(result -> {
            result.setMsg(msg);
            result.setCode(ResultEnum.FAIL.getCode());
        });
    }

    public static Result fail() {
        return apply(result -> {
            result.setCode(ResultEnum.FAIL.getCode());
            result.setMsg(ResultEnum.FAIL.getMsg());
        });
    }

    public static Result fail(DataCheckException e) {
        return apply(result -> {
            String msg;
            if (!StringUtil.isEmpty(e.getTips())) {
                msg = e.getTips();
            } else if (!StringUtil.isEmpty(e.getMessage())) {
                msg = e.getMessage();
            } else {
                msg = ResultEnum.FAIL.getMsg();
            }

            result.setCode(ResultEnum.FAIL.getCode());
            result.setMsg(msg);
        });
    }

    public static Result error(Exception e) {
        if (e instanceof DataCheckException) {
            return fail((DataCheckException) e);
        }

        return apply(result -> {
            String message = e.getMessage();
            if (StringUtil.isEmpty(message)) {
                result.setMsg(ResultEnum.ERROR.getMsg());
            } else {
                result.setMsg(message);
            }
            result.setCode(ResultEnum.ERROR.getCode());
        });
    }

    private static Result apply(Consumer<Result> consumer) {
        if (consumer == null) {
            return null;
        }

        Result result = new Result();
        consumer.accept(result);
        return result;
    }
}
