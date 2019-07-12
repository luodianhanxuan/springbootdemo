package com.wangjg.framework.util.wrapper.annotation;

import java.lang.annotation.*;

/**
 * @author wangjg
 * 2019-07-11
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface EqualQuery {
}
