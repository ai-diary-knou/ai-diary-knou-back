package com.aidiary.user.service.command;

import com.aidiary.core.service.UserDatabaseWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResetLoginAttemptCommand implements UserCommand{

    private final UserDatabaseWriteService userDatabaseWriteService;

    @Override
    public void execute(UserCommandContext context) {

        context.getUser().updateLoginAttemptCnt(0);
        userDatabaseWriteService.save(context.getUser());

    }

}
