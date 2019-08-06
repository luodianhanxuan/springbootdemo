package com.wangjg.framework.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author wangjg
 * <p>
 * 2018/10/26
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Slf4j
public class CollectionUtil {

    public static <T> String parseListStringToStringWithSeparator(Collection<T> strs, String separator) {
        StringJoiner sj = new StringJoiner(separator);
        for (T str : strs) {
            sj.add(str.toString());
        }
        return sj.toString();
    }

    public static <T, F> List<T> transferFromList2ToList(Class<T> toClazz, List<F> fromList) {
        List<T> toList = new ArrayList<>();
        if (CollectionUtils.isEmpty(fromList)) {
            return toList;
        }
        T t;
        try {
            for (F f : fromList) {
                if (f == null) {
                    continue;
                }

                t = toClazz.newInstance();

                BeanUtils.copyProperties(f, t);

                toList.add(t);
            }
        } catch (Exception e) {
            log.error("集合元素转换失败", e);
        }
        return toList;
    }

    public static <T, K, F> Map<K, T> transferFromValueMap2ToValueMap(Class<T> toClazz, Map<K, F> fromMap) {
        Map<K, T> toMap = new HashMap<>();

        if (CollectionUtil.isEmpty(fromMap)) {
            return toMap;
        }

        try {
            K k;
            F f;
            T t;
            for (Map.Entry<K, F> kfEntry : fromMap.entrySet()) {
                if (kfEntry == null) {
                    continue;
                }

                k = kfEntry.getKey();
                f = kfEntry.getValue();
                t = toClazz.newInstance();

                BeanUtils.copyProperties(f, t);

                toMap.put(k, t);
            }
        } catch (Exception e) {
            log.error("map 元素转换失败", e);
        }
        return toMap;
    }


    /**
     * 将一个list均分成n个list,主要通过偏移量来实现的
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<>();
        int remaider = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T, D> List<D> sortListByThePrimaryFieldWithValues(List<T> valueListOfPrimaryFiled, List<D> list, String nameOfThePrimaryFiled) {
        if (CollectionUtil.isEmpty(valueListOfPrimaryFiled)) {
            return list;
        }

        if (CollectionUtil.isEmpty(list)) {
            return list;
        }

        if (StringUtil.isEmpty(nameOfThePrimaryFiled)) {
            throw new IllegalArgumentException("nameOfThePrimaryFiled 不能为空");
        }

        Map<T, D> map = new HashMap<>();
        T fieldValue;
        for (D d : list) {
            if (d == null) {
                continue;
            }
            fieldValue = (T) ReflectUtil.getFieldValue(d, nameOfThePrimaryFiled);
            map.put(fieldValue, d);
        }

        List<D> sortedList = new ArrayList<>();
        for (T t : valueListOfPrimaryFiled) {
            if (t == null) {
                continue;
            }
            if (map.get(t) == null) {
                continue;
            }
            sortedList.add(map.get(t));
        }
        return sortedList;
    }

    /**
     * 将一组数据固定分组，每组n个元素
     *
     * @param source 要分组的数据源
     * @param n      每组n个元素
     */
    public static <T> List<List<T>> fixedGrouping(List<T> source, int n) {

        if (null == source || source.size() == 0 || n <= 0)
            return null;
        List<List<T>> result = new ArrayList<>();

        int sourceSize = source.size();
        int size = (source.size() / n);
        for (int i = 0; i < size; i++) {
            List<T> subset = new ArrayList<>();
            for (int j = i * n; j < (i + 1) * n; j++) {
                if (j < sourceSize) {
                    subset.add(source.get(j));
                }
            }
            result.add(subset);
        }
        return result;
    }

    /**
     * 将一组数据固定分组，每组n个元素
     *
     * @param source 要分组的数据源
     * @param n      每组n个元素
     */
    public static <T> List<List<T>> fixedGrouping2(List<T> source, int n) {

        if (null == source || source.size() == 0 || n <= 0)
            return null;
        List<List<T>> result = new ArrayList<>();
        int remainder = source.size() % n;
        int size = (source.size() / n);
        for (int i = 0; i < size; i++) {
            List<T> subset;
            subset = source.subList(i * n, (i + 1) * n);
            result.add(subset);
        }
        if (remainder > 0) {
            List<T> subset;
            subset = source.subList(size * n, size * n + remainder);
            result.add(subset);
        }

        return result;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return CollectionUtils.isEmpty(collection);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return CollectionUtils.isEmpty(map);
    }
}
