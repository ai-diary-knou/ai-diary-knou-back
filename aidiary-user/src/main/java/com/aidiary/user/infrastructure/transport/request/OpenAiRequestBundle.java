package com.aidiary.user.infrastructure.transport.request;

import lombok.Builder;

import java.util.List;


public class OpenAiRequestBundle {

    @Builder
    public record OpenAiAnalysisReq(String model, List<OpenAiMessage> messages){}

    @Builder
    public record OpenAiMessage(String role, String content){}

}
