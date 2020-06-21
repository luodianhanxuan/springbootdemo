package com.wangjg.framework.controller.teacher;


import com.wangjg.framework.controller.GeneralController;
import com.wangjg.framework.pojo.entity.teacher.Teacher;
import com.wangjg.framework.pojo.vo.teacher.TeacherVO;
import com.wangjg.framework.service.teacher.ITeacherService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 学校信息 前端控制器
 * </p>
 *
 * @author wangjg
 * @since 2020-06-21
 */
@RestController
@RequestMapping("/teacher")
public class TeacherController extends GeneralController<ITeacherService, Teacher, TeacherVO> {

}

