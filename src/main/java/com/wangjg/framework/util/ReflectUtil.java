package com.wangjg.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangjg
 * @date 2018/9/30
 * Desc:
 */

@SuppressWarnings("WeakerAccess")
public class ReflectUtil {
    private ReflectUtil() {
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(ReflectUtil.class);
    private final static String TAG = "反射工具类";


    /**
     * 判断两个相同类型的对象的属性值是否相等
     */
    public static <T> Boolean comparePropertiesExcludeFields(T t1, T t2, String... excludeFields) throws Exception {

        return compareProperties(t1, t2, (s) -> excludeFields != null
                && excludeFields.length > 0
                && Arrays.asList(excludeFields).contains(s));
    }


    /**
     * 判断两个相同类型的对象的属性值是否相等
     */
    public static <T> Boolean compareProperties(T t1, T t2, String... fieldNames) throws Exception {

        return compareProperties(t1, t2, (s) -> fieldNames != null
                && fieldNames.length > 0
                && !Arrays.asList(fieldNames).contains(s));
    }

    /**
     * 判断两个相同类型的对象的属性值是否相等
     */
    public static <T> Boolean compareProperties(T t1, T t2, Predicate<String> predicateToSkipField) throws Exception {
        //为空判断
        if (t1 == null && t2 == null) {
            return Boolean.TRUE;
        } else if (t1 == null || t2 == null) {
            return Boolean.FALSE;
        }

        Class<?> classType = t1.getClass();

        Field[] fields = t1.getClass().getDeclaredFields();//获得所有字段
        PropertyDescriptor propertyDescriptor;
        for (Field field : fields) {
            // 如果该
            if (predicateToSkipField.test(field.getName())) {
                continue;
            }

            int mod = field.getModifiers();
            String modifier = Modifier.toString(mod);

            if (modifier.contains("static") || modifier.contains("final")) {
                continue;
            }

            propertyDescriptor = new PropertyDescriptor(field.getName(), classType);//获得类中字段的属性描述
            Method getMethod = propertyDescriptor.getReadMethod();//从属性描述中获得字段的get方法
            //通过getMethod.invoke(obj)方法获得obj对象中该字段get方法返回的值
            if (!Objects.equals(getMethod.invoke(t1), getMethod.invoke(t2))) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }


    /**
     * 判断两个相同类型的对象的属性值是否相等
     */
    public static <T> List<CompareDifferentResult> comparePropertiesWithDifferentValue(T first, T sencond) throws Exception {
        return comparePropertiesWithDifferentValue(first, sencond, null, null);
    }

    /**
     * 判断两个相同类型的对象的属性值是否相等
     */
    public static <T> List<CompareDifferentResult> comparePropertiesWithDifferentValue(T first, T sencond, String[] excludeFields) throws Exception {
        return comparePropertiesWithDifferentValue(first, sencond, null, excludeFields);
    }


    /**
     * 判断两个相同类型的对象的属性值是否相等
     */
    public static <T> List<CompareDifferentResult> comparePropertiesWithDifferentValue(T first, T sencond, String[] fieldNames, String[] excludeFields) {
        //为空判断
        if (first == null && sencond == null) {
            return new ArrayList<>();
        } else if (first == null || sencond == null) {
            return new ArrayList<>();
        }

        Class<?> classType = first.getClass();
        //如果传入的类型不一样则直接返回false
       /* //compareProperties<T>中的<T>可以限定传入的类型必须一致，所以不需要该判断
        if (classType != sencond.getClass()) {
            return Boolean.FALSE;
        }*/

        Field[] fields = first.getClass().getDeclaredFields();//获得所有字段
        PropertyDescriptor propertyDescriptor;
        List<CompareDifferentResult> differentFields = new ArrayList<>();
        CompareDifferentResult result;
        Object firstValue;
        Object secondField;
        for (Field field : fields) {
            if (fieldNames != null
                    && fieldNames.length > 0
                    && !Arrays.asList(fieldNames).contains(field.getName())) {
                continue;
            }
            if (excludeFields != null
                    && excludeFields.length > 0
                    && Arrays.asList(excludeFields).contains(field.getName())) {
                continue;
            }
            try {
                propertyDescriptor = new PropertyDescriptor(field.getName(), classType);//获得类中字段的属性描述
                Method getMethod = propertyDescriptor.getReadMethod();//从属性描述中获得字段的get方法
                //通过getMethod.invoke(obj)方法获得obj对象中该字段get方法返回的值
                firstValue = getMethod.invoke(first);
                secondField = getMethod.invoke(sencond);
                if (!Objects.equals(firstValue, secondField)) {
                    result = new CompareDifferentResult(getPrimitive2BoxTypeClassIfNeed(field.getType()));
                    result.setField(underline(field.getName()));
                    result.setFirstValue(firstValue);
                    result.setSencondValue(secondField);
                    differentFields.add(result);
                }
            } catch (Exception e) {
                LOGGER.error(String.format("%s：对象对比异常，%s", TAG, e.getMessage()));
            }
        }
        return differentFields;
    }


    public static class CompareDifferentResult {
        private String field;
        private Object firstValue;
        private Object sencondValue;
        private Class type;

        public CompareDifferentResult(Class type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "CompareDifferentResult{" +
                    "field='" + field + '\'' +
                    ", firstValue=" + firstValue +
                    ", sencondValue=" + sencondValue +
                    '}';
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Object getFirstValue() {
            return firstValue;
        }

        public void setFirstValue(Object firstValue) {
            this.firstValue = firstValue;
        }

        public Object getSencondValue() {
            return sencondValue;
        }

        public void setSencondValue(Object sencondValue) {
            this.sencondValue = sencondValue;
        }

        public Class getType() {
            return type;
        }

        public void setType(Class type) {
            this.type = type;
        }
    }

    public static List<String> fields(Object obj) {
        return fields(obj, null);
    }

    public static List<String> fields(Object obj, String[] excludeNames) {
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        if (declaredFields.length <= 0) {
            return new ArrayList<>();
        }

        List<String> fields = new ArrayList<>();
        String name;
        for (Field declaredField : declaredFields) {
            name = declaredField.getName();
            if (excludeNames != null && excludeNames.length > 0 && Arrays.asList(excludeNames).contains(declaredField.getName())) {
                continue;
            }
            fields.add(underline(name));
        }
        return fields;
    }

    public static String camel(String name) {
        StringBuffer sb = camel(new StringBuffer(name));
        return sb.toString();
    }

    private static StringBuffer camel(StringBuffer str) {
        //利用正则删除下划线，把下划线后一位改成大写
        Pattern pattern = Pattern.compile("_(\\w)");
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb);
        } else {
            return sb;
        }
        return camel(sb);
    }


    /**
     * 在大写字母前加 _ （驼峰转下划线）
     *
     * @param name 字段名称
     * @return 转换后的字段名称
     */
    public static String underline(String name) {
        StringBuffer sb = underline(new StringBuffer(name));
        return sb.toString();
    }

    private static StringBuffer underline(StringBuffer str) {
        Pattern pattern = Pattern.compile("[A-Z]");
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb);
        } else {
            return sb;
        }
        return underline(sb);
    }

    /**
     * 获取对象的指定属性的值
     *
     * @param object     指定对象
     * @param fieldNames 属性集合
     * @return
     */
    public static Map<String, Object> getFieldValue(Object object, String[] fieldNames) {
        Map<String, Object> data = new HashMap<>();
        if (fieldNames.length <= 0) {
            return data;
        }

        Class<?> aClass = object.getClass();
        String className = aClass.getSimpleName();

        String fieldNameWithFirstLetterUpper;
        String getterMethodName;
        Method getterMethod;
        Object value;
        for (String fieldName : fieldNames) {
            if (StringUtil.isEmpty(fieldName)) {
                continue;
            }
            fieldNameWithFirstLetterUpper = fieldNameWithFirstLetterUpper(fieldName);
            getterMethodName = String.format("get%s", fieldNameWithFirstLetterUpper);
            try {
                getterMethod = aClass.getDeclaredMethod(getterMethodName);
                value = getterMethod.invoke(object);
                data.put(fieldName, value);
            } catch (Exception e) {
                LOGGER.error(String.format("%s:调用[%s]的[%s]的getter[%s]失败", TAG, className, fieldName, getterMethodName));
            }
        }
        return data;
    }

    /**
     * 获取对象的指定属性的值
     *
     * @param object    指定对象
     * @param fieldName 属性
     * @return 属性值
     */
    public static Object getFieldValue(Object object, String fieldName) {
        if (StringUtil.isEmpty(fieldName)) {
            return null;
        }

        Class<?> aClass = object.getClass();
        String className = aClass.getSimpleName();
        String getterMethodName = null;
        try {
            Field field = aClass.getDeclaredField(fieldName);
            getterMethodName = getterNameWithFieldName(field);
            if (StringUtil.isEmpty(getterMethodName)) {
                LOGGER.error(String.format("%s：获取类【%s】属性【%s】getter方法名失败", TAG, className, field));
                return null;
            }

            Method getterMethod = aClass.getMethod(getterMethodName);
            return getterMethod.invoke(object);
        } catch (NoSuchFieldException e) {
            LOGGER.error(String.format("%s：类【%s】中没有【%s】属性", TAG, className, fieldName));
        } catch (NoSuchMethodException e) {
            LOGGER.error(String.format("%s：类【%s】中没有【%s】属性的方法名为【%s】的方法", TAG, className, fieldName, getterMethodName));
        } catch (IllegalAccessException e) {
            LOGGER.error(String.format("%s：类【%s】中没有【%s】属性的方法名为【%s】的方法访问不到", TAG, className, fieldName, getterMethodName));
        } catch (InvocationTargetException e) {
            LOGGER.error(String.format("%s：类【%s】中没有【%s】属性的方法名为【%s】的方法调用失败", TAG, className, fieldName, getterMethodName));
        }

        return null;
    }


    public static String getterNameWithFieldName(Field field) {
        //是否为boolean 类型，如果是则 getter名称为 isXXX ,否则为 getXXX
        String fieldName = field.getName();
        if (fieldName == null || fieldName.isEmpty()) {
            return null;
        }

        Class<?> type = field.getType();

        String fieldNameWithFirstLetterUpper = fieldNameWithFirstLetterUpper(fieldName);
        boolean isBoolean = (type.isPrimitive() && type == boolean.class) || type == Boolean.class;

        if (isBoolean) {
            if (fieldName.startsWith("is")) {
                return fieldName;
            } else {
                return String.format("is%s", fieldNameWithFirstLetterUpper);
            }
        }

        return String.format("get%s", fieldNameWithFirstLetterUpper);
    }

    public static String setterNameWithFieldName(Field field) {
        //是否为boolean 类型，如果是则 getter名称为 isXXX ,否则为 getXXX
        String fieldName = field.getName();
        if (fieldName == null || fieldName.isEmpty()) {
            return null;
        }
        String fieldNameWithFirstLetterUpper = fieldNameWithFirstLetterUpper(fieldName);
        return String.format("set%s", fieldNameWithFirstLetterUpper);
    }

    /**
     * 返回首字母大写的字符串
     *
     * @param fieldName 字符串形式的属性
     * @return
     */
    public static String fieldNameWithFirstLetterUpper(String fieldName) {
        String firstCharacter = fieldName.substring(0, 1).toUpperCase();
        String remainCharacter = fieldName.substring(1);

        return String.format("%s%s", firstCharacter, remainCharacter);
    }


    public static Class<?> getPrimitive2BoxTypeClassIfNeed(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (type == int.class) {
            return Integer.class;
        } else if (type == byte.class) {
            return Byte.class;
        } else if (type == short.class) {
            return Short.class;
        } else if (type == long.class) {
            return Long.class;
        } else if (type == double.class) {
            return Double.class;
        } else if (type == float.class) {
            return Float.class;
        } else if (type == boolean.class) {
            return Boolean.class;
        } else if (type == Character.class) {
            return Character.class;
        } else {
            return type;
        }
    }

    public static Object parseStrValueType2TypeIfNeeded(Class<?> type, String value) {
        Object result;
        type = getPrimitive2BoxTypeClassIfNeed(type);
        if (type == Integer.class) {
            result = Integer.valueOf(value);
        } else if (type == Byte.class) {
            result = Byte.valueOf(value);
        } else if (type == Short.class) {
            result = Short.valueOf(value);
        } else if (type == Long.class) {
            result = Long.valueOf(value);
        } else if (type == Double.class) {
            result = Double.valueOf(value);
        } else if (type == Float.class) {
            result = Float.class;
        } else if (type == Boolean.class) {
            result = Boolean.valueOf(value);
        } else if (type == Character.class) {
            result = value.charAt(0);
        } else {
            // Str
            result = value;
        }
        return result;
    }

    public static boolean isEmpty(Field field, Object value) {
        // 非空判断
        boolean isEmpty = Boolean.FALSE;
        Class<?> type = field.getType();
        if (type.isPrimitive()) {
            //基本类型
            if (type == int.class) {
                if ((int) value == 0) {
                    isEmpty = Boolean.TRUE;
                }
            } else if (type == byte.class) {
                if ((byte) value == 0) {
                    isEmpty = Boolean.TRUE;
                }
            } else if (type == short.class) {
                if ((short) value == 0) {
                    isEmpty = Boolean.TRUE;
                }
            } else if (type == long.class) {
                if ((long) value == 0L) {
                    isEmpty = Boolean.TRUE;
                }
            } else if (type == double.class) {
                if ((double) value == 0D) {
                    isEmpty = Boolean.TRUE;
                }
            } else if (type == float.class) {
                if ((float) value <= 0F) {
                    isEmpty = Boolean.TRUE;
                }
            } else if (type == boolean.class) {
                if (!(boolean) value) {
                    isEmpty = Boolean.TRUE;
                }
            } else {
                char c = (char) value;
                if ('\u0000' == c) {
                    isEmpty = Boolean.TRUE;
                }
            }
        } else {
            //对象类型
            if (type == String.class) {
                if (value == null || "".equals(value)) {
                    isEmpty = Boolean.TRUE;
                }
            } else if (type == Character.class) {
                if ('\u0000' == (Character) value) {
                    isEmpty = Boolean.TRUE;
                }
            } else {
                if (value == null) {
                    isEmpty = Boolean.TRUE;
                }
            }
        }
        return isEmpty;
    }

    public static void set(Object obj, String fieldName, Object value) {
        if (value == null) {
            return;
        }

        Class<?> aClass = obj.getClass();
        String className = aClass.getSimpleName();
        String setterMethodName = null;
        try {
            Field field = aClass.getDeclaredField(fieldName);
            setterMethodName = setterNameWithFieldName(field);
            if (StringUtil.isEmpty(setterMethodName)) {
                LOGGER.error(String.format("%s：获取类【%s】属性【%s】setter方法名失败", TAG, className, field));
                return;
            }

            Class<?> type = getPrimitive2BoxTypeClassIfNeed(field.getType());
            Method setterMethod = aClass.getMethod(setterMethodName, type);
            setterMethod.invoke(obj, value);
        } catch (NoSuchFieldException e) {
            LOGGER.error(String.format("%s：类【%s】中没有【%s】属性", TAG, className, fieldName));
        } catch (NoSuchMethodException e) {
            LOGGER.error(String.format("%s：类【%s】中没有【%s】属性的方法名为【%s】的方法", TAG, className, fieldName, setterMethodName));
        } catch (IllegalAccessException e) {
            LOGGER.error(String.format("%s：类【%s】中没有【%s】属性的方法名为【%s】的方法访问不到", TAG, className, fieldName, setterMethodName));
        } catch (InvocationTargetException e) {
            LOGGER.error(String.format("%s：类【%s】中没有【%s】属性的方法名为【%s】的方法调用失败", TAG, className, fieldName, setterMethodName));
        }
    }

    public static void main(String[] args) {
    }
}
