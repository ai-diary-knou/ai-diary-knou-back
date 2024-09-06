package com.aidiary.user.service;

import com.aidiary.common.enums.EmailSendType;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.model.UserRequestBundle.UserEmailAndAuthCode;
import com.aidiary.user.model.UserRequestBundle.UserEmailAuthCodeSentRequest;
import com.aidiary.user.service.command.UserCommandContext;
import com.aidiary.user.service.command.UserCommandGroup;
import com.aidiary.user.service.command.emailAuth.EmailAuthCodeCreateCommand;
import com.aidiary.user.service.command.emailAuth.EmailAuthCodeSendCommand;
import com.aidiary.user.service.command.emailAuth.EmailAuthConfirmCommand;
import com.aidiary.user.service.command.emailAuth.EmailAuthCreateOrUpdateCommand;
import com.aidiary.user.service.command.validation.ValidateEmailAuthCodeExpiredCommand;
import com.aidiary.user.service.command.validation.ValidateEmailAuthCodeMatchCommand;
import com.aidiary.user.service.command.validation.ValidateUserExistByEmailCommand;
import com.aidiary.user.service.command.validation.ValidateUserNotExistByEmailCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEmailAuthService {

    private final UserDatabaseReadService userDatabaseReadService;
    private final UserDatabaseWriteService userDatabaseWriteService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void createRandomCodeAndSendEmail(UserEmailAuthCodeSentRequest request) {

        switch (EmailSendType.of(request.type())) {
            case REGISTER -> sendRegisterAuthCodeEmail(request);
            case PASSWORD_MODIFICATION -> sendPasswordModificationAuthCodeEmail(request);
        }

    }

    private void sendRegisterAuthCodeEmail(UserEmailAuthCodeSentRequest request) {

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new ValidateUserExistByEmailCommand(userDatabaseReadService));
        userCommandGroup.add(new EmailAuthCodeCreateCommand());
        userCommandGroup.add(new EmailAuthCreateOrUpdateCommand(userDatabaseReadService, userDatabaseWriteService));
        userCommandGroup.add(new EmailAuthCodeSendCommand(applicationEventPublisher));

        UserCommandContext userCommandContext = UserCommandContext.builder()
                .emailSendType(EmailSendType.of(request.type()))
                .email(request.email())
                .build();

        userCommandGroup.execute(userCommandContext);

    }

    private void sendPasswordModificationAuthCodeEmail(UserEmailAuthCodeSentRequest request) {

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new ValidateUserNotExistByEmailCommand(userDatabaseReadService));
        userCommandGroup.add(new EmailAuthCodeCreateCommand());
        userCommandGroup.add(new EmailAuthCreateOrUpdateCommand(userDatabaseReadService, userDatabaseWriteService));
        userCommandGroup.add(new EmailAuthCodeSendCommand(applicationEventPublisher));

        UserCommandContext userCommandContext = UserCommandContext.builder()
                .emailSendType(EmailSendType.of(request.type()))
                .email(request.email())
                .build();

        userCommandGroup.execute(userCommandContext);

    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void confirmEmailAuthCode(UserEmailAndAuthCode request) {

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new ValidateEmailAuthCodeMatchCommand(userDatabaseReadService));
        userCommandGroup.add(new ValidateEmailAuthCodeExpiredCommand());
        userCommandGroup.add(new EmailAuthConfirmCommand(userDatabaseWriteService));

        UserCommandContext userCommandContext = UserCommandContext.builder()
                .email(request.email())
                .code(request.code())
                .build();

        userCommandGroup.execute(userCommandContext);

    }

}
