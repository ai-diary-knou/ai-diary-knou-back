package com.aidiary.diary.service.processor;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.diary.model.DiaryResponseBundle.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmotionContentProcessor implements DiaryContentProcessor{

    @Override
    public void process(DiaryDetail.DiaryDetailBuilder builder,
                        DiariesEntity diariesEntity,
                        Map<DiaryWordType, List<DiaryWord>> wordsByType,
                        Map<DiarySentenceType, List<String>> sentencesByType
    ){
        builder.emotions(
                DiaryEmotions.builder()
                        .content(getFirstSentenceFromMapByType(sentencesByType, DiarySentenceType.EMOTION))
                        .words(wordsByType.get(DiaryWordType.EMOTION))
                        .build()
        );
    }

    private String getFirstSentenceFromMapByType(Map<DiarySentenceType, List<String>> sentencesByType, DiarySentenceType diarySentenceType) {
        List<String> sentences = sentencesByType.getOrDefault(diarySentenceType, new ArrayList<>());
        return sentences.isEmpty() ? null : sentences.get(0);
    }

}
