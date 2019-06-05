package com.wangjg.framework.util;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjg
 * 2019-06-05
 * <p>
 * mybatis-plus 插件查询条件处理工具类
 */
@Slf4j
public class WrapperUtil {
    private static final String TAG = "mybatis-plus wrapper 对象工具类";

    private WrapperUtil() {
    }

    /**
     * 每一个线程都有一条责任链
     */
    private static ThreadLocal<QueryFieldHandlerChain> handlerChain = ThreadLocal.withInitial(() -> {
        QueryFieldHandlerChain queryFieldHandlerChain = new QueryFieldHandlerChain();
        // 处理 >= 字段
        queryFieldHandlerChain.addHandler(new BeginQueryFieldHandler());
        // 处理 <= 字段
        queryFieldHandlerChain.addHandler(new EndQueryFieldHandler());
        // 处理 == 字段 （将其放在处理链达到其他处理器链都不匹配之后最后以 equal 方式处理）
        queryFieldHandlerChain.addHandler(new EqualQueryFieldHandler());
        return queryFieldHandlerChain;
    });



    public static <E, V> QueryWrapper<E> getWrapperByVO(Class<E> entityClazz, Class<V> voClazz, V vo) {
        // 1. 根据 voClazz 初始化 一个对象 voComparedWith
        V voComparedWith;
        try {
            voComparedWith = getVoComparedWith(voClazz);
        } catch (IllegalAccessException e) {
            log.error(String.format("%s：反射构造VO对象失败：没有构造器访问权限【%s】", TAG, e));
            return null;
        } catch (InstantiationException e) {
            log.error(String.format("%s：反射构造VO对象失败：初始化异常【%s】", TAG, e));
            return null;
        }
        if (voComparedWith == null) {
            return null;
        }

        // 2. 将 vo 和 voCompared 进行对比，值不一致的属性集合
        final List<ReflectUtil.CompareDifferentResult> compareDifferentResults;
        try {
            compareDifferentResults = ReflectUtil.comparePropertiesWithDifferentValue(vo, voComparedWith);
        } catch (Exception e) {
            log.error(String.format("%s：对比 vo 和 voComparedWith 失败：【%s】", TAG, e));
            return null;
        }

        if (CollectionUtil.isEmpty(compareDifferentResults)) {
            return new QueryWrapper<>();
        }

        final QueryWrapper<E> wrapper = new QueryWrapper<>();
        // 3. 根据属性集合构造初始化 wrapper 对象
        for (ReflectUtil.CompareDifferentResult compareDifferentResult : compareDifferentResults) {
            String field = compareDifferentResult.getField();
            Object value = compareDifferentResult.getFirstValue();
            Class type = compareDifferentResult.getType();
            final QueryFieldHandlerChain queryFieldHandlerChain = handlerChain.get();

            queryFieldHandlerChain.doHandler(field, value, type, wrapper, queryFieldHandlerChain);
        }
        return wrapper;
    }

    private static <V> V getVoComparedWith(Class<V> voClazz) throws IllegalAccessException, InstantiationException {
        return voClazz.newInstance();
    }

    /**
     * 查询条件拼装处理器
     */
    private interface QueryFieldHandler {
        <E> void handler(String fieldName, Object value, Class type, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain);
    }

    /**
     * 处理链容器
     */
    private static class QueryFieldHandlerChain {
        private List<QueryFieldHandler> handlers = new ArrayList<>();

        private int index = 0;

        void addHandler(QueryFieldHandler handler) {
            this.handlers.add(handler);
        }

        <E> void doHandler(String fieldName, Object value, Class type, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain) {
            if (index == handlers.size()) {
                log.info(String.format("%s：处理器链处理完毕", TAG));
                return;
            }
            final QueryFieldHandler queryFieldHandler = handlers.get(index);
            index++;
            queryFieldHandler.handler(fieldName, value, type, wrapper, handlerChain);
        }

    }

    private static class BeginQueryFieldHandler implements QueryFieldHandler {
        @Override
        public <E> void handler(String fieldName, Object value, Class type, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain) {
            if (fieldName.endsWith("_begin")) {
                fieldName = fieldName.substring(0, fieldName.lastIndexOf("_begin"));
                wrapper.ge(true, ReflectUtil.underline(fieldName), value);
            } else {
                handlerChain.doHandler(fieldName, value, type, wrapper, handlerChain);
            }
        }
    }


    private static class EndQueryFieldHandler implements QueryFieldHandler {
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


    private static class EqualQueryFieldHandler implements QueryFieldHandler {
        @Override
        public <E> void handler(String fieldName, Object value, Class type, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain) {
            wrapper.eq(true, ReflectUtil.underline(fieldName), value);
        }
    }

}
