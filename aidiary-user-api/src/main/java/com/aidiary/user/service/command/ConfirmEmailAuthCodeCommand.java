package com.aidiary.user.service.command;

import com.aidiary.core.service.UserDatabaseWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfirmEmailAuthCodeCommand implements UserCommand{

    private final UserDatabaseWriteService userDatabaseWriteService;

    @Override
    public void execute(UserCommandContext context) {

        context.getUserEmailAuth().updateConfirmedAt(LocalDateTime.now());
        userDatabaseWriteService.save(context.getUserEmailAuth());

    }
}
