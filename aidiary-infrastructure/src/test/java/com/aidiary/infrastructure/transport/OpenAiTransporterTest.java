package com.aidiary.infrastructure.transport;

import com.aidiary.infrastructure.transport.response.OpenAiResponseBundle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@SpringBootTest
class OpenAiTransporterTest {

    @Autowired
    private OpenAiTransporter openAiTransporter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void GPT_TURBO3_TEST() throws JsonProcessingException {

        // given
        String content = "오늘은 또 다른 여름의 하루. 더운 습기가 방 안을 가득 채운다. 이런 더운 날 커다란 빙하 얼음을 방 안에 가져다두고 싶다. " +
                "그러면 이 더위가 조금은 가실까. 그런 생각을 하게 되면 얼마 전 여행지가 떠오르고 다시 여행을 떠나고 싶어진다." +
                "여행은 많은 것을 바꾼다. 갈 땐 정신 없지만 막상 돌아오면 내가 생각보다 이것저것 배웠다는 걸 알게 된다." +
                "다른 나라 사람들의 가치관과 태도를 보고 겪는 것만으로도 나는 겸손함과 용기를 배울 수 있었다." +
                "최근엔 미루기만 했던 책을 사서 읽기 시작했다. 조금 못하더라도 다시 영어로 책을 읽어봐야지." +
                "언제 갈진 모르겠지만 다시 여행을 훌쩍 떠나고 싶다. 이번엔 어디로 갈까.";

        // when
        OpenAiResponseBundle.OpenAiContent openAiContent = openAiTransporter.getAnalysisContentFromTurbo3Point5(content);

        // then
        String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(openAiContent);
        System.out.println("JSON Result: " + jsonResult);
        assertNotNull(openAiContent);

//        JSON Result: {
//            "properties" : {
//                "emotions" : {
//                    "content" : "더운 날의 답답함에서 벗어나 여행에 대한 갈망을 느낌",
//                            "words" : [ {
//                        "text" : "답답함",
//                                "scale" : 6
//                    }, {
//                        "text" : "갈망",
//                                "scale" : 8
//                    } ]
//                },
//                "self_thoughts" : {
//                    "content" : "여행을 통해 겸손함과 용기를 배운 경험에 대해 회상",
//                            "words" : [ {
//                        "text" : "겸손함",
//                                "scale" : 9
//                    }, {
//                        "text" : "용기",
//                                "scale" : 8
//                    } ]
//                },
//                "core_values" : {
//                    "content" : "다른 문화를 경험함으로써 새로운 가치관을 습득함",
//                            "words" : [ {
//                        "text" : "다양성",
//                                "scale" : 9
//                    }, {
//                        "text" : "배움",
//                                "scale" : 7
//                    } ]
//                },
//                "recommended_actions" : [ "다음 여행지를 계획하고 이를 위해 저축 계획을 세워보기", "국내 어딘가 가볼만한 곳을 미리 알아보고 일정 잡기", "읽기 취향에 맞는 책을 선정하고 다양한 지식을 습득하기" ],
//                "additionals" : [ "여행은 새로운 환경에서의 자기위치를 재조명하고 성장할 수 있는 기회를 제공함", "책을 읽는 것은 자아 개발에 큰 도움을 줄 수 있으며, 외국어 독해 능력 향상에도 도움이 됨" ]
//            },
//            "summaries" : {
//                "literary_summary" : "무더운 여름의 답답함을 지속하는 중, 갈망하는 마음은 마치 톨킨의 '반지의 제왕' 속 무한한 여정으로 이어지는 것 같다."
//            }
//        }

    }

}