package com.vangel.xmldp.utils;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public final class StringUtils {
    public static boolean isEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }

    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }


    private StringUtils() {}
}
