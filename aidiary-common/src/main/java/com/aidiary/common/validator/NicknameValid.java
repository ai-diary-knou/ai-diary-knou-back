package com.aidiary.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NicknameValidator.class)
public @interface NicknameValid {

    String message() default "Invalid Parameter. Nickname can only be ^([가-힣a-zA-Z0-9]*)$";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
