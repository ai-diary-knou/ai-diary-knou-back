package com.aidiary.user.application.service;

import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.user.application.dto.DiaryRequestBundle.DiariesOfMonthGetRequest;
import com.aidiary.user.application.dto.DiaryRequestBundle.DiaryCreateRequest;
import com.aidiary.user.application.dto.DiaryResponseBundle.*;
import com.aidiary.user.domain.entity.DailyAnalysisSentencesEntity;
import com.aidiary.user.domain.entity.DailyAnalysisWordsEntity;
import com.aidiary.user.domain.entity.DiariesEntity;
import com.aidiary.user.domain.entity.UsersEntity;
import com.aidiary.user.domain.repository.JpaDailyAnalysisSentencesRepository;
import com.aidiary.user.domain.repository.JpaDailyAnalysisWordsRepository;
import com.aidiary.user.domain.repository.JpaDiariesRepository;
import com.aidiary.user.infrastructure.encryptor.HybridDiaryEncryptor;
import com.aidiary.user.infrastructure.transport.OpenAiTransporter;
import com.aidiary.user.infrastructure.transport.response.OpenAiResponseBundle.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiaryService {

    private final OpenAiTransporter openAiTransporter;
    private final JpaDiariesRepository jpaDiariesRepository;
    private final JpaDailyAnalysisWordsRepository jpaDailyAnalysisWordsRepository;
    private final JpaDailyAnalysisSentencesRepository jpaDailyAnalysisSentencesRepository;
    private final HybridDiaryEncryptor hybridDiaryEncryptor;

    public MainReportResponse getMainReportsOfDiaries() {

        List<String> recentSevenLiterarySummaries = new ArrayList<>();
        List<BigDecimal> recentSevenAverageEmotionScales = new ArrayList<>();
        List<String> recentTenRepetitiveKeywords = new ArrayList<>();
        List<String> recentRecommendedActions = new ArrayList<>();

        return MainReportResponse.builder()
                .recentSevenLiterarySummaries(recentSevenLiterarySummaries)
                .recentSevenAverageEmotionScales(recentSevenAverageEmotionScales)
                .recentTenRepetitiveKeywords(recentTenRepetitiveKeywords)
                .recentRecommendedActions(recentRecommendedActions)
                .build();
    }


    public MonthlyReportResponse getMonthlyReportsOfDiaries(DiariesOfMonthGetRequest request) {

        LocalDate selectedDate = null;
        List<String> recentSevenLiterarySummaries = new ArrayList<>();
        List<DiaryOutline> monthlyDiaryReports = new ArrayList<>();

        return MonthlyReportResponse.builder()
                .selectedDate(selectedDate)
                .monthlyDiaryReports(monthlyDiaryReports)
                .build();
    }

    public DiaryDetail saveDiaryAfterOpenAiAnalysis(UsersEntity usersEntity, DiaryCreateRequest request) throws Exception {


        OpenAiContent openAiContent = openAiTransporter.getAnalysisContentFromTurbo3Point5(request.content());

        // 다이어리 정보 저장
        DiariesEntity diariesEntity = jpaDiariesRepository.save(
                DiariesEntity.builder()
                        .user(usersEntity)
                        .content(hybridDiaryEncryptor.encrypt(request.content()))
                        .entryDate(request.entryDate())
                        .build()
        );

        // words 저장
        OpenAiEmotions openAiEmotions = openAiContent.properties().emotions();
        OpenAiSelfThoughts openAiSelfThoughts = openAiContent.properties().selfThoughts();
        OpenAiCoreValues openAiCoreValues = openAiContent.properties().coreValues();

        saveWords(usersEntity, diariesEntity, DiaryWordType.EMOTION, openAiEmotions.words());
        saveWords(usersEntity, diariesEntity, DiaryWordType.SELF_THOUGHT, openAiSelfThoughts.words());
        saveWords(usersEntity, diariesEntity, DiaryWordType.CORE_VALUE, openAiCoreValues.words());

        // sentence 저장
        List<String> recommendedActions = openAiContent.properties().recommendedActions();
        List<String> additionals = openAiContent.properties().additionals();
        String literarySummary = openAiContent.summaries().literarySummary();

        saveSentences(usersEntity, diariesEntity, DiarySentenceType.EMOTION, openAiEmotions.content());
        saveSentences(usersEntity, diariesEntity, DiarySentenceType.SELF_THOUGHT, openAiSelfThoughts.content());
        saveSentences(usersEntity, diariesEntity, DiarySentenceType.CORE_VALUE, openAiCoreValues.content());
        saveSentences(usersEntity, diariesEntity, DiarySentenceType.RECOMMENDED_ACTION, recommendedActions);
        saveSentences(usersEntity, diariesEntity, DiarySentenceType.ADDITIONAL, additionals);
        saveSentences(usersEntity, diariesEntity, DiarySentenceType.LITERARY_SUMMARY, literarySummary);

        return DiaryDetail.builder()
                .entryDate(request.entryDate())
                .emotions(DiaryEmotions.fromOpenAiEmotions(openAiContent.properties().emotions()))
                .selfThoughts(DiarySelfThoughts.fromOpenAiSelfThoughts(openAiContent.properties().selfThoughts()))
                .coreValues(DiaryCoreValues.fromOpenAiCoreValues(openAiContent.properties().coreValues()))
                .recommendedActions(openAiContent.properties().recommendedActions())
                .additionals(openAiContent.properties().additionals())
                .literarySummary(literarySummary)
                .diaryContent(request.content())
                .build();
    }

    private void saveWords(UsersEntity usersEntity, DiariesEntity diariesEntity, DiaryWordType diaryWordType, List<OpenAiWord> words){

        List<DailyAnalysisWordsEntity> dailyAnalysisWordsEntities = new ArrayList<>();

        for (OpenAiWord openAiWord : words) {
            if (Objects.isNull(openAiWord)) continue;
            dailyAnalysisWordsEntities.add(
                    DailyAnalysisWordsEntity.builder()
                            .user(usersEntity)
                            .diary(diariesEntity)
                            .type(diaryWordType)
                            .text(openAiWord.text())
                            .scale(openAiWord.scale())
                            .build()
            );
        }

        jpaDailyAnalysisWordsRepository.saveAll(dailyAnalysisWordsEntities);

    }

    private void saveSentences(UsersEntity usersEntity, DiariesEntity diariesEntity, DiarySentenceType diarySentenceType, String sentence) {

        if (!StringUtils.hasText(sentence)) return;

        jpaDailyAnalysisSentencesRepository.save(
                DailyAnalysisSentencesEntity.builder()
                        .user(usersEntity)
                        .diary(diariesEntity)
                        .type(diarySentenceType)
                        .content(sentence)
                        .build()
        );

    }

    private void saveSentences(UsersEntity usersEntity, DiariesEntity diariesEntity, DiarySentenceType diarySentenceType, List<String> sentences) {

        List<DailyAnalysisSentencesEntity> dailyAnalysisSentencesEntities = new ArrayList<>();

        for (String sentence : sentences) {
            dailyAnalysisSentencesEntities.add(
                    DailyAnalysisSentencesEntity.builder()
                            .user(usersEntity)
                            .diary(diariesEntity)
                            .type(diarySentenceType)
                            .content(sentence)
                            .build()
            );
        }

        jpaDailyAnalysisSentencesRepository.saveAll(dailyAnalysisSentencesEntities);

    }

}
