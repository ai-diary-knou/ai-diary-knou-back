package com.aidiary.user.service.command.emailAuth;

import com.aidiary.infrastructure.transport.GoogleMailSender;
import com.aidiary.user.service.command.UserCommand;
import com.aidiary.user.service.command.UserCommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.aidiary.common.enums.EmailSendType.REGISTER;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailAuthCodeSendCommand implements UserCommand {

    private final GoogleMailSender googleMailSender;

    @Override
    public void execute(UserCommandContext context) {

        String purpose = REGISTER.equals(context.getEmailSendType()) ? "회원가입" : "비밀번호 변경";
        String title = String.format("[AiDiary] 인증 코드를 확인하고 %s을 완료하세요.", purpose);
        String description = "아래의 인증 코드를 사용하여 이메일 인증을 완료해 주세요.";
        String content = googleMailSender.generateEmailVerificationTemplate(title, description, context.getCode());

        googleMailSender.sendMail(context.getEmail(), title, content, true);

    }

}
