package com.wangjg.framework.util.wrapper.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjg.framework.util.ReflectUtil;
import com.wangjg.framework.util.wrapper.annotation.BeginQuery;
import com.wangjg.framework.util.wrapper.annotation.EndQuery;
import com.wangjg.framework.util.wrapper.handlerchain.QueryFieldHandlerChain;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author wangjg
 * 2019-06-10
 */
public class EndQueryFieldHandler implements QueryFieldHandler {
    @Override
    public <E> void handler(String fieldName, Object value, Field field, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain) {
        if (field != null
                && field.getAnnotationsByType(EndQuery.class) != null
                && field.getAnnotationsByType(EndQuery.class).length > 0) {
            wrapper.le(true, ReflectUtil.underline(fieldName), value);
        } else {
            handlerChain.doHandler(fieldName, value, field, wrapper);
        }
    }
}
