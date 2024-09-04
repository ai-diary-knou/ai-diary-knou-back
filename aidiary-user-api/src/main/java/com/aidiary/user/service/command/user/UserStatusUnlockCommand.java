package com.aidiary.user.service.command.user;

import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.aidiary.common.enums.UserStatus.ACTIVE;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserStatusUnlockCommand implements UserCommand {

    private final UserDatabaseWriteService userDatabaseWriteService;

    @Override
    public void execute(UserCommandContext context) {

        if (!context.getUser().isAccountNonLocked()){
            context.getUser().updateStatus(ACTIVE);
            userDatabaseWriteService.save(context.getUser());
        }

    }

}
