package com.aidiary.user.service.event;

import com.aidiary.common.enums.EmailSendType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EmailAuthCreateOrUpdateEvent {

    private final EmailSendType emailSendType;
    private final String email;
    private final String code;

}
