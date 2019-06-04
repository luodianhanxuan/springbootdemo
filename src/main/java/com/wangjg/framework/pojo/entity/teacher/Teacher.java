package com.wangjg.framework.pojo.entity.teacher;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 教师表
 * </p>
 *
 * @author wangjg
 * @since 2019-06-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("d_teacher")
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 教师名称
     */
    private String name;

    /**
     * 教师年龄
     */
    private Integer age;

    /**
     * 教师生日
     */
    private LocalDate birthday;

    /**
     * 性别
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
