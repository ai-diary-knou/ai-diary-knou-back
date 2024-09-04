package com.aidiary.user.service.command.user;

import com.aidiary.user.service.event.UserLoginEvent;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLoginHistoryEventPublishCommand implements UserCommand {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void execute(UserCommandContext context) {

        applicationEventPublisher.publishEvent(new UserLoginEvent(context.getUser(), context.getIpAddress(), context.getDevice()));

    }
}
