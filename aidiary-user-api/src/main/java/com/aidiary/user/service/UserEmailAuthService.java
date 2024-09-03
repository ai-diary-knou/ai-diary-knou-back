package com.aidiary.user.service;

import com.aidiary.common.enums.EmailSendType;
import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.utils.RandomCodeGenerator;
import com.aidiary.core.entity.UserEmailAuthsEntity;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.infrastructure.transport.GoogleMailSender;
import com.aidiary.user.model.UserRequestBundle.*;
import com.aidiary.user.service.command.*;
import com.aidiary.user.service.command.validation.ValidateEmailAuthCodeExpiredCommand;
import com.aidiary.user.service.command.validation.ValidateEmailAuthCodeMatchCommand;
import com.aidiary.user.service.command.validation.ValidateEmailExistCommand;
import com.aidiary.user.service.command.validation.ValidateEmailNotExistCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEmailAuthService {

    private final UserDatabaseReadService userDatabaseReadService;
    private final UserDatabaseWriteService userDatabaseWriteService;
    private final GoogleMailSender googleMailSender;

    public void createRandomCodeAndSendEmail(UserEmailAuthCodeSentRequest request) {

        switch (EmailSendType.of(request.type().toUpperCase(Locale.ROOT))) {
            case REGISTER -> sendRegisterAuthCodeEmail(request);
            case PASSWORD_MODIFICATION -> sendPasswordModificationAuthCodeEmail(request);
        }

    }

    private void sendRegisterAuthCodeEmail(UserEmailAuthCodeSentRequest request) {

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new ValidateEmailExistCommand(userDatabaseReadService));
        userCommandGroup.add(new UserEmailAuthCreateOrUpdateCommand(userDatabaseReadService, userDatabaseWriteService));
        userCommandGroup.add(new EmailAuthSendCommand(googleMailSender));

        String randomCode = RandomCodeGenerator.getInstance().createAlphanumericCodeWithSpecialKeys();
        UserCommandContext userCommandContext = UserCommandContext.builder()
                .emailSendType(EmailSendType.valueOf(request.type().toUpperCase(Locale.ROOT)))
                .email(request.email())
                .code(randomCode)
                .build();

        userCommandGroup.execute(userCommandContext);

    }

    private void sendPasswordModificationAuthCodeEmail(UserEmailAuthCodeSentRequest request) {

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new ValidateEmailNotExistCommand(userDatabaseReadService));
        userCommandGroup.add(new UserEmailAuthCreateOrUpdateCommand(userDatabaseReadService, userDatabaseWriteService));
        userCommandGroup.add(new EmailAuthSendCommand(googleMailSender));

        String randomCode = RandomCodeGenerator.getInstance().createAlphanumericCodeWithSpecialKeys();
        UserCommandContext userCommandContext = UserCommandContext.builder()
                .emailSendType(EmailSendType.valueOf(request.type().toUpperCase(Locale.ROOT)))
                .email(request.email())
                .code(randomCode)
                .build();

        userCommandGroup.execute(userCommandContext);

    }

    public void confirmEmailAuthCode(UserEmailAndAuthCode request) {

        UserEmailAuthsEntity userEmailAuthsEntity = userDatabaseReadService.findUserEmailAuthByEmail(request.email())
                .orElseThrow(() -> new UserException(ErrorCode.EMAIL_AUTH_FAIL));

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new ValidateEmailAuthCodeMatchCommand());
        userCommandGroup.add(new ValidateEmailAuthCodeExpiredCommand());
        userCommandGroup.add(new ConfirmEmailAuthCodeCommand(userDatabaseWriteService));

        UserCommandContext userCommandContext = UserCommandContext.builder()
                .userEmailAuth(userEmailAuthsEntity)
                .email(request.email())
                .code(request.code())
                .build();

        userCommandGroup.execute(userCommandContext);

    }

}
