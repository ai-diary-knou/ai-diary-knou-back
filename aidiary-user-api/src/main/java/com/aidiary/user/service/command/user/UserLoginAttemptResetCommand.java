package com.aidiary.user.service.command.user;

import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLoginAttemptResetCommand implements UserCommand {

    private final UserDatabaseWriteService userDatabaseWriteService;

    @Override
    public void execute(UserCommandContext context) {

        userDatabaseWriteService.resetUserLoginAttemptCnt(context.getUser());

    }

}
