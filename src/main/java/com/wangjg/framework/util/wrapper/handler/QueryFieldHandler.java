package com.wangjg.framework.util.wrapper.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjg.framework.util.wrapper.handlerchain.QueryFieldHandlerChain;

import java.lang.reflect.Field;

/**
 * @author wangjg
 * 2019-06-10
 * <p>
 * 查询条件拼装处理器
 */
public interface QueryFieldHandler {

    <E> void handler(String fieldName, Object value, Field field, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain);
}
