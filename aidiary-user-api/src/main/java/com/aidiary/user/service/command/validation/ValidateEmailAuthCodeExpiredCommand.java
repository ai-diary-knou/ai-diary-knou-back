package com.aidiary.user.service.command.validation;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateEmailAuthCodeExpiredCommand implements UserCommand {

    @Override
    public void execute(UserCommandContext context) {

        LocalDateTime expireTime = context.getUserEmailAuth().getCreatedAt().plusMinutes(5);
        context.setCurrentTime(LocalDateTime.now());
        if (expireTime.isBefore(context.getCurrentTime())) {
            log.info("인증 코드가 만료되었습니다.");
            throw new UserException(ErrorCode.AUTH_CODE_EXPIRED);
        }

    }
}
