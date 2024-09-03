package com.aidiary.user.model;

import com.aidiary.common.validator.EmailValid;
import com.aidiary.common.validator.NicknameValid;
import com.aidiary.common.validator.PasswordValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public abstract class UserRequestBundle {

    public record DuplicateUserValidateRequest(@NotNull String type, @NotBlank String value) {}

    public record UserEmailAuthCodeSentRequest(@NotBlank String type, @NotBlank  @EmailValid String email) {}

    public record UserLoginRequest(@NotBlank @EmailValid String email, @NotBlank @PasswordValid String password){}

    public record UserRegisterRequest(@NotBlank @EmailValid String email, @NotBlank @NicknameValid String nickname, @NotBlank @PasswordValid String password, @NotBlank @PasswordValid String rePassword){}

    public record UserEmailAndAuthCode(@NotBlank @EmailValid String email, @NotBlank String code) {}

    public record UserPasswordUpdateRequest(@NotBlank String email, @NotBlank String code, @NotBlank @PasswordValid String password, @NotBlank @PasswordValid String rePassword){}

    public record UserNicknameUpdateRequest(@NotBlank @NicknameValid String nickname){}

}
