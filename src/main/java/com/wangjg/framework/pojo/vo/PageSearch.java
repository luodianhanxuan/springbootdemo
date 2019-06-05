package com.wangjg.framework.pojo.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * @author wangjg
 * 2019-06-05
 */
@Data
public class PageSearch<V> {
    /***
     * 分页信息
     */
    private Page<V> page;

    /**
     * 搜索实体
     */
    private V search;
}
