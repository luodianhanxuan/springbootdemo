package com.wangjg.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangjg
 * @date 2018/10/26
 * Desc:
 */
public class StringUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);
    private static final String TAG = "字符串处理工具类";

    private StringUtil() {
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 截取数据库字段描述信息：从字符串第0位截取到第一个不是中文或者英文的字符的前一个字符
     *
     * @param comment 要截取的数据库描述信息
     * @return 从字符串第0位截取到第一个不是中文或者英文的字符的前一个字符
     */
    public static String substr4TableComment(String comment) {
        if (isEmpty(comment)) {
            return "";
        }
        Matcher matcher = Pattern.compile("([\u4E00-\u9FA5]|[a-zA-z])+").matcher(comment.trim());
        if (!matcher.find()) {
            return "";
        }
        return matcher.group();
    }

    /**
     * 隐藏手机号码中间四位
     *
     * @param mobile 完整的手机号码
     * @return 隐藏中间四位后的手机号码
     */
    public static String hideMobileMiddle(String mobile) {
        if (isEmpty(mobile)) {
            return "";
        }
        Matcher matcher = Pattern.compile("(\\d{3})(\\d{4})(\\d{4})").matcher(mobile.trim());
        if (!matcher.find()) {
            return "";
        }
        return matcher.group(1) + "****" + matcher.group(3);
    }

//    public static String hideMobileMiddle(String mobile) {
//        if (isEmpty(mobile)) {
//            return "";
//        }
//        return mobile.replaceAll("(\\d{3})(\\d{4})(\\d{4})","$1****$3");
//    }

    public static boolean isMobileNum(String str) {
        if (isEmpty(str)) {
            return false;
        }
        Matcher matcher = Pattern.compile("^1[3|4|5|6|7|8|9]\\d{9}$").matcher(str);
        return matcher.matches();
    }


    /**
     * 如果addr是以www | WWW开头，返回http://+addr
     *
     * @param addr 网址
     * @return 校验过的地址
     */
    public static String getHttpAddrByAddr(String addr) {
        String value = "";

        if (StringUtil.isEmpty(addr)) {
            return value;
        }

        String http = "http://";
        String https = "https://";
        String www = "www.";

        if (addr.startsWith(www) || addr.startsWith(www.toUpperCase())) {
            return String.format("%s%s", http, addr.replace(www.toUpperCase(), www));
        }

        if (addr.startsWith(http) || addr.startsWith(http.toUpperCase())) {
            return addr.replace(http.toUpperCase(), http);
        }
        if (addr.startsWith(https) || addr.startsWith(https.toUpperCase())) {
            return addr.replace(https.toUpperCase(), https);
        }

        return addr;
    }

    public static <T> String parseListStringToStringWithSeparator(Collection<T> strs, String separator) {

        return CollectionUtil.parseListStringToStringWithSeparator(strs, separator);
    }


    public static String removeTheFirstLetter(String s) {
        String regular = "(?<=[a-z])[A-Z].*";
        Matcher matcher = Pattern.compile(regular).matcher(s);
        if (matcher.find()) {
            String remainWords = matcher.group();
            return remainWords.substring(0, 1).toLowerCase() + remainWords.substring(1);
        }
        return s;
    }

    public static String replace(String regexp, String replacement, String content) {
        if (StringUtil.isEmpty(content) || StringUtil.isEmpty(regexp) || StringUtil.isEmpty(replacement)) {
            return content;
        }
        return content.replaceAll(regexp, replacement);
    }

    public static boolean match(String regex, String s) {
        if (isEmpty(regex) || isEmpty(s)) {
            return false;
        }

        Matcher matcher = Pattern.compile(regex).matcher(s);
        return matcher.matches();
    }
}
