package com.aidiary.core.dto;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.common.vo.PagingRequest;
import com.aidiary.core.entity.UsersEntity;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public abstract class DiaryDatabaseRequestBundle {

    @Builder
    public record DiarySentencesOfTypeInMonthRequest(UsersEntity usersEntity, int year, int month, DiaryStatus diaryStatus, DiarySentenceType diarySentenceType){

    }

    @Builder
    public record DiarySentencesOfTypeWithinPageRequest(UsersEntity usersEntity, DiaryStatus diaryStatus, DiarySentenceType diarySentenceType, PagingRequest pagingRequest){

    }

    @Builder
    public record AverageWordsScaleOfTypeByUserWithinPageRequest(UsersEntity usersEntity, DiaryStatus diaryStatus, DiaryWordType diaryWordType, PagingRequest pagingRequest){}

    @Builder
    public record TopNRepetitiveDiaryWordsOfTypesBetweenDatesRequest(UsersEntity usersEntity, DiaryStatus diaryStatus, List<DiaryWordType> diaryWordTypes, LocalDate startDate, LocalDate endDate, long limit){}

}
