package com.wangjg.framework.service.impl.teacher;

import com.wangjg.framework.pojo.entity.teacher.Teacher;
import com.wangjg.framework.pojo.vo.teacher.TeacherVO;
import com.wangjg.framework.mapper.teacher.TeacherMapper;
import com.wangjg.framework.service.teacher.ITeacherService;
import com.wangjg.framework.service.impl.GeneralServiceImpl;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 学校信息 服务实现类
 * </p>
 *
 * @author wangjg
 * @since 2020-06-21
 */
@Service
public class TeacherServiceImpl extends GeneralServiceImpl<TeacherMapper, Teacher, TeacherVO> implements ITeacherService {

}
