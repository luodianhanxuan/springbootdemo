package com.wangjg.framework.util.wrapper.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjg.framework.util.ReflectUtil;
import com.wangjg.framework.util.wrapper.annotation.BeginQuery;
import com.wangjg.framework.util.wrapper.annotation.EqualQuery;
import com.wangjg.framework.util.wrapper.handlerchain.QueryFieldHandlerChain;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author wangjg
 * 2019-06-10
 */
public class EqualQueryFieldHandler implements QueryFieldHandler {
    @Override
    public <E> void handler(String fieldName, Object value, Field field, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain) {
        if (field != null
                && field.getAnnotationsByType(EqualQuery.class) != null
                && field.getAnnotationsByType(EqualQuery.class).length > 0) {
            wrapper.eq(true, ReflectUtil.underline(fieldName), value);
        } else {
            handlerChain.doHandler(fieldName, value, field, wrapper, handlerChain);
        }

    }
}
