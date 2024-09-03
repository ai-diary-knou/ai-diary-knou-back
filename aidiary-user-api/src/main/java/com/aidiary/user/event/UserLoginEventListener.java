package com.aidiary.user.event;

import com.aidiary.core.service.UserDatabaseWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLoginEventListener {

    private final UserDatabaseWriteService userDatabaseWriteService;

    @Async
    @EventListener
    public void handleUserLoginEvent(UserLoginEvent event) {

        try {

            userDatabaseWriteService.createOrUpdateUserLoginHistory(event.getUser(), event.getIpAddress(), event.getDevice(), LocalDateTime.now());

        } catch (Exception e) {
            log.info("User Login History Save Error :: ", e);
        }

    }

}
