package com.wangjg.framework.service.impl.school;

import com.wangjg.framework.mapper.school.SchoolMapper;
import com.wangjg.framework.pojo.entity.school.School;
import com.wangjg.framework.pojo.vo.school.SchoolVO;
import com.wangjg.framework.service.impl.GeneralServiceImpl;
import com.wangjg.framework.service.school.ISchoolService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学校 服务实现类
 * </p>
 *
 * @author wangjg
 * @since 2019-07-11
 */
@Service
public class SchoolServiceImpl extends GeneralServiceImpl<SchoolMapper, School, SchoolVO> implements ISchoolService {

}
