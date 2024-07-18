package com.aidiary.user.application.service;

import com.aidiary.common.enums.DiaryStatus;
import com.aidiary.common.enums.DiarySentenceType;
import com.aidiary.common.enums.DiaryWordType;
import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.DiaryException;
import com.aidiary.user.application.dto.DiaryRequestBundle;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static com.aidiary.common.enums.DiaryStatus.ACTIVE;
import static com.aidiary.common.enums.DiaryStatus.INACTIVE;

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

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public DiarySaveRes saveDiaryAfterOpenAiAnalysis(UsersEntity usersEntity, DiaryCreateRequest request) throws Exception {

        Optional<DiariesEntity> sameEntryDateDiary = jpaDiariesRepository.findByEntryDateAndStatus(request.entryDate(), DiaryStatus.ACTIVE);

        if (sameEntryDateDiary.isPresent()) {
            throw new DiaryException(ErrorCode.DIARY_ALREADY_EXIST);
        }

        OpenAiContent openAiContent = openAiTransporter.getAnalysisContentFromTurbo3Point5(request.content());

        // 다이어리 정보 저장
        DiariesEntity diariesEntity = jpaDiariesRepository.save(
                DiariesEntity.builder()
                        .user(usersEntity)
                        .content(hybridDiaryEncryptor.encrypt(request.content()))
                        .entryDate(request.entryDate())
                        .status(ACTIVE)
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

        return DiarySaveRes.builder().diaryId(diariesEntity.getId()).build();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public DiarySaveRes updateDiaryAfterOpenAiAnalysis(UsersEntity usersEntity, Long diaryId, DiaryRequestBundle.DiaryUpdateRequest request) throws Exception {

        DiariesEntity originalDiary = jpaDiariesRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(ErrorCode.DIARY_NOT_FOUND));
        originalDiary.updateStatus(INACTIVE);

        OpenAiContent openAiContent = openAiTransporter.getAnalysisContentFromTurbo3Point5(request.content());

        // 다이어리 정보 저장
        DiariesEntity diariesEntity = jpaDiariesRepository.save(
                DiariesEntity.builder()
                        .user(usersEntity)
                        .content(hybridDiaryEncryptor.encrypt(request.content()))
                        .entryDate(originalDiary.getEntryDate())
                        .status(ACTIVE)
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

        return DiarySaveRes.builder().diaryId(diariesEntity.getId()).build();
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

    public DiaryDetail getDiaryDetail(Long userId, Long diaryId) throws Exception {

        DiariesEntity diariesEntity = jpaDiariesRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(ErrorCode.DIARY_NOT_FOUND));

        if (!diariesEntity.getUser().getId().equals(userId)) {
            throw new DiaryException(ErrorCode.DIARY_OWNER_MISMATCH);
        }

        if (INACTIVE.equals(diariesEntity.getStatus())) {
            throw new DiaryException(ErrorCode.DIARY_NOT_FOUND);
        }

        String diaryContent = hybridDiaryEncryptor.decrypt(diariesEntity.getContent());

        Map<DiaryWordType, List<DiaryWord>> wordsByType = diaryWordsByType(jpaDailyAnalysisWordsRepository.findByDiary(diariesEntity));
        Map<DiarySentenceType, List<String>> sentencesByType = diarySentencesByType(jpaDailyAnalysisSentencesRepository.findByDiary(diariesEntity));

        return DiaryDetail.builder()
                .entryDate(diariesEntity.getEntryDate())
                .emotions(
                        DiaryEmotions.builder()
                                .content(sentencesByType.get(DiarySentenceType.EMOTION).get(0))
                                .words(wordsByType.get(DiaryWordType.EMOTION))
                                .build()
                )
                .selfThoughts(
                        DiarySelfThoughts.builder()
                                .content(sentencesByType.get(DiarySentenceType.SELF_THOUGHT).get(0))
                                .words(wordsByType.get(DiaryWordType.SELF_THOUGHT))
                                .build()
                )
                .coreValues(
                        DiaryCoreValues.builder()
                                .content(sentencesByType.get(DiarySentenceType.CORE_VALUE).get(0))
                                .words(wordsByType.get(DiaryWordType.CORE_VALUE))
                                .build()
                )
                .recommendedActions(sentencesByType.get(DiarySentenceType.RECOMMENDED_ACTION))
                .additionals(sentencesByType.get(DiarySentenceType.ADDITIONAL))
                .literarySummary(sentencesByType.get(DiarySentenceType.LITERARY_SUMMARY).get(0))
                .diaryContent(diaryContent)
                .build();
    }

    private Map<DiaryWordType, List<DiaryWord>> diaryWordsByType(List<DailyAnalysisWordsEntity> dailyAnalysisWordsEntities){

        Map<DiaryWordType, List<DiaryWord>> wordsByType = new HashMap<>();

        for (DailyAnalysisWordsEntity dailyAnalysisWordsEntity : dailyAnalysisWordsEntities) {
            List<DiaryWord> words = wordsByType.getOrDefault(dailyAnalysisWordsEntity.getType(), new ArrayList<DiaryWord>());
            words.add(DiaryWord.of(dailyAnalysisWordsEntity));
            wordsByType.put(dailyAnalysisWordsEntity.getType(), words);
        }

        return wordsByType;
    }

    private Map<DiarySentenceType, List<String>> diarySentencesByType(List<DailyAnalysisSentencesEntity> dailyAnalysisSentencesEntities) {

        Map<DiarySentenceType, List<String>> sentencesByType = new HashMap<>();

        for (DailyAnalysisSentencesEntity dailyAnalysisSentencesEntity : dailyAnalysisSentencesEntities) {
            List<String> sentences = sentencesByType.getOrDefault(dailyAnalysisSentencesEntity.getContent(), new ArrayList<String>());
            sentences.add(dailyAnalysisSentencesEntity.getContent());
            sentencesByType.put(dailyAnalysisSentencesEntity.getType(), sentences);
        }

        return sentencesByType;
    }

}
