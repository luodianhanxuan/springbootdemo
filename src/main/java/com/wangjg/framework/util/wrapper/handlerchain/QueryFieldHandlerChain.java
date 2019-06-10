package com.wangjg.framework.util.wrapper.handlerchain;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjg.framework.util.wrapper.handler.QueryFieldHandler;

/**
 * @author wangjg
 * 2019-06-10
 * <p>
 * 处理链容器
 */
public interface QueryFieldHandlerChain {

    <E> void doHandler(String fieldName, Object value, Class type, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain);

    void addHandler(QueryFieldHandler handler);
}
