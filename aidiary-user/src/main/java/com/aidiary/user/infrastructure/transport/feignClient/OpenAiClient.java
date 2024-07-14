package com.aidiary.user.infrastructure.transport.feignClient;

import com.aidiary.user.infrastructure.transport.request.OpenAiRequestBundle.OpenAiAnalysisReq;
import com.aidiary.user.infrastructure.transport.response.OpenAiResponseBundle.OpenAiAnalysisRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "openAI", url = "https://api.openai.com")
public interface OpenAiClient {

    @PostMapping("/v1/chat/completions")
    OpenAiAnalysisRes postToChatCompletions(@RequestHeader("Authorization") String apiKey, @RequestBody OpenAiAnalysisReq openAiAnalysisReq);

}
