#### Spring Boot Simple 框架
本项目旨在消除乏味的单表 CURD 代码

#### 思路
通过模版引擎ORM实体以及 Spring 三层架构的基础类
* Controller   
```java
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
```
可以通过 override 父类方法来进行数据验证
* Service
```java
/**
 * <p>
 * 学校信息 服务类
 * </p>
 *
 * @author wangjg
 * @since 2020-06-21
 */
public interface ITeacherService extends GeneralService<Teacher, TeacherVO> {

}
```
* ServiceImpl 
```java
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
```
可以通过 override 父类的 hook 方法来达到个性化需求
#### 默认接口
以 teacherController 为例
* 保存或更新接口
    * 接口路径：/teacher（子类中定义的接口路径）
    * 请求方式：POST
    * 入参方式：在 RequestBody 中设置需要保存到数据库表中的每一个字段（须与数据库字段名一一对应）
    * 出参：返回保存后的数据
* 按照主键删除接口
    * 接口路径：/teacher/{id}
    * 请求方式：DELETE
    * 入参方式：id 放在请求路径中（将真实 id 替换 {id}）
    * 出参：返回删除的数据 id
* 按照主键批量删除接口
    * 接口路径：/teacher
    * 请求方式：DELETE
    * 入参方式：以 query 方式将数组放入到请求路径中 
    * 出参：true | false（删除成功与否）
* 根据主键查询本表数据接口
    * 接口路径：/teacher/{id}
    * 请求方式：GET
    * 入参方式：id 放在请求路径中（将真实 id 替换 {id}）
    * 出参：返回该 id 对应的数据库数据
* 根据查询条件获取列表
    * 接口路径：/teacher/list
    * 请求方式：POST
    * 入参方式：在 RequestBody 中设置查询条件，默认须一一对应数据库表中字段
    * 出参：返回符合该查询的所有数据
    * 框架会根据入参以责任链 + 自定义注解 的方式自动封装查询条件，只需要在被查询的字段上加上响应注解就可以了，默认只支持 begin、end、equal、like 方式查询，如有其他需求，可自定义扩展（详见 WrapperUtil）
    ```java
      /**
       * @author wangjg
       * 2019-06-10
       */
      public class EqualQueryFieldHandler implements QueryFieldHandler {
          @Override
          public <E> void handler(String fieldName, Object value, Field field, QueryWrapper<E> wrapper, QueryFieldHandlerChain handlerChain) {
              if (field != null
                      && field.getAnnotationsByType(EqualQuery.class) != null
                      && field.getAnnotationsByType(EqualQuery.class).length > 0) {
                  wrapper.eq(true, ReflectUtil.underline(fieldName), value);
              } else {
                  handlerChain.doHandler(fieldName, value, field, wrapper);
              }
      
          }
      }
    ```
* 根据查询条件获取分页数据
    * 接口路径：/teacher/page
    * 请求方式：POST
    * 入参方式：在 list 接口基础上增加了分页参数，查询条件参考 list 接口
    * 出参：返回符合该查询的分页数据