package com.aidiary.diary.event;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.DiaryException;
import com.aidiary.core.entity.DailyAnalysisSentencesEntity;
import com.aidiary.core.entity.DailyAnalysisWordsEntity;
import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.DiaryDatabaseWriteService;
import com.aidiary.infrastructure.transport.OpenAiTransporter;
import com.aidiary.infrastructure.transport.response.OpenAiResponseBundle;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyDiaryCreateEventListener {

    private final DiaryDatabaseWriteService diaryDatabaseWriteService;
    private final OpenAiTransporter openAiTransporter;

    @EventListener
    public void handleDailyDiaryCreateEvent(DailyDiaryCreateEvent event){
        try {
            OpenAiResponseBundle.OpenAiContent openAiContent = openAiTransporter.getAnalysisContentFromTurbo3Point5(event.getContent());

            diaryDatabaseWriteService.saveDailyDiaryAnalysisWordsAndSentences(
                    extractWordsFromDailyDiaryAnalysis(event.getUser(), event.getDiary(), openAiContent),
                    extractSentencesFromDailyDiaryAnalysis(event.getUser(), event.getDiary(), openAiContent)
            );
        } catch (JsonProcessingException e) {
            log.info("handleDailyDiaryCreateEvent Error ::", e);
            throw new DiaryException(ErrorCode.DIARY_ANALYSIS_FAIL);
        }
    }

    private List<DailyAnalysisWordsEntity> extractWordsFromDailyDiaryAnalysis(UsersEntity usersEntity, DiariesEntity diariesEntity, OpenAiResponseBundle.OpenAiContent openAiContent) {

        List<DailyAnalysisWordsEntity> extractedWords = new ArrayList<>();
        extractedWords.addAll(toDiaryWordEntities(usersEntity, diariesEntity, DiaryWordType.EMOTION, openAiContent.properties().emotions().words()));
        extractedWords.addAll(toDiaryWordEntities(usersEntity, diariesEntity, DiaryWordType.SELF_THOUGHT, openAiContent.properties().selfThoughts().words()));
        extractedWords.addAll(toDiaryWordEntities(usersEntity, diariesEntity, DiaryWordType.CORE_VALUE, openAiContent.properties().coreValues().words()));
        return extractedWords;

    }

    private List<DailyAnalysisSentencesEntity> extractSentencesFromDailyDiaryAnalysis(UsersEntity usersEntity, DiariesEntity diariesEntity, OpenAiResponseBundle.OpenAiContent openAiContent) {

        List<DailyAnalysisSentencesEntity> extractedSentences = new ArrayList<>();
        extractedSentences.add(toSentenceEntity(usersEntity, diariesEntity, DiarySentenceType.EMOTION, openAiContent.properties().emotions().content()));
        extractedSentences.add(toSentenceEntity(usersEntity, diariesEntity, DiarySentenceType.SELF_THOUGHT, openAiContent.properties().selfThoughts().content()));
        extractedSentences.add(toSentenceEntity(usersEntity, diariesEntity, DiarySentenceType.CORE_VALUE, openAiContent.properties().coreValues().content()));
        extractedSentences.add(toSentenceEntity(usersEntity, diariesEntity, DiarySentenceType.LITERARY_SUMMARY, openAiContent.summaries().literarySummary()));
        extractedSentences.addAll(toSentenceEntities(usersEntity, diariesEntity, DiarySentenceType.RECOMMENDED_ACTION, openAiContent.properties().recommendedActions()));
        extractedSentences.addAll(toSentenceEntities(usersEntity, diariesEntity, DiarySentenceType.ADDITIONAL, openAiContent.properties().additionals()));
        return extractedSentences;

    }

    public List<DailyAnalysisWordsEntity> toDiaryWordEntities(UsersEntity usersEntity, DiariesEntity diariesEntity, DiaryWordType diaryWordType, List<OpenAiResponseBundle.OpenAiWord> openAiWords){
        return openAiWords.stream().map(openAiWord -> toDiaryWordEntity(usersEntity, diariesEntity, diaryWordType, openAiWord)).collect(Collectors.toList());
    }

    public DailyAnalysisWordsEntity toDiaryWordEntity(UsersEntity usersEntity, DiariesEntity diariesEntity, DiaryWordType diaryWordType, OpenAiResponseBundle.OpenAiWord openAiWord){
        return DailyAnalysisWordsEntity.builder()
                .user(usersEntity)
                .diary(diariesEntity)
                .type(diaryWordType)
                .text(openAiWord.text())
                .scale(openAiWord.scale())
                .build();
    }

    private List<DailyAnalysisSentencesEntity> toSentenceEntities(UsersEntity usersEntity, DiariesEntity diariesEntity, DiarySentenceType diarySentenceType, List<String> sentences) {

        return sentences.stream().map(sentence -> DailyAnalysisSentencesEntity.builder()
                .user(usersEntity)
                .diary(diariesEntity)
                .type(diarySentenceType)
                .content(sentence)
                .build()).collect(Collectors.toList());

    }

    private DailyAnalysisSentencesEntity toSentenceEntity(UsersEntity usersEntity, DiariesEntity diariesEntity, DiarySentenceType diarySentenceType, String sentence) {

        return DailyAnalysisSentencesEntity.builder()
                .user(usersEntity)
                .diary(diariesEntity)
                .type(diarySentenceType)
                .content(sentence)
                .build();

    }

}
