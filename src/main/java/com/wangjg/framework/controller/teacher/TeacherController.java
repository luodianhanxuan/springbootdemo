package com.wangjg.framework.controller.teacher;

import org.springframework.web.bind.annotation.RequestMapping;
import com.wangjg.framework.controller.GeneralController;
import com.wangjg.framework.pojo.entity.teacher.Teacher;
import com.wangjg.framework.service.teacher.ITeacherService;
import com.wangjg.framework.pojo.vo.teacher.TeacherVO;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 教师表 前端控制器
 * </p>
 *
 * @author wangjg
 * @since 2019-06-04
 */
@RestController
@RequestMapping("/teacher")
public class TeacherController extends GeneralController<ITeacherService, Teacher, TeacherVO>{

}

