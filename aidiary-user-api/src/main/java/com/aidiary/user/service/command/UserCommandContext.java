package com.aidiary.user.service.command;

import com.aidiary.common.enums.DuplicateUserValidateType;
import com.aidiary.common.enums.EmailSendType;
import com.aidiary.core.entity.UserEmailAuthsEntity;
import com.aidiary.core.entity.UsersEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCommandContext {
    private UsersEntity user;
    private UserEmailAuthsEntity userEmailAuth;
    private String email;
    private String nickname;
    private EmailSendType emailSendType;
    private DuplicateUserValidateType duplicateUserValidateType;
    private String code;
    private String password;
    private String rePassword;
    private String comparingPasswordPlain;
    private String ipAddress;
    private String device;
    private LocalDateTime currentTime;
}
