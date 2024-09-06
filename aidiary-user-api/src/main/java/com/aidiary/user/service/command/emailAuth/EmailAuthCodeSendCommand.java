package com.aidiary.user.service.command.emailAuth;

import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import com.aidiary.user.service.event.EmailAuthCreateOrUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailAuthCodeSendCommand implements UserCommand {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void execute(UserCommandContext context) {

        applicationEventPublisher.publishEvent(
                new EmailAuthCreateOrUpdateEvent(context.getEmailSendType(), context.getEmail(), context.getCode())
        );

    }

}
