package com.wangjg.framework.util.wrapper.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjg.framework.util.ReflectUtil;
import com.wangjg.framework.util.wrapper.annotation.BeginQuery;
import com.wangjg.framework.util.wrapper.handlerchain.QueryFieldHandlerChain;

import java.lang.reflect.Field;

/**
 * @author wangjg
 * 2019-06-10
 */
public class BeginQueryFieldHandler implements QueryFieldHandler {
    @Override
    public <E> void handler(String fieldName, Object value, Field field, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain) {
        if (field != null
                && field.getAnnotationsByType(BeginQuery.class) != null
                && field.getAnnotationsByType(BeginQuery.class).length > 0) {
            wrapper.ge(true, ReflectUtil.underline(fieldName), value);
        } else {
            handlerChain.doHandler(fieldName, value, field, wrapper, handlerChain);
        }
    }
}
