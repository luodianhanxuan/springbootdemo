package com.wangjg.framework.util.wrapper.handlerchain;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjg.framework.util.wrapper.handler.QueryFieldHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjg
 * 2019-06-10
 */
@Data
@Slf4j
public class SimpleQueryFieldHandlerChain implements QueryFieldHandlerChain {
    private static final String TAG = "mybatis plus 查询条件封装处理器执行链：";

    private List<QueryFieldHandler> handlers = new ArrayList<>();

    /**
     * 每个线程以 0 开始
     */
    private ThreadLocal<Integer> indexOfThreadLocal = ThreadLocal.withInitial(() -> 0);

    @Override
    public void addHandler(QueryFieldHandler handler) {
        this.handlers.add(handler);
    }

    @Override
    public void reset() {
        indexOfThreadLocal.set(0);
    }

    @Override
    public <E> void doHandler(String fieldName, Object value, Class type, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain) {
        Integer index = indexOfThreadLocal.get();
        if (index == handlers.size()) {
            log.info(String.format("%s：处理器链处理完毕", TAG));
            return;
        }

        final QueryFieldHandler queryFieldHandler = handlers.get(index++);
        indexOfThreadLocal.set(index);

        queryFieldHandler.handler(fieldName, value, type, wrapper, handlerChain);
    }

}
