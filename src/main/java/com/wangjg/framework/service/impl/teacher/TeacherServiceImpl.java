package com.wangjg.framework.service.impl.teacher;

import com.wangjg.framework.pojo.entity.teacher.Teacher;
import com.wangjg.framework.mapper.teacher.TeacherMapper;
import com.wangjg.framework.service.teacher.ITeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 教师表 服务实现类
 * </p>
 *
 * @author wangjg
 * @since 2019-06-04
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements ITeacherService {

}
