package com.imho.authguard.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MaskingUtils {

    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "hidden";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) return "hidden";
        return email.charAt(0) + "***" + email.substring(atIndex - 1);
    }

}

