package com.aidiary.user.infrastructure.transport;

import com.aidiary.user.infrastructure.transport.response.OpenAiResponseBundle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class OpenAiTransporterTest {

    @Test
    public void contentParsingTest() throws JsonProcessingException {

        String content = "{\n  \"properties\": {\n    \"emotions\": {\n      \"content\": \"평온함, 만족감\",\n      \"words\": [\n        {\"text\": \"평온함\", \"scale\": 8},\n        {\"text\": \"만족감\", \"scale\": 9}\n      ]\n    },\n    \"self_thoughts\": {\n      \"content\": \"소중한 사람들과 함께 보낸 소중한 시간\",\n      \"words\": [\n        {\"text\": \"소중한\", \"scale\": 9},\n        {\"text\": \"시간\", \"scale\": 8}\n      ]\n    },\n    \"core_values\": {\n      \"content\": \"가족과 친구, 창의성과 즐거움\",\n      \"words\": [\n        {\"text\": \"가족\", \"scale\": 9},\n        {\"text\": \"친구\", \"scale\": 8},\n        {\"text\": \"창의성\", \"scale\": 7},\n        {\"text\": \"즐거움\", \"scale\": 8}\n      ]\n    },\n    \"recommended_actions\": [\n      \"가족이나 친구들과 소중한 시간을 보내보세요\",\n      \"새로운 취미나 활동을 시작해보는 것은 어떨까요?\",\n      \"매일 하루를 마무리할 때 감사하고 만족했던 순간을 되새겨보세요\"\n    ],\n    \"additionals\": [\n      \"가족과의 소중한 연결이 일상적으로 이루어지면 삶의 만족도가 상승할 수 있습니다.\",\n      \"창의적인 활동을 통해 마음의 안정을 유지하고 즐거움을 느낄 수 있습니다.\"\n    ]\n  },\n  \"summaries\": {\n    \"literary_summary\": \"하늘이 맑고 마음이 맑아지는 그날, 소중한 이들과의 소중한 시간을 보내며 창의성과 즐거움을 느꼈다.\"\n  }\n}";

        System.out.println(content);
        ObjectMapper objectMapper = new ObjectMapper();
        OpenAiResponseBundle.OpenAiContent openAiContent = objectMapper.readValue(content, OpenAiResponseBundle.OpenAiContent.class);

        System.out.println(objectMapper.writeValueAsString(openAiContent));;

    }

}