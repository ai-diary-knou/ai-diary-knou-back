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
                "%s%s%s%s%s%s%s%s%s",
                "JSON 응답으로 diary에 대한 properties, summaries를 한국어로 작성하여 보낸다. 줄글은 우유체 사용. " ,
                "properties에는 emotions, self_thoghts, core_values, recommended_actions, additionals 포함. " ,
                "summaries에는 literary_summary을 포함. emotions, self_thoughts, core_values에는 순서대로 diary에서 볼 수 있는 " ,
                "감정상태, 자기이해, 핵심가치를 나타내는 문장을 content에, 단어 목록을 words에 담아 전달한다. " ,
                "모든 words에는 text와 scale 정보가 있으며, 단어 정보를 text에, 긍정 및 부정 정도를 나타내는 0-10 수치를 scale에 담는다. ",
                "recommended_actions에는 diary를 통해 추천할 수 있는 행동들을 리스트로 구성. " ,
                "additionals에는 diary에 대해 분석할 수 있는 추가적인 심리학적 분석 내용을 리스트로 구성. " ,
                "literary_summary에는 properties의 정보를 토대로 diary의 상황을 표현할 수 있는 유명한 책의 한 줄 문장 내지는 " ,
                "만들어낸 문학적인 한 줄 문장을 담음."
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
