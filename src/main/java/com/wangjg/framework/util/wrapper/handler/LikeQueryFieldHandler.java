package com.wangjg.framework.util.wrapper.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjg.framework.util.ReflectUtil;
import com.wangjg.framework.util.wrapper.annotation.EqualQuery;
import com.wangjg.framework.util.wrapper.annotation.LikeQuery;
import com.wangjg.framework.util.wrapper.handlerchain.QueryFieldHandlerChain;

import java.lang.reflect.Field;

/**
 * @author wangjg
 * 2019-07-12
 */
public class LikeQueryFieldHandler implements QueryFieldHandler {
    @Override
    public <E> void handler(String fieldName, Object value, Field field, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain) {
        if (field != null
                && field.getAnnotationsByType(LikeQuery.class) != null
                && field.getAnnotationsByType(LikeQuery.class).length > 0) {
            wrapper.like(true, ReflectUtil.underline(fieldName), value);
        } else {
            handlerChain.doHandler(fieldName, value, field, wrapper);
        }

    }
}
