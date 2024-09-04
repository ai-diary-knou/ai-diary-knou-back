package com.aidiary.common.enums;

import java.util.Locale;

public enum EmailSendType {

    REGISTER,
    PASSWORD_MODIFICATION;

    public static EmailSendType of(String name){
        if ("PASSWORD-MODIFICATION".equals(name.toUpperCase(Locale.ROOT))) {
            return PASSWORD_MODIFICATION;
        }
        return EmailSendType.valueOf(name);
    }

}
