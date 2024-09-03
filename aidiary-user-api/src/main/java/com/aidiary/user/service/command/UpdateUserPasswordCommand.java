package com.aidiary.user.service.command;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.utils.SHA256Util;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.UserDatabaseWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserPasswordCommand implements UserCommand {

    private final UserDatabaseWriteService userDatabaseWriteService;

    @Override
    public void execute(UserCommandContext context) {

        try {
            UsersEntity usersEntity = context.getUser();
            usersEntity.updatePassword(SHA256Util.getHashString(context.getPassword()));
            userDatabaseWriteService.save(usersEntity);
        } catch (NoSuchAlgorithmException e) {
            log.info("User Password Update Fail");
            throw new UserException(ErrorCode.UNKNOWN_ERROR);
        }

    }

}
