package com.kai.webby.util;

public class StringUtil {

    public static boolean isEmpty(String str) {
        if (null == str || "".equals(str)) {
            return true;
        } else {
            return false;
        }
    }
}
