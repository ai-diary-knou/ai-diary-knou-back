package com.aidiary.diary.service.processor;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.diary.model.DiaryResponseBundle.DiaryDetail;
import com.aidiary.diary.model.DiaryResponseBundle.DiaryWord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LiterarySummaryContentProcessor implements DiaryContentProcessor{

    @Override
    public void process(DiaryDetail.DiaryDetailBuilder builder,
                        DiariesEntity diariesEntity,
                        Map<DiaryWordType, List<DiaryWord>> wordsByType,
                        Map<DiarySentenceType, List<String>> sentencesByType
    ){
        builder.literarySummary(
                getFirstSentenceFromMapByType(sentencesByType, DiarySentenceType.LITERARY_SUMMARY)
        );
    }

    public String getFirstSentenceFromMapByType(Map<DiarySentenceType, List<String>> sentencesByType, DiarySentenceType diarySentenceType) {
        List<String> sentences = sentencesByType.getOrDefault(diarySentenceType, new ArrayList<>());
        return sentences.isEmpty() ? null : sentences.get(0);
    }

}
