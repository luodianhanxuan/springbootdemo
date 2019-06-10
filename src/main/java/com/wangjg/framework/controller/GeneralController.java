package com.wangjg.framework.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wangjg.framework.exception.DataCheckException;
import com.wangjg.framework.pojo.vo.PageSearch;
import com.wangjg.framework.util.CollectionUtil;
import com.wangjg.framework.util.StringUtil;
import com.wangjg.framework.util.wrapper.WrapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author wangjg
 * 2019-06-04
 */

@SuppressWarnings({"unchecked", "WeakerAccess", "unused"
        , "SpringJavaInjectionPointsAutowiringInspection", "RedundantThrows", "UnusedAssignment"})
@Slf4j
public class GeneralController<S extends IService<E>, E, V> extends BaseController {

    private static final String TAG = "通用 WEB 控制器";

    /**
     * 本表 service 对象
     */
    @Autowired
    protected S service;

    /**
     * 事务管理器
     */
    @Autowired
    private PlatformTransactionManager txManager;

    /**
     * 保存和更新方法
     *
     * @param vo 客户端提供的参数封装对象
     * @return 填充保存数据过后的 vo
     */
    @PostMapping
    public V save(@RequestBody V vo) {
        if (vo == null) {
            log.info(String.format("%s：vo 对象不能为 null", TAG));
            // TODO
            return null;
        }
        log.info(vo.toString());
        try {
            this.dataCheck4Save(vo);
        } catch (DataCheckException e) {
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

        entity = this.vo2Entity(vo, entity);
        this.preSave(vo, entity);

        boolean b;
        if (this.saveInTransaction()) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus status = txManager.getTransaction(def);
            try {
                doSave(vo, entity);
            } catch (Exception ex) {
                txManager.rollback(status);
                throw ex;
            }
            txManager.commit(status);
            b = true;
        } else {
            b = doSave(vo, entity);
        }

        return this.entity2vo(entity, vo);
    }

    /**
     * 执行数据库的数据保存
     *
     * @param vo     客户端提供的参数封装对象
     * @param entity 要保存的数据库映射对象
     * @return 保存成功与否
     */
    private boolean doSave(@RequestBody V vo, E entity) {
        boolean b;
        b = service.saveOrUpdate(entity);
        log.info(String.format("%s：实体【%s】保存%s", TAG, entity, b ? "成功" : "失败"));
        this.postSave(vo, entity);
        return b;
    }


    /**
     * 按照主键删除方法
     *
     * @param id 主键
     * @return 删除的主键
     */
    @DeleteMapping("/{id}")
    public String del(@PathVariable("id") String id) {
        if (StringUtil.isEmpty(id)) {
            log.info(String.format("%s：id 不能为空", TAG));
            // TODO
            return id;
        }

        final E entity = service.getById(id);
        if (entity == null) {
            log.info(String.format("%s：不存在ID【%s】", TAG, id));
            // TODO
            return id;
        }
        try {
            dataCheck4DelOne(entity);
        } catch (DataCheckException e) {
            log.info(String.format("%s：删除数据【%s】验证不通过", TAG, entity));
            // TODO
            return id;
        }

        this.preDelOne(id, entity);

        boolean b;
        if (this.delInTransaction()) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus status = txManager.getTransaction(def);
            try {
                this.doDelOne(id, entity);
            } catch (Exception ex) {
                txManager.rollback(status);
                throw ex;
            }
            txManager.commit(status);
            b = true;
        } else {
            b = this.doDelOne(id, entity);
        }
        return id;
    }

    /**
     * 执行数据库数据删除
     *
     * @param id     主键
     * @param entity 根据主键查询出来的数据库映射对象
     * @return 删除成功与否
     */
    private boolean doDelOne(String id, E entity) {
        final boolean b = service.removeById(id);
        log.info(String.format("%s：删除ID【%s】数据%s", TAG, id, b ? "成功" : "失败"));
        this.postDelOne(id, entity);
        return b;
    }

    /**
     * 批量删除
     *
     * @param ids 要删除的主键数组
     * @return 删除成功与否
     */
    @DeleteMapping
    public boolean del(String[] ids) {
        if (ids == null || ids.length <= 0) {
            log.info(String.format("%s：要删除的 id 为空", TAG));
            // TODO
            return false;
        }

        List<String> idList = Arrays.asList(ids);

        Collection<E> entities = service.listByIds(idList);

        try {
            dataCheck4DelOne(entities);
        } catch (DataCheckException e) {
            log.info(String.format("%s：删除数据验证不通过", TAG));
            // TODO
            return false;
        }

        this.preDelList(idList, entities);

        boolean b;
        if (this.delInTransaction()) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus status = txManager.getTransaction(def);
            try {
                this.doDelList(idList, entities);
            } catch (Exception ex) {
                txManager.rollback(status);
                throw ex;
            }
            txManager.commit(status);
            b = true;
        } else {
            b = doDelList(idList, entities);
        }
        // TODO
        return b;
    }

    /**
     * 执行数据库数据删除
     *
     * @param idList   要删除的数据主键集合
     * @param entities 要删除的数据库映射对象集合
     * @return 删除成功与否
     */
    private boolean doDelList(List<String> idList, Collection<E> entities) {
        final boolean b = service.removeByIds(idList);
        log.info(String.format("%s：删除数据%s", TAG, b ? "成功" : "失败"));
        this.postDelList(idList, entities);
        return b;
    }

    /**
     * 根据主键查询本表数据
     *
     * @param id 主键
     * @return 主键对应的本表数据
     */
    @GetMapping("/{id}")
    public V get(@PathVariable("id") String id) {
        if (StringUtil.isEmpty(id)) {
            log.info(String.format("%s：id 不能为空", TAG));
            // TODO
            return null;
        }

        final E entity = service.getById(id);
        if (entity == null) {
            // TODO
            return null;
        }

        Class<V> vClass = getClazz4VO();
        V vo;
        try {
            vo = vClass.newInstance();
        } catch (Exception e) {
            log.error(String.format("%s：创建【%s】类型对象失败：【%s】", TAG, vClass.getSimpleName(), e));
            // TODO
            return null;
        }

        if (vo == null) {
            // TODO
            return null;
        }

        vo = this.entity2vo(entity, vo);
        // TODO
        return vo;
    }

    /**
     * 获取列表
     *
     * @param vo 查询条件封装对象
     * @return 根据查询条件查询出来的数据集合
     */
    @GetMapping("/list")
    public List<V> list(V vo) {
        if (vo == null) {
            log.info(String.format("%s：系统异常 vo 不能为 null", TAG));
            // TODO
            return null;
        }

        this.preListCondition(vo);

        Wrapper<E> queryWrapper = this.getWrapperByVO(vo);
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper<>();
        }

        final List<E> entityList = service.list(queryWrapper);

        // TODO
        return this.entityList2VoList(entityList);
    }

    /**
     * 获取分页模糊列表
     *
     * @param pageSearch 分页数据封装对象
     * @return 分页数据
     */
    @PostMapping("/page")
    public Page<V> page(@RequestBody PageSearch<V> pageSearch) {
        if (pageSearch == null) {
            log.info(String.format("%s：系统异常 pageSearch 不能为 null", TAG));
            // TODO
            return null;
        }
        Page<V> pageInfo = pageSearch.getPage();
        Page<E> page = this.getPageCondition(pageInfo);
        final V vo = pageSearch.getSearch();

        this.preListCondition(vo);
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
            return new Page<>();
        }
        final List<E> records = pageData.getRecords();
        final List<V> voList = this.entityList2VoList(records);
        BeanUtils.copyProperties(pageData, pageInfo);
        pageInfo.setRecords(voList);
        return pageInfo;
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

    /**
     * 校验保存请求参数，子类可重写
     *
     * @param vo 客户端端提供的入参对象封装
     * @throws DataCheckException 若某参数验证不通过抛出此异常信息
     */
    protected void dataCheck4Save(V vo) throws DataCheckException {
    }

    /**
     * 校验删除请求参数，子类可重写
     *
     * @param entity 根据主键查询出来的数据库映射对象
     * @throws DataCheckException 若某参数验证不通过抛出此异常信息
     */
    protected void dataCheck4DelOne(E entity) throws DataCheckException {
    }

    /**
     * 删除前数据验证
     *
     * @param entities 要删除的数据库映射对象集合
     * @throws DataCheckException 若某参数验证不通过抛出此异常信息
     */
    protected void dataCheck4DelOne(Collection<E> entities) throws DataCheckException {
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
     * 允许子类在本表数据删除之后做一些事情
     *
     * @param deletedId     被删除的数据主键
     * @param deletedEntity 被删除的数据库映射对象
     */
    protected void postDelOne(String deletedId, E deletedEntity) {

    }

    /**
     * 允许子类在本表数据删除之前做一些事情
     *
     * @param toDelId     要删除的主键
     * @param ToDelEntity 要删除的数据库映射对象
     */
    protected void preDelOne(String toDelId, E ToDelEntity) {
    }

    /**
     * 允许子类在本表数据删除之后做一些事情
     *
     * @param deletedIdList   已删除的主键集合
     * @param deletedEntities 已删除的数据库映射对象集合
     */
    protected void postDelList(List<String> deletedIdList, Collection<E> deletedEntities) {
    }

    /**
     * 允许子类在本表数据删除之前做一些事情
     *
     * @param toDelIds      要删除的主键集合
     * @param toDelEntities 要删除的数据库映射实体集合
     */
    protected void preDelList(List<String> toDelIds, Collection<E> toDelEntities) {
    }

    /**
     * 是否在事务中执行保存动作
     */
    protected boolean saveInTransaction() {
        return false;
    }

    /**
     * 是否在事务中执行删除动作
     */
    protected boolean delInTransaction() {
        return false;
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
     * 允许子类在构造查询条件封装对象之前做一些事情
     *
     * @param vo 客户端提供的参数封装对象
     */
    protected void preListCondition(V vo) {
    }

    /**
     * @param vo 客户端提供的参数封装对象
     * @return 数据库查询条件封装对象
     */
    protected Wrapper<E> getWrapperByVO(V vo) {
        final Class<V> voClazz = getClazz4VO();
        final Class<E> entityClazz = getClazz4Entity();
        QueryWrapper<E> wrapper = WrapperUtil.getWrapperByVO(entityClazz, voClazz, vo);
        if (wrapper == null) {
            wrapper = new QueryWrapper<>();
        }
        return wrapper;
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

    /**
     * 分页列表升序字段
     *
     * @param pageInfo 分页参数封装对象
     * @return 分页列表升序字段数组
     */
    protected String[] asc(Page<V> pageInfo) {
        return null;
    }

    /**
     * 分页列表降序字段
     *
     * @param pageInfo 分页参数封装对象
     * @return 分页列表升序字段数组
     */
    protected String[] desc(Page<V> pageInfo) {
        return null;
    }
}
