package com.aidiary.user.event;

import com.aidiary.core.entity.UsersEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserLoginEvent {

    private final UsersEntity user;
    private final String ipAddress;
    private final String device;

}
