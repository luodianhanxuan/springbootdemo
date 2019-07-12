package com.wangjg.framework.pojo.vo.school;

import com.wangjg.framework.util.wrapper.annotation.BeginQuery;
import com.wangjg.framework.util.wrapper.annotation.EndQuery;
import com.wangjg.framework.util.wrapper.annotation.EqualQuery;
import com.wangjg.framework.util.wrapper.annotation.LikeQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 学校
 * </p>
 *
 * @author wangjg
 * @since 2019-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SchoolVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 名称
     */
    @EqualQuery
    private String name;

    /**
     * 学校地址
     */
    @LikeQuery
    private String address;

    /**
     * 建校时间
     */
    @BeginQuery
    private LocalDate birthday;

    @EndQuery
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @BeginQuery
    private LocalDateTime updateTime;

}
