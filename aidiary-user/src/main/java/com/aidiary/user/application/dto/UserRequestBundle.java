package com.aidiary.user.application.dto;

import com.aidiary.user.domain.validator.EmailValid;
import com.aidiary.user.domain.validator.NicknameValid;
import com.aidiary.user.domain.validator.PasswordValid;
import jakarta.validation.constraints.NotBlank;

public abstract class UserRequestBundle {

    public record UserValidateDuplicateRequest(@NotBlank String type, @NotBlank String value) {}

    public record UserEmailAuthCodeSentRequest(@NotBlank String type, @NotBlank  @EmailValid String email) {}

    public record UserRegisterRequest(@NotBlank @EmailValid String email, @NotBlank @NicknameValid String nickname, @NotBlank @PasswordValid String password, @NotBlank @PasswordValid String rePassword){}

    public record UserLoginRequest(@NotBlank @EmailValid String email, @NotBlank @PasswordValid String password){}

    public record UserEmailAndAuthCode(@NotBlank @EmailValid String email, @NotBlank String code) {}

    public record UserPasswordUpdateRequest(@NotBlank String email, @NotBlank String code, @NotBlank @PasswordValid String password, @NotBlank @PasswordValid String rePassword){}

    public record UserNicknameUpdateRequest(@NotBlank @NicknameValid String nickname){}

}
