package com.aidiary.common.enums;

public enum EmailSendType {

    REGISTER,
    PASSWORD_MODIFICATION;

    public static EmailSendType of(String name){
        if ("password-modification".equals(name)) {
            return PASSWORD_MODIFICATION;
        }
        return EmailSendType.valueOf(name);
    }

}
