package com.aidiary.user.service.command.emailAuth;

import com.aidiary.common.utils.RandomCodeGenerator;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailAuthCodeCreateCommand implements UserCommand {

    @Override
    public void execute(UserCommandContext context) {
        String randomCode = RandomCodeGenerator.getInstance().createAlphanumericCodeWithSpecialKeys();
        context.setCode(randomCode);
    }
}
