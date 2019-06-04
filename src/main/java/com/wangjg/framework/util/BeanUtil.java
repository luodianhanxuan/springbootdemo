package com.wangjg.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;

public class BeanUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtil.class);

    /**
     * 作用域 只对 private 属性进行复制
     * <p>
     * 忽略  final,static 修饰的变量
     * <p>
     * 属性赋值
     *
     * @param dest 目标对象
     * @param orig 发起对象
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void copyProperties(Object dest, Object orig) {

        String origClassName = orig.getClass().getSimpleName();
        String destClassName = dest.getClass().getSimpleName();
        long start = System.currentTimeMillis();
        LOGGER.info(String.format("属性拷贝【%s -> %s】 拷贝开始！", origClassName, destClassName));
        Field[] destField = dest.getClass().getDeclaredFields();

        for (Field field : destField) { // 遍历所有属性
            //名称
            String name = field.getName();
            //属性类型
            String typeName = field.getType().getName();
            boolean ignoreFiled = checkFiledIgnore(field);
            if (ignoreFiled) {
                LOGGER.error(String.format("属性拷贝【%s -> %s】 拷贝属性：%s 非private 或者 含有 final，static已经忽略！", origClassName, destClassName, name));
                continue;
            }
            field.setAccessible(true);
            boolean copy = true;
            Object value = null;
            switch (typeName) {
                case "int":
                    value = getInteger(orig, name);
                    if (null == value) {
                        value = 0;
                    }
                    break;
                case "java.lang.Integer":
                    value = getInteger(orig, name);
                    break;
                case "double":
                    value = getDouble(orig, name);
                    if (null == value) {
                        value = 0d;
                    }
                    break;
                case "java.lang.Double":
                    value = getDouble(orig, name);
                    break;
                case "java.lang.String":
                    value = getString(orig, name);
                    break;
                case "java.util.Date":
                    value = getDate(orig, name);
                    break;
                case "long":
                    value = getLong(orig, name);
                    if (null == value) {
                        value = 0L;
                    }
                    break;
                case "java.lang.Long":
                    value = getLong(orig, name);
                    break;
                case "boolean":
                    value = getBoolean(orig, name);
                    if (null == value) {
                        value = false;
                    }
                    break;
                case "java.lang.Boolean":
                    value = getBoolean(orig, name);
                    break;
                case "java.math.BigDecimal":
                    value = getBigDecimal(orig, name);
                    break;
                default:
                    LOGGER.error(String.format("属性拷贝【%s -> %s】 拷贝属性：%s 未找到符合类型 %s，本次忽略！ ", origClassName, destClassName, name, typeName));
                    copy = false;
                    break;
            }
            if (copy) {
                if (null != value) {
                    try {
                        field.set(dest, value);
                    } catch (IllegalAccessException e) {
                        LOGGER.info(String.format("属性拷贝【%s -> %s】 拷贝属性：%s 类型：%s, 值：%s  出错，已忽略 ", origClassName, destClassName, name, typeName, value));
                    }
//                    LOGGER.info(String.format("属性拷贝【%s -> %s】 拷贝属性：%s 类型：%s, 值：%s ",origClassName,destClassName,name,typeName,value));
                } else {
//                    LOGGER.error(String.format("属性拷贝【%s -> %s】 拷贝属性：%s 类型：%s, 值为空不能拷贝 ",origClassName,destClassName,name,typeName));
                }

            }
        }
        long end = System.currentTimeMillis();
        LOGGER.info(String.format("属性拷贝【%s -> %s】 拷贝结束 耗时：%s 毫秒", origClassName, destClassName, end - start));
    }

    /**
     * 含有非 private 修饰符，static，final 修饰符的时候忽略字符复制
     *
     * @param field
     * @return
     */
    private static boolean checkFiledIgnore(Field field) {

        int mod = field.getModifiers();

        String modifier = Modifier.toString(mod);

        if (modifier.contains("private") && !modifier.contains("static") && !modifier.contains("final")) {
            return false;
        }
        return true;
    }

    private static Integer getInteger(Object object, String name) {

        Field[] origField = object.getClass().getDeclaredFields();
        for (Field field : origField) {
            field.setAccessible(true);
            try {
                if (field.getName().equalsIgnoreCase(name)) {
                    String typeName = field.getType().getName();
                    if (typeName.equalsIgnoreCase("java.lang.Integer")
                            || typeName.equalsIgnoreCase("int")) {
                        if (null != field.get(object)) {
                            return (Integer) field.get(object);
                        }
                    }
                    break;
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("getInteger", e);
            }
        }
        return null;
    }

    private static Date getDate(Object object, String name) {

        Field[] origField = object.getClass().getDeclaredFields();
        for (Field field : origField) {
            field.setAccessible(true);
            try {
                if (field.getName().equalsIgnoreCase(name)) {
                    String typeName = field.getType().getName();
                    if (typeName.equalsIgnoreCase("java.util.Date")) {
                        if (null != field.get(object)) {
                            return (Date) field.get(object);
                        }
                    }
                    break;
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("getDate", e);
            }
        }
        return null;
    }

    private static String getString(Object object, String name) {

        Field[] origField = object.getClass().getDeclaredFields();
        for (Field field : origField) {
            field.setAccessible(true);
            try {
                if (field.getName().equalsIgnoreCase(name)) {
                    String typeName = field.getType().getName();
                    if (typeName.equalsIgnoreCase("java.lang.String")) {
                        if (null != field.get(object)) {
                            return (String) field.get(object);
                        }
                    }
                    break;
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("getString", e);
            }
        }
        return null;
    }

    private static Double getDouble(Object object, String name) {

        Field[] origField = object.getClass().getDeclaredFields();
        for (Field field : origField) {
            field.setAccessible(true);
            try {
                if (field.getName().equalsIgnoreCase(name)) {
                    String typeName = field.getType().getName();
                    if (typeName.equalsIgnoreCase("java.lang.Double")
                            || typeName.equalsIgnoreCase("double")) {
                        if (null != field.get(object)) {
                            return (Double) field.get(object);
                        }
                    }
                    break;
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("getDouble", e);
            }
        }
        return null;
    }

    private static Boolean getBoolean(Object object, String name) {

        Field[] origField = object.getClass().getDeclaredFields();
        for (Field field : origField) {
            field.setAccessible(true);
            try {
                if (field.getName().equalsIgnoreCase(name)) {
                    String typeName = field.getType().getName();
                    if (typeName.equalsIgnoreCase("java.lang.Boolean")
                            || typeName.equalsIgnoreCase("boolean")) {
                        if (null != field.get(object)) {
                            return (Boolean) field.get(object);
                        }
                    }
                    break;
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("getBoolean", e);
            }
        }
        return null;
    }

    private static BigDecimal getBigDecimal(Object object, String name) {
        Field[] origField = object.getClass().getDeclaredFields();
        for (Field field : origField) {
            field.setAccessible(true);
            try {
                if (field.getName().equalsIgnoreCase(name)) {
                    String typeName = field.getType().getName();
                    if (typeName.equalsIgnoreCase("java.math.BigDecimal")) {
                        if (null != field.get(object)) {
                            return (BigDecimal) field.get(object);
                        }
                    }
                    break;
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("getBigDecimal", e);
            }
        }
        return null;
    }

    private static Long getLong(Object object, String name) {

        Field[] origField = object.getClass().getDeclaredFields();
        for (Field field : origField) {
            field.setAccessible(true);
            try {
                if (field.getName().equalsIgnoreCase(name)) {
                    String typeName = field.getType().getName();
                    if (typeName.equalsIgnoreCase("java.lang.Long")
                            || typeName.equalsIgnoreCase("long")) {
                        if (null != field.get(object)) {
                            return (Long) field.get(object);
                        }
                    }
                    break;
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("getLong", e);
            }
        }
        return null;
    }

}