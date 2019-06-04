package com.wangjg.framework.controller;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangjg.framework.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author wangjg
 * 2019-06-04
 */

@SuppressWarnings({"unchecked", "WeakerAccess", "unused", "SpringJavaAutowiredMembersInspection", "SpringJavaInjectionPointsAutowiringInspection"})
@Slf4j
public class GeneralController<S extends IService<E>, E, V> extends BaseController {
    private static final String TAG = "通用 WEB 控制器";

    @Autowired
    protected S service;

    @PostMapping
    public V save(V vo) {
        if (vo == null) {
            log.info(String.format("%s：vo 对象不能为 null", TAG));
            return null;
        }

        try {
            this.dataCheck4Save(vo);
        } catch (Exception e) {
            log.info(String.format("%s：保存VO【%s】验证不通过", TAG, vo));
            // TODO
            return vo;
        }

        Class<E> eClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        E entity;
        try {
            entity = eClass.newInstance();
        } catch (Exception e) {
            log.error(String.format("%s：创建【%s】类型对象失败：【%s】", TAG, eClass.getSimpleName(), e));
            // TODO
            return vo;
        }

        Converter<V, E> converter4VO2Entity = this.getConverter4VO2Entity(vo, entity);
        converter4VO2Entity.convert(vo, entity);

        final boolean b = service.saveOrUpdate(entity);
        log.info(String.format("%s：实体【%s】保存%s", TAG, entity, b ? "成功" : "失败"));

        final Converter<E, V> converter4Entity2VO = getConverter4Entity2VO(entity, vo);
        converter4Entity2VO.convert(entity, vo);

        return vo;
    }

    protected void dataCheck4Save(V vo) {
    }


    protected Converter<E, V> getConverter4Entity2VO(E entity, V vo) {
        return new DefaultConverter<>();
    }

    protected Converter<V, E> getConverter4VO2Entity(V vo, E entity) {
        return new DefaultConverter<>();
    }

    @DeleteMapping("/{id}")
    public String del(@PathVariable("id") String id) {
        if (StringUtil.isEmpty(id)) {
            log.info(String.format("%s：id 不能为空", TAG));
            // TODO
            return id;
        }

        final E entity = service.getById(id);
        if (entity == null) {
            log.info(String.format("%s：不存在ID【%s", TAG, id));
            // TODO
            return id;
        }
        try {
            dataCheck4Del(entity);
        } catch (Exception e) {
            log.info(String.format("%s：删除数据【%s】验证不通过", TAG, entity));
            // TODO
            return id;
        }
        final boolean b = service.removeById(id);
        // TODO
        log.info(String.format("%s：删除ID【%s】数据%s", TAG, id, b ? "成功" : "失败"));
        return id;
    }

    private void dataCheck4Del(E entity) {

    }

    @GetMapping("/{id}")
    public V get(@PathVariable("id") String id) {
        if (StringUtil.isEmpty(id)) {
            log.info(String.format("%s：id 不能为空", TAG));
            return null;
        }

        final E entity = service.getById(id);
        if (entity == null) {
            return null;
        }

        final Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        Class<V> vClass = (Class<V>) actualTypeArguments[2];
        V vo;

        try {
            vo = vClass.newInstance();
        } catch (Exception e) {
            log.error(String.format("%s：创建【%s】类型对象失败：【%s】", TAG, vClass.getSimpleName(), e));
            return null;
        }

        if (vo == null) {
            return null;
        }

        Converter<E, V> converter = this.getConverter4Entity2VO(entity, vo);
        converter.convert(entity, vo);
        return vo;
    }

    public interface Converter<O, D> {
        @SuppressWarnings("UnusedReturnValue")
        D convert(O original, D destination);
    }

    public class DefaultConverter<O, D> implements Converter<O, D> {
        @Override
        public D convert(O original, D destination) {
            BeanUtils.copyProperties(original, destination);
            return destination;
        }
    }

}
