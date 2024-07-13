package com.aidiary.user.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NicknameValidator implements ConstraintValidator<NicknameValid, String> {

    private final static String NICKNAME_REGEX = "^([가-힣a-zA-Z0-9]*)$";

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {
        return Pattern.compile(NICKNAME_REGEX).matcher(nickname).matches();
    }
}
