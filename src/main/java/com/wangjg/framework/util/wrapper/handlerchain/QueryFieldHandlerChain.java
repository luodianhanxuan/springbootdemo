package com.wangjg.framework.util.wrapper.handlerchain;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjg.framework.util.wrapper.handler.QueryFieldHandler;

import java.lang.reflect.Field;

/**
 * @author wangjg
 * 2019-06-10
 * <p>
 * 处理链容器
 */
public interface QueryFieldHandlerChain {

    <E> void doHandler(String fieldName, Object value, Field field, QueryWrapper<E> wrapper);

    void addHandler(QueryFieldHandler handler);

    void reset();
}
