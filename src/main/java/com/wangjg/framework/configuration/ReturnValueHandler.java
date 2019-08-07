package com.wangjg.framework.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.util.List;

/**
 * @author wangjg
 * 2019-08-07
 * <p>
 * 对没有发生异常情况下接口响应数据结构进行同一封装（对异常的处理，
 * 可以通过在 {@link com.wangjg.framework.controller.BaseController} 中添加异常处理方法处理）
 */
@Slf4j
public class ReturnValueHandler extends RequestResponseBodyMethodProcessor {

    ReturnValueHandler(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    /**
     * 该处理程序是否支持给定的方法返回类型(只有返回true才回去调用handleReturnValue)
     */
    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        return super.supportsReturnType(methodParameter);
    }

    /**
     * 处理给定的返回值
     * 通过向 ModelAndViewContainer 添加属性和设置视图或者
     * 通过调用 ModelAndViewContainer.setRequestHandled(true) 来标识响应已经被直接处理(不再调用视图解析器)
     * <p>
     * 对响应数据 object 进行封装
     */
    @Override
    public void handleReturnValue(Object o, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws IOException, HttpMediaTypeNotAcceptableException {
        super.handleReturnValue(o, methodParameter, modelAndViewContainer, nativeWebRequest);
    }
}
