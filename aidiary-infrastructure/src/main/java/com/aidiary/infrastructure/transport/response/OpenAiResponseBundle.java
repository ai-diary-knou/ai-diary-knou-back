package com.aidiary.infrastructure.transport.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

public class OpenAiResponseBundle {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record OpenAiAnalysisRes(
            String id,
            String object,
            Long created, // 날짜 포맷 아님
            String model,
            List<OpenAiChoice> choices,
            String systemFingerprint
    ){}

    public record OpenAiChoice(
            Long index,
            OpenAiMessage message,
            String logprobs,
            String finishReason
    ){}

    public record OpenAiMessage(String role, String content){}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record OpenAiUsage(Long promptToken, Long completionTokens, Long totalTokens){}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record OpenAiContent(
            OpenAiProperties properties,
            OpenAiSummaries summaries
    ){}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record OpenAiProperties(
            OpenAiEmotions emotions,
            OpenAiSelfThoughts selfThoughts,
            OpenAiCoreValues coreValues,
            List<String> recommendedActions,
            List<String> additionals
    ) {}

    public record OpenAiEmotions(
            String content,
            List<OpenAiWord> words
    ) {}

    public record OpenAiSelfThoughts(
            String content,
            List<OpenAiWord> words
    ) {}

    public record OpenAiCoreValues(
            String content,
            List<OpenAiWord> words
    ) {}

    public record OpenAiWord(
            String text,
            int scale
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record OpenAiSummaries(
            String literarySummary
    ) {}

}
