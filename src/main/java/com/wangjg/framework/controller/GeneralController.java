package com.wangjg.framework.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wangjg.framework.exception.DataCheckException;
import com.wangjg.framework.pojo.vo.PageSearch;
import com.wangjg.framework.service.GeneralService;
import com.wangjg.framework.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author wangjg
 * 2019-06-04
 */

@SuppressWarnings({"WeakerAccess", "RedundantThrows", "SpringJavaInjectionPointsAutowiringInspection"})
@Slf4j
public class GeneralController<S extends GeneralService<E, V>, E, V> extends BaseController {

    private static final String TAG = "通用 WEB 控制器";

    /**
     * 本表 service 对象
     */
    @Autowired
    protected S service;

    /**
     * 保存或更新方法
     *
     * @param vo 客户端提供的参数封装对象
     * @return 填充保存数据过后的 vo
     */
    @PostMapping
    public V save(@RequestBody V vo) throws Exception {
        if (vo == null) {
            throw new DataCheckException(String.format("%s：vo 对象不能为 null", TAG));
        }
        log.info(vo.toString());
        this.dataCheck4Save(vo);
        return service.insertOrUpdate(vo);
    }

    /**
     * 按照主键删除方法
     *
     * @param id 主键
     * @return 删除的主键
     */
    @DeleteMapping("/{id}")
    public Serializable del(@PathVariable("id") String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new DataCheckException(String.format("%s：id 不能为空", TAG), TAG);
        }

        final E entity = service.getById(id);
        if (entity == null) {
            throw new DataCheckException(String.format("%s：不存在ID【%s】", TAG, id), TAG);
        }
        dataCheck4DelOne(entity);
        return service.del(id);
    }

    /**
     * 批量删除
     *
     * @param ids 要删除的主键数组
     * @return 删除成功与否
     */
    @DeleteMapping
    public boolean del(String[] ids) throws Exception {
        if (ids == null || ids.length <= 0) {
            throw new DataCheckException(String.format("%s：要删除的 id 为空", TAG), "要删除的 id 为空");
        }

        List<String> idList = Arrays.asList(ids);
        Collection<E> entities = service.listByIds(idList);
        dataCheck4DelOne(entities);
        return service.del(ids);
    }

    /**
     * 根据主键查询本表数据
     *
     * @param id 主键
     * @return 主键对应的本表数据
     */
    @GetMapping("/{id}")
    public V get(@PathVariable("id") String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new DataCheckException(String.format("%s：id 不能为空", TAG), "id 不能为空");
        }
        return service.get(id);
    }

    /**
     * 获取列表
     *
     * @param vo 查询条件封装对象
     * @return 根据查询条件查询出来的数据集合
     */
    @PostMapping("/list")
    public List<V> list(@RequestBody V vo) {
        return service.list(vo);
    }

    /**
     * 获取分页模糊列表
     *
     * @param pageSearch 分页数据封装对象
     * @return 分页数据
     */
    @PostMapping("/page")
    public Page<V> page(@RequestBody PageSearch<V> pageSearch) {
        return service.page(pageSearch);
    }

    /**
     * 校验保存请求参数，子类可重写
     *
     * @param vo 客户端端提供的入参对象封装
     * @throws DataCheckException 若某参数验证不通过抛出此异常信息
     */
    @SuppressWarnings("unused")
    protected void dataCheck4Save(V vo) throws DataCheckException {
    }

    /**
     * 校验删除请求参数，子类可重写
     *
     * @param entity 根据主键查询出来的数据库映射对象
     * @throws DataCheckException 若某参数验证不通过抛出此异常信息
     */
    @SuppressWarnings("unused")
    protected void dataCheck4DelOne(E entity) throws DataCheckException {
    }

    /**
     * 删除前数据验证
     *
     * @param entities 要删除的数据库映射对象集合
     * @throws DataCheckException 若某参数验证不通过抛出此异常信息
     */
    @SuppressWarnings("unused")
    protected void dataCheck4DelOne(Collection<E> entities) throws DataCheckException {
    }
}
