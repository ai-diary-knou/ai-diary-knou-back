package com.aidiary.user.service.command.user;

import com.aidiary.auth.service.JwtTokenProvider;
import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAccessTokenCreateCommand implements UserCommand {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void execute(UserCommandContext context) {

        try {
            context.setAccessToken(jwtTokenProvider.createToken(Objects.requireNonNull(context.getUser())));
        } catch (Exception e) {
            log.info("User Access Token Create Error :: ", e);
            throw new UserException(ErrorCode.USER_TOKEN_ERROR);
        }

    }
}
