package com.wangjg.framework.pojo.vo.teacher;


import com.wangjg.framework.util.wrapper.annotation.EqualQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 学校信息
 * </p>
 *
 * @author wangjg
 * @since 2020-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TeacherVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @EqualQuery
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 性别：1男，2女
     */
    private Integer gender;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;



}
