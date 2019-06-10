package com.wangjg.framework.util.wrapper.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjg.framework.util.ReflectUtil;
import com.wangjg.framework.util.wrapper.handlerchain.QueryFieldHandlerChain;

/**
 * @author wangjg
 * 2019-06-10
 */
public class EndQueryFieldHandler implements QueryFieldHandler {
    @Override
    public <E> void handler(String fieldName, Object value, Class type, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain) {
        if (fieldName.endsWith("_end")) {
            fieldName = fieldName.substring(0, fieldName.lastIndexOf("_end"));
            wrapper.le(true, ReflectUtil.underline(fieldName), value);
        } else {
            handlerChain.doHandler(fieldName, value, type, wrapper, handlerChain);
        }
    }
}
