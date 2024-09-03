package com.aidiary.user.service.command.validation;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.utils.SHA256Util;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

import static com.aidiary.common.enums.UserStatus.BLOCKED;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateLoginPasswordMatchCommand implements UserCommand {

    private final UserDatabaseWriteService userDatabaseWriteService;

    @Override
    public void execute(UserCommandContext context) {
        try {
            if (!context.getUser().getPassword().equals(SHA256Util.getHashString(context.getComparingPasswordPlain()))) {

                context.getUser().updateLoginAttemptCnt(context.getUser().getLoginAttemptCnt() + 1);
                if (context.getUser().getLoginAttemptCnt() == 5) {
                    context.getUser().updateStatus(BLOCKED);
                }
                userDatabaseWriteService.save(context.getUser());
                throw new UserException(getLoginFailMessage(context.getUser().getLoginAttemptCnt()), ErrorCode.USER_LOGIN_FAIL);
            }
        } catch (NoSuchAlgorithmException e) {
            log.info("ValidatePasswordHashMatchCommand.execute :: ", e);
            throw new UserException(getLoginFailMessage(context.getUser().getLoginAttemptCnt()), ErrorCode.USER_LOGIN_FAIL);
        }
    }

    private String getLoginFailMessage(Integer loginAttemptCnt) {
        return ErrorCode.USER_LOGIN_FAIL.getMessage().replace("{loginAttemptCnt}", String.valueOf(loginAttemptCnt));
    }

}
