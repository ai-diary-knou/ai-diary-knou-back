package com.aidiary.user.service.command.validation;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateEmailNotExistCommand implements UserCommand {

    private final UserDatabaseReadService userDatabaseReadService;

    @Override
    public void execute(UserCommandContext context) {
        if (!userDatabaseReadService.isUserExistsByEmail(context.getEmail())) {
            throw new UserException(ErrorCode.USER_NOT_EXIST);
        }
    }
}
