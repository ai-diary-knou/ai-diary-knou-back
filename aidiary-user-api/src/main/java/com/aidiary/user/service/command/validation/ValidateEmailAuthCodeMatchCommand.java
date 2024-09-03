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
public class ValidateEmailAuthCodeMatchCommand implements UserCommand {

    @Override
    public void execute(UserCommandContext context) {

        if (!context.getUserEmailAuth().getCode().equals(context.getCode())) {
            log.info("인증 코드가 불일치 합니다.");
            throw new UserException(ErrorCode.EMAIL_AUTH_FAIL);
        }

    }
}
