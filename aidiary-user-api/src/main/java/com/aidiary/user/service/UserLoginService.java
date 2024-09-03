package com.aidiary.user.service;

import com.aidiary.auth.service.JwtTokenProvider;
import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.event.UserLoginEvent;
import com.aidiary.user.model.UserRequestBundle.*;
import com.aidiary.user.service.command.*;
import com.aidiary.user.service.command.validation.ValidateLoginPasswordMatchCommand;
import com.aidiary.user.service.command.validation.ValidateUserSignedOutOrLockedCommand;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginService {

    private final UserDatabaseReadService userDatabaseReadService;
    private final UserDatabaseWriteService userDatabaseWriteService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher applicationEventPublisher;

    public String login(UserLoginRequest request, HttpServletRequest httpServletRequest) throws Exception {

        UsersEntity user = userDatabaseReadService.findUserByEmail(request.email())
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_EXIST));

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new ValidateUserSignedOutOrLockedCommand());
        userCommandGroup.add(new ValidateLoginPasswordMatchCommand(userDatabaseWriteService));
        userCommandGroup.add(new ResetLoginAttemptCommand(userDatabaseWriteService));

        UserCommandContext userCommandContext = UserCommandContext.builder()
                .user(user)
                .email(request.email())
                .comparingPasswordPlain(request.password())
                .build();

        userCommandGroup.execute(userCommandContext);

        String token = jwtTokenProvider.createToken(user);

        String ipAddress = getClientIp(httpServletRequest);
        String device = httpServletRequest.getHeader("User-Agent");
        applicationEventPublisher.publishEvent(new UserLoginEvent(user, ipAddress, device));

        return token;
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

}
