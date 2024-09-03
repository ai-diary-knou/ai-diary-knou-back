package com.aidiary.user.service.command;

import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.UserDatabaseWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateNicknameCommand implements UserCommand {

    private final UserDatabaseWriteService userDatabaseWriteService;

    @Override
    public void execute(UserCommandContext context) {

        UsersEntity usersEntity = context.getUser();
        usersEntity.updateNickname(context.getNickname());
        userDatabaseWriteService.save(usersEntity);

    }

}
