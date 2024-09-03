package com.aidiary.user.service.command;

import com.aidiary.core.service.UserDatabaseWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.aidiary.common.enums.UserStatus.ACTIVE;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnlockUserCommand implements UserCommand {

    private final UserDatabaseWriteService userDatabaseWriteService;

    @Override
    public void execute(UserCommandContext context) {

        if (!context.getUser().isAccountNonLocked()){
            context.getUser().updateStatus(ACTIVE);
            userDatabaseWriteService.save(context.getUser());
        }

    }

}
