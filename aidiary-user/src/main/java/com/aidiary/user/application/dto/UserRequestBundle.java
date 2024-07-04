package com.aidiary.user.application.dto;

public abstract class UserRequestBundle {

    public record UserValidateDuplicateRequest(String type, String value) {}

    public record UserEmailDuplicateValidateRequest(String email) {}

    public record UsernameDuplicateValidateRequest(String username) {}

    public record UserEmailAuthCodeSentRequest(String email) {}

    public record UserEmailAuthCodeVerifyRequest(String code) {}

    public record UserRegisterRequest(String email, String nickname, String password, String rePassword){}

    public record UserEmailAndAuthCode(String email, int code) {}
}
