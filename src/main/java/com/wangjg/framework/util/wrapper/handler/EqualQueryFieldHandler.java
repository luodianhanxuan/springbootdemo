package com.wangjg.framework.util.wrapper.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjg.framework.util.ReflectUtil;
import com.wangjg.framework.util.wrapper.handlerchain.QueryFieldHandlerChain;

/**
 * @author wangjg
 * 2019-06-10
 */
public class EqualQueryFieldHandler implements QueryFieldHandler {
    @Override
    public <E> void handler(String fieldName, Object value, Class type, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain) {
        wrapper.eq(true, ReflectUtil.underline(fieldName), value);
    }
}
