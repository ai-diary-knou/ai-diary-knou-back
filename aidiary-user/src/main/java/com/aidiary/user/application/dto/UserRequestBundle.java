package com.aidiary.user.application.dto;

public abstract class UserRequestBundle {

    public record UserValidateDuplicateRequest(String type, String value) {}

    public record UserEmailAuthCodeSentRequest(String email) {}

    public record UserEmailAuthCodeVerifyRequest(String code) {}

    public record UserRegisterRequest(String email, String nickname, String password, String rePassword){}

    public record UserLoginRequest(String email, String password){}

    public record UserEmailAndAuthCode(String email, int code) {}

    public record UserPasswordUpdateRequest(String originalPassword, String newPassword){}

}
