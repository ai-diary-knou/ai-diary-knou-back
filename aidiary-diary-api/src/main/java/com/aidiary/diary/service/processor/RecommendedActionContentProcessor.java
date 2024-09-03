package com.aidiary.diary.service.processor;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.diary.model.DiaryResponseBundle.DiaryDetail;
import com.aidiary.diary.model.DiaryResponseBundle.DiaryWord;

import java.util.List;
import java.util.Map;

public class RecommendedActionContentProcessor implements DiaryContentProcessor{

    @Override
    public void process(DiaryDetail.DiaryDetailBuilder builder,
                        DiariesEntity diariesEntity,
                        Map<DiaryWordType, List<DiaryWord>> wordsByType,
                        Map<DiarySentenceType, List<String>> sentencesByType
    ){
        builder.recommendedActions(
                sentencesByType.get(DiarySentenceType.RECOMMENDED_ACTION)
        );
    }

}
