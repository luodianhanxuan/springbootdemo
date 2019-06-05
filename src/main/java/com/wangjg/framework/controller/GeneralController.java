package com.wangjg.framework.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wangjg.framework.pojo.vo.PageSearch;
import com.wangjg.framework.util.CollectionUtil;
import com.wangjg.framework.util.StringUtil;
import com.wangjg.framework.util.WrapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjg
 * 2019-06-04
 */

@SuppressWarnings({"unchecked", "WeakerAccess", "unused", "SpringJavaInjectionPointsAutowiringInspection"})
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

        Class<E> eClass = this.getClazz4Entity();
        E entity;
        try {
            entity = eClass.newInstance();
        } catch (Exception e) {
            log.error(String.format("%s：创建【%s】类型对象失败：【%s】", TAG, eClass.getSimpleName(), e));
            // TODO
            return vo;
        }

        final E e = this.vo2entity(vo, entity);

        final boolean b = service.saveOrUpdate(entity);
        log.info(String.format("%s：实体【%s】保存%s", TAG, entity, b ? "成功" : "失败"));

        return this.entity2vo(entity, vo);
    }

    private V entity2vo(E entity, V vo) {
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private E vo2entity(V vo, E entity) {
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    protected void dataCheck4Save(V vo) {
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

        Class<V> vClass = this.getClazz4VO();
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

        return this.entity2vo(entity, vo);
    }

    private Class<E> getClazz4Entity() {
        return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    private Class<V> getClazz4VO() {
        return (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
    }

    @GetMapping("/list")
    public List<V> list(V vo) {
        if (vo == null) {
            log.info(String.format("%s：系统异常 vo 不能为 null", TAG));
            // TODO
            return new ArrayList<>();
        }

        Wrapper<E> queryWrapper = this.getWrapperByVO(vo);
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper<>();
        }

        final List<E> entityList = service.list(queryWrapper);

        return this.entityList2VoList(entityList);
    }

    @PostMapping("/page")
    public IPage<V> page(@RequestBody PageSearch<V> pageSearch) {
        if (pageSearch == null) {
            log.info(String.format("%s：系统异常 vo 不能为 null", TAG));
            // TODO
            return null;
        }
        Page<V> pageInfo = pageSearch.getPage();
        Page<E> page = this.getPage(pageInfo);
        final V vo = pageSearch.getSearch();
        Wrapper<E> queryWrapper = null;
        if (vo != null) {
            queryWrapper = this.getWrapperByVO(vo);
        }
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper<>();
        }

        final IPage<E> pageData = service.page(page, queryWrapper);
        if (pageData == null) {
            // TODO
            return null;
        }
        final List<E> records = pageData.getRecords();
        final List<V> voList = this.entityList2VoList(records);
        BeanUtils.copyProperties(pageData, pageInfo);
        pageInfo.setRecords(voList);
        return pageInfo;
    }

    protected Page<E> getPage(Page<V> pageInfo) {
        final Page<E> page = new Page<>();
        BeanUtils.copyProperties(pageInfo, page);

        String[] desc = desc(pageInfo);
        String[] asc = asc(pageInfo);

        if (desc != null) {
            page.setDesc(desc);
        }
        if (asc != null) {
            page.setAsc(asc);
        }
        return page;
    }

    private String[] asc(Page<V> pageInfo) {
        return null;
    }

    private String[] desc(Page<V> pageInfo) {
        return null;
    }

    private List<V> entityList2VoList(List<E> entityList) {
        final Class<V> clazz4VO = this.getClazz4VO();
        return CollectionUtil.transferFromList2ToList(clazz4VO, entityList);
    }

    protected Wrapper<E> getWrapperByVO(V vo) {
        final Class<V> voClazz = getClazz4VO();
        final Class<E> entityClazz = getClazz4Entity();
        QueryWrapper<E> wrapper = WrapperUtil.getWrapperByVO(entityClazz, voClazz, vo);
        if (wrapper == null) {
            wrapper = new QueryWrapper<>();
        }
        return wrapper;
    }
}
