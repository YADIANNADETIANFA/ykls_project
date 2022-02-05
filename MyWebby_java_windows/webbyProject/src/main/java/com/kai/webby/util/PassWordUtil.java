package com.kai.webby.util;

import java.util.HashSet;
import java.util.Set;

public class PassWordUtil {
    public static boolean isAcceptablePwd(String password) {
        Set<String> pwdSet = new HashSet<>();
        for (int i = 0; i < password.length(); ++i) {
            int index = password.charAt(i);
            //数字
            if (index >= 48 && index <= 57) {
                pwdSet.add("number");
            }
            //大写字母
            else if (index >= 65 && index <= 90) {
                pwdSet.add("bigChar");
            }
            //小写字母
            else if (index >= 97 && index <= 122) {
                pwdSet.add("smallChar");
            }
            //特殊字符
            else {
                pwdSet.add("spcChar");
            }
        }
        if (pwdSet.size() < 4) {
            return false;
        } else {
            return true;
        }
    }
}
