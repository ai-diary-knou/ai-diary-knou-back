package com.aidiary.user.service.command.validation;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateUserSignedOutOrLockedCommand implements UserCommand {

    @Override
    public void execute(UserCommandContext context) {

        validateIfUserSignedOut(context.getUser());
        validateIfUserLocked(context.getUser());

    }

    private void validateIfUserSignedOut(UsersEntity usersEntity) {
        if (!usersEntity.isAccountNonExpired()) {
            throw new UserException(ErrorCode.USER_ALREADY_SIGNED_OUT);
        }
    }

    private void validateIfUserLocked(UsersEntity usersEntity) {
        if (!usersEntity.isAccountNonLocked() || usersEntity.getLoginAttemptCnt() >= 5){
            throw new UserException(ErrorCode.USER_LOGIN_LOCKED);
        }
    }

}
