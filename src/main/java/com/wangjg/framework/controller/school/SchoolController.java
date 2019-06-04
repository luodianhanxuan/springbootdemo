package com.wangjg.framework.controller.school;

import org.springframework.web.bind.annotation.RequestMapping;
import com.wangjg.framework.controller.GeneralController;
import com.wangjg.framework.pojo.entity.school.School;
import com.wangjg.framework.service.school.ISchoolService;
import com.wangjg.framework.pojo.vo.school.SchoolVO;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 学校 前端控制器
 * </p>
 *
 * @author wangjg
 * @since 2019-06-04
 */
@RestController
@RequestMapping("/school")
public class SchoolController extends GeneralController<ISchoolService, School, SchoolVO>{

}

