package com.aidiary.user.service;

import com.aidiary.auth.service.JwtTokenProvider;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.core.service.UserDatabaseWriteService;
import com.aidiary.user.model.UserRequestBundle.UserLoginRequest;
import com.aidiary.user.service.command.UserCommandContext;
import com.aidiary.user.service.command.UserCommandGroup;
import com.aidiary.user.service.command.user.UserAccessTokenCreateCommand;
import com.aidiary.user.service.command.user.UserLoginAttemptResetCommand;
import com.aidiary.user.service.command.user.UserLoginHistoryEventPublishCommand;
import com.aidiary.user.service.command.user.UserSearchByEmailCommand;
import com.aidiary.user.service.command.validation.ValidateLoginPasswordMatchCommand;
import com.aidiary.user.service.command.validation.ValidateUserSignedOutOrLockedCommand;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginService {

    private final UserDatabaseReadService userDatabaseReadService;
    private final UserDatabaseWriteService userDatabaseWriteService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public String login(UserLoginRequest request, HttpServletRequest httpServletRequest) {

        UserCommandGroup userCommandGroup = new UserCommandGroup();
        userCommandGroup.add(new UserSearchByEmailCommand(userDatabaseReadService));
        userCommandGroup.add(new ValidateUserSignedOutOrLockedCommand());
        userCommandGroup.add(new ValidateLoginPasswordMatchCommand(userDatabaseWriteService));
        userCommandGroup.add(new UserAccessTokenCreateCommand(jwtTokenProvider));
        userCommandGroup.add(new UserLoginAttemptResetCommand(userDatabaseWriteService));
        userCommandGroup.add(new UserLoginHistoryEventPublishCommand(applicationEventPublisher));

        UserCommandContext userCommandContext = UserCommandContext.builder()
                .email(request.email())
                .comparingPasswordPlain(request.password())
                .ipAddress(getClientIp(httpServletRequest))
                .device(httpServletRequest.getHeader("User-Agent"))
                .build();

        userCommandGroup.execute(userCommandContext);

        return userCommandContext.getAccessToken();
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
