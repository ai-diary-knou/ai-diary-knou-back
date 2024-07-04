package com.aidiary.user.application.dto;

public abstract class UserRequestBundle {

    public record UserEmailDuplicateValidateRequest(String email) {}

    public record UserEmailAuthCodeSentRequest(String email) {}

    public record UserNicknameDuplicateValidateRequest(String nickname) {}

    public record UserEmailAndAuthCode(String email, int code) {}
}
