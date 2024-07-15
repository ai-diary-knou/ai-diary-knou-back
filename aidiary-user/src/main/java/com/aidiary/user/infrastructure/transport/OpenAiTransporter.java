package com.aidiary.user.infrastructure.transport;

import com.aidiary.user.domain.entity.DiariesEntity;
import com.aidiary.user.infrastructure.transport.feignClient.OpenAiClient;
import com.aidiary.user.infrastructure.transport.request.OpenAiRequestBundle.OpenAiAnalysisReq;
import com.aidiary.user.infrastructure.transport.request.OpenAiRequestBundle.OpenAiMessage;
import com.aidiary.user.infrastructure.transport.response.OpenAiResponseBundle;
import com.aidiary.user.infrastructure.transport.response.OpenAiResponseBundle.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAiTransporter {

    @Value("${openapi.openai.key}")
    private String openAiKey;

    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    public OpenAiContent getAnalysisContentFromTurbo3Point5(String userContent) throws JsonProcessingException {

        OpenAiAnalysisRes openAiResponse = getAnalysisFromTurbo3Point5(userContent);
        String jsonContent = openAiResponse.choices().get(0).message().content();
        return parseOpenAiContent(jsonContent);

    }

    @Transactional(rollbackFor = Exception.class)
    public OpenAiAnalysisRes getAnalysisFromTurbo3Point5(String userContent) throws JsonProcessingException {

        String authorization = "Bearer " + openAiKey;

        String command = String.format(
                "%s %s %s %s %s %s %s %s %s %s %s",
                "In the JSON response, properties and summaries for the diary are written in Korean.",
                "For writing, use milk font. Properties include emotions, self_thoughts, core_values,",
                "recommended_actions, and additionals. The summaries include literary_summary.",
                "For emotions, self_thoughts, and core_values, sentences representing emotional states, self-understanding,",
                "and core values that can be found in the diary are sequentially delivered as one-line content,",
                "and a list of words as words. All words have text and scale information.",
                "Word information is contained in the text, and numbers from 0 to 10 indicating the degree of positivity and negativity are contained in the scale.",
                "recommended_actions consists of a list of actions that can be recommended through the diary.",
                "Additionals consists of a list of additional psychological analysis contents that can be analyzed for the diary.",
                "literary_summary contains one-line sentences from famous books or created literary one-line sentences",
                "that can express the diary situation based on properties information."
        );

        OpenAiAnalysisRes res = openAiClient.postToChatCompletions(authorization, OpenAiAnalysisReq.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(
                        OpenAiMessage.builder()
                                .role("system")
                                .content(command)
                                .build(),
                        OpenAiMessage.builder()
                                .role("user")
                                .content(userContent)
                                .build()
                ))
                .build()
        );

        log.info(objectMapper.writeValueAsString(res));

        return res;
    }

    public OpenAiContent parseOpenAiContent(String jsonContent) throws JsonProcessingException {

        return objectMapper.readValue(jsonContent, OpenAiResponseBundle.OpenAiContent.class);

    }

}
