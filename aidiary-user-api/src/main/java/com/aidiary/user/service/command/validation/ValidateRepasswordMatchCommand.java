package com.aidiary.user.service.command.validation;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateRepasswordMatchCommand implements UserCommand {

    @Override
    public void execute(UserCommandContext context) {
        if (!context.getPassword().equals(context.getRePassword())) {
            throw new UserException("Invalid Parameter. password and rePassword mismatch.", ErrorCode.INVALID_PARAMETER);
        }
    }
}
