package com.aidiary.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<PasswordValid, String> {

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return Pattern.compile(PASSWORD_REGEX).matcher(password).matches();
    }

}
