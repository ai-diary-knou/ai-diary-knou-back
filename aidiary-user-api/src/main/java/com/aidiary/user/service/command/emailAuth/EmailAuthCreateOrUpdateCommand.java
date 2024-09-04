package com.aidiary.user.service.command.emailAuth;

import com.aidiary.core.entity.UserEmailAuthsEntity;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailAuthCreateOrUpdateCommand implements UserCommand {

    private final UserDatabaseReadService userDatabaseReadService;
    private final UserDatabaseWriteService userDatabaseWriteService;

    @Override
    public void execute(UserCommandContext context) {

        Optional<UserEmailAuthsEntity> optionalUserEmailAuths = userDatabaseReadService.findUserEmailAuthByEmail(context.getEmail());

        if (optionalUserEmailAuths.isPresent()) {
            userDatabaseWriteService.updateUserEmailAuthCodeAndResetConfirmedDate(
                    optionalUserEmailAuths.get(), context.getCode()
            );
        } else {
            userDatabaseWriteService.save(
                    UserEmailAuthsEntity.builder()
                            .email(context.getEmail())
                            .code(context.getCode())
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        }

    }

}
