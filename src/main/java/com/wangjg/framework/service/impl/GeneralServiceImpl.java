package com.wangjg.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangjg.framework.exception.DataCheckException;
import com.wangjg.framework.pojo.vo.PageSearch;
import com.wangjg.framework.service.GeneralService;
import com.wangjg.framework.util.CollectionUtil;
import com.wangjg.framework.util.wrapper.WrapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author wangjg
 * 2020/6/21
 */
@SuppressWarnings({"unchecked", "unused"})
@Slf4j
public class GeneralServiceImpl<M extends BaseMapper<E>, E, V> extends ServiceImpl<M, E> implements GeneralService<E, V> {

    private static final String TAG = "通用 WEB 控制器";

    @Override
    public V insertOrUpdate(V vo) throws Exception {
        E initEntity = initEntity();
        E toSave = this.vo2Entity(vo, initEntity);
        this.preSave(vo, toSave);
        this.doSave(vo, toSave);
        this.entity2vo(toSave, vo);
        return vo;
    }

    @Override
    public Serializable del(Serializable id) throws Exception {
        final E entity = super.getById(id);
        if (entity == null) {
            throw new DataCheckException(String.format("%s：不存在ID【%s】", TAG, id), TAG);
        }

        this.preDelOne(id, entity);
        this.doDelOne(id, entity);
        return id;
    }

    @Override
    public boolean del(Serializable[] ids) {
        List<Serializable> idList = Arrays.asList(ids);
        Collection<E> entities = super.listByIds(idList);
        this.preDelList(idList, entities);
        return this.doDelList(idList, entities);
    }

    @Override
    public V get(Serializable id) throws Exception {
        final E entity = super.getById(id);
        if (entity == null) {
            throw new DataCheckException(String.format("%s：id【%s】 不存在", TAG, id), "id 不存在");
        }

        V vo = this.initVO();
        vo = this.entity2vo(entity, vo);
        return vo;
    }

    @Override
    public List<V> list(V vo) {
        this.preListCondition(vo);
        Wrapper<E> queryWrapper = this.getWrapperByVO(vo, false);
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper<>();
        }
        final List<E> entityList = super.list(queryWrapper);
        return this.entityList2VoList(entityList);
    }

    @Override
    public Page<V> page(PageSearch<V> pageSearch) {
        Page<V> pageInfo = pageSearch.getPage();
        Page<E> page = this.getPageCondition(pageInfo);
        final V vo = pageSearch.getSearch();

        this.preListCondition(vo);
        Wrapper<E> queryWrapper = null;
        if (vo != null) {
            queryWrapper = this.getWrapperByVO(vo, true);
        }
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper<>();
        }

        final IPage<E> pageData = super.page(page, queryWrapper);
        if (pageData == null) {
            return new Page<>();
        }
        final List<E> records = pageData.getRecords();
        final List<V> voList = this.entityList2VoList(records);
        BeanUtils.copyProperties(pageData, pageInfo);
        pageInfo.setRecords(voList);

        return pageInfo;
    }

    /**
     * 将客户端的分页数据封装对象转换为mybatis plus查询插件的分页数据封装对象
     *
     * @param pageInfo mybatis plus 查询响应的分页数据封装对象
     * @return mybatis plus查询插件的分页数据封装对象
     */
    protected Page<E> getPageCondition(Page<V> pageInfo) {
        final Page<E> page = new Page<>();
        BeanUtils.copyProperties(pageInfo, page);

        String[] desc = pageDesc(pageInfo);
        String[] asc = pageAsc(pageInfo);

        if (desc != null) {
            page.setDesc(desc);
        }
        if (asc != null) {
            page.setAsc(asc);
        }
        return page;
    }

    /**
     * 分页列表升序字段
     *
     * @param pageInfo 分页参数封装对象
     * @return 分页列表升序字段数组
     */
    protected String[] pageAsc(Page<V> pageInfo) {
        return null;
    }

    /**
     * 分页列表降序字段
     *
     * @param pageInfo 分页参数封装对象
     * @return 分页列表升序字段数组
     */
    protected String[] pageDesc(Page<V> pageInfo) {
        return null;
    }


    /**
     * 根据entitylist构造volist并返回
     *
     * @param entityList 数据库映射对象集合
     * @return 提供给客户端的参数封装对象集合
     */
    protected List<V> entityList2VoList(List<E> entityList) {
        final Class<V> clazz4VO = this.getClazz4VO();
        return CollectionUtil.transferFromList2ToList(clazz4VO, entityList);
    }

    /**
     * @param vo          客户端提供的参数封装对象
     * @param isPageQuery 是否为分页查询 (如果是分页查询，则排序设置在分页参数对象中，否则需设置在 wrapper 中)
     * @return 数据库查询条件封装对象
     */
    protected Wrapper<E> getWrapperByVO(V vo, boolean isPageQuery) {
        final Class<V> voClazz = getClazz4VO();
        final Class<E> entityClazz = getClazz4Entity();
        QueryWrapper<E> wrapper = WrapperUtil.getWrapperByVO(entityClazz, voClazz, vo);
        if (wrapper == null) {
            wrapper = new QueryWrapper<>();
        }
        String[] asc = listAsc();
        if (!isPageQuery) {
            if (asc != null && asc.length > 0) {
                wrapper.orderByAsc(asc);
            }
            String[] desc = listDesc();
            if (desc != null && desc.length > 0) {
                wrapper.orderByDesc(desc);
            }
        }
        return wrapper;
    }

    /**
     * 列表升序字段
     *
     * @return 列表升序字段数组
     */
    protected String[] listAsc() {
        return null;
    }


    /**
     * 列表降序字段
     *
     * @return 列表升序字段数组
     */
    protected String[] listDesc() {
        return null;
    }


    /**
     * 允许子类在构造查询条件封装对象之前做一些事情
     *
     * @param vo 客户端提供的参数封装对象
     */
    protected void preListCondition(V vo) {
    }


    /**
     * 将数据库映射对象转换为 vo 对象
     *
     * @param entity 数据库映射对象
     * @param vo     客户端提供的参数封装对象
     * @return 填充好数据的 vo 对象
     */
    protected V entity2vo(E entity, V vo) {
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }


    private V initVO() throws IllegalAccessException, InstantiationException {
        Class<V> vClass = this.getClazz4VO();
        return vClass.newInstance();
    }

    /**
     * 执行数据库数据删除
     *
     * @param idList   要删除的数据主键集合
     * @param entities 要删除的数据库映射对象集合
     * @return 删除成功与否
     */
    private boolean doDelList(List<Serializable> idList, Collection<E> entities) {
        final boolean b = super.removeByIds(idList);
        log.info(String.format("%s：删除数据%s", TAG, b ? "成功" : "失败"));
        this.postDelList(idList, entities);
        return b;
    }

    /**
     * 允许子类在本表数据删除之后做一些事情
     *
     * @param deletedIdList   已删除的主键集合
     * @param deletedEntities 已删除的数据库映射对象集合
     */
    protected void postDelList(List<Serializable> deletedIdList, Collection<E> deletedEntities) {
    }

    /**
     * 允许子类在本表数据删除之前做一些事情
     *
     * @param toDelIds      要删除的主键集合
     * @param toDelEntities 要删除的数据库映射实体集合
     */
    protected void preDelList(List<Serializable> toDelIds, Collection<E> toDelEntities) {
    }


    /**
     * 执行数据库数据删除
     *
     * @param id     主键
     * @param entity 根据主键查询出来的数据库映射对象
     */
    private void doDelOne(Serializable id, E entity) {
        final boolean b = super.removeById(id);
        log.info(String.format("%s：删除ID【%s】数据%s", TAG, id, b ? "成功" : "失败"));
        this.postDelOne(id, entity);
    }

    /**
     * 允许子类在本表数据删除之后做一些事情
     *
     * @param deletedId     被删除的数据主键
     * @param deletedEntity 被删除的数据库映射对象
     */
    protected void postDelOne(Serializable deletedId, E deletedEntity) {

    }


    /**
     * 允许子类在本表数据删除之前做一些事情
     *
     * @param toDelId     要删除的主键
     * @param ToDelEntity 要删除的数据库映射对象
     */
    protected void preDelOne(Serializable toDelId, E ToDelEntity) {
    }

    /**
     * 执行数据库的数据保存
     *
     * @param vo     客户端提供的参数封装对象
     * @param entity 要保存的数据库映射对象
     */
    private void doSave(@RequestBody V vo, E entity) {
        boolean b;
        b = super.saveOrUpdate(entity);
        log.info(String.format("%s：实体【%s】保存%s", TAG, entity, b ? "成功" : "失败"));
        this.postSave(vo, entity);
    }


    /**
     * 允许子类在本表保存完成之后做一些其他的事情
     *
     * @param vo    客户端提供的参数封装对象
     * @param saved 保存之后的填充主键之后的实体
     */
    protected void postSave(V vo, E saved) {
    }

    /**
     * 允许子类在本表数据保存之前做一些事情
     *
     * @param vo     客户端提供的参数封装对象
     * @param toSave 要保存的实体对象
     */
    protected void preSave(V vo, E toSave) {
    }


    /**
     * 将vo对象转化为数据库映射对象
     *
     * @param vo     客户端提供的参数对象
     * @param entity 数据库映射对象
     * @return 填充好数据的数据库映射对象
     */
    protected E vo2Entity(V vo, E entity) {
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }


    private E initEntity() throws IllegalAccessException, InstantiationException {
        Class<E> eClass = this.getClazz4Entity();
        return eClass.newInstance();
    }


    /**
     * 获取数据库映射对象的类型
     *
     * @return 数据库映射对象的类型
     */
    private Class<E> getClazz4Entity() {
        return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    /**
     * 获取vo的类型
     *
     * @return 客户端提供的参数封装的类型
     */
    private Class<V> getClazz4VO() {
        return (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
    }


}
