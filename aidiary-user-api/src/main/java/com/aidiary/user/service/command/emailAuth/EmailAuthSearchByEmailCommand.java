package com.aidiary.user.service.command.emailAuth;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.core.entity.UserEmailAuthsEntity;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailAuthSearchByEmailCommand implements UserCommand {

    private final UserDatabaseReadService userDatabaseReadService;

    @Override
    public void execute(UserCommandContext context) {
        UserEmailAuthsEntity userEmailAuth = userDatabaseReadService.findUserEmailAuthByEmail(context.getEmail())
                .orElseThrow(() -> new UserException(ErrorCode.EMAIL_NOT_AUTHORIZED));
        context.setUserEmailAuth(userEmailAuth);
    }
}
