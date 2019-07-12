package com.wangjg.framework.util.wrapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjg.framework.util.CollectionUtil;
import com.wangjg.framework.util.ReflectUtil;
import com.wangjg.framework.util.wrapper.handler.BeginQueryFieldHandler;
import com.wangjg.framework.util.wrapper.handler.EndQueryFieldHandler;
import com.wangjg.framework.util.wrapper.handler.EqualQueryFieldHandler;
import com.wangjg.framework.util.wrapper.handlerchain.QueryFieldHandlerChain;
import com.wangjg.framework.util.wrapper.handlerchain.SimpleQueryFieldHandlerChain;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author wangjg
 * 2019-06-05
 * <p>
 * mybatis-plus 插件查询条件处理工具类
 */
@SuppressWarnings("unused")
@Slf4j
public class WrapperUtil {
    private static final String TAG = "mybatis-plus wrapper 对象工具类";

    private WrapperUtil() {
    }

    private static final QueryFieldHandlerChain handlerChain = new SimpleQueryFieldHandlerChain() {{
        // 处理 >= 字段
        this.addHandler(new BeginQueryFieldHandler());
        // 处理 <= 字段
        this.addHandler(new EndQueryFieldHandler());
        // 处理 == 字段 （将其放在处理链达到其他处理器链都不匹配之后最后以 equal 方式处理）
        this.addHandler(new EqualQueryFieldHandler());
    }};


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
            String fieldName = compareDifferentResult.getFieldName();
            Object value = compareDifferentResult.getFirstValue();
            Field field = compareDifferentResult.getFirstField();

            handlerChain.reset();
            handlerChain.doHandler(fieldName, value, field, wrapper, handlerChain);
        }
        return wrapper;
    }

    private static <V> V getVoComparedWith(Class<V> voClazz) throws IllegalAccessException, InstantiationException {
        return voClazz.newInstance();
    }

}
