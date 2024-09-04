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

import java.security.InvalidParameterException;
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
                userDatabaseWriteService.increaseLoginAttemptCntAndLockIfApproachMaxAttempt(context.getUser());
                throw new InvalidParameterException();
            }
        } catch (InvalidParameterException e) {
            log.info("비밀번호가 불일치 :: ", e);
            throw new UserException(getLoginFailMessage(context.getUser().getLoginAttemptCnt()), ErrorCode.USER_LOGIN_FAIL);
        } catch (NoSuchAlgorithmException e) {
            log.info("SHA 알고리즘 해싱에서 문제 발생 :: ", e);
            throw new UserException(getLoginFailMessage(context.getUser().getLoginAttemptCnt()), ErrorCode.USER_LOGIN_FAIL);
        }
    }

    private String getLoginFailMessage(Integer loginAttemptCnt) {
        return ErrorCode.USER_LOGIN_FAIL.getMessage().replace("{loginAttemptCnt}", String.valueOf(loginAttemptCnt));
    }

}
