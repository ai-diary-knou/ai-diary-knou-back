package com.aidiary.user.service.command.user;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSearchByIdCommand implements UserCommand {

    private final UserDatabaseReadService userDatabaseReadService;

    @Override
    public void execute(UserCommandContext context) {
        UsersEntity user = userDatabaseReadService.findUserById(context.getUserId())
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_EXIST));
        context.setUser(user);
    }
}
