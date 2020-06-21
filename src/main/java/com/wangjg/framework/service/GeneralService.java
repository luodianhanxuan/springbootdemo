package com.wangjg.framework.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wangjg.framework.pojo.vo.PageSearch;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangjg
 * 2020/6/21
 */
public interface GeneralService<E, V> extends IService<E> {

    V insertOrUpdate(V vo) throws Exception;

    Serializable del(Serializable id) throws Exception;

    boolean del(Serializable[] ids) throws Exception;

    V get(Serializable id) throws Exception;

    List<V> list(V vo);

    Page<V> page(@RequestBody PageSearch<V> pageSearch);
}
