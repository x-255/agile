package com.perf.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perf.backend.dto.DimensionScore;
import com.perf.backend.dto.QuestionnaireItem;
import com.perf.backend.entity.QuestionnaireAnswerRecord;
import com.perf.backend.mapper.QuestionnaireAnswerRecordMapper;
import com.perf.backend.util.ScoreCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionnaireService {

  private final QuestionnaireAnswerRecordMapper questionnaireAnswerRecordMapper;
  private final ReportService reportService;
  private final UserService userService;
  private final ObjectMapper objectMapper;
  private final DictionaryService dictionaryService;

  public QuestionnaireService(QuestionnaireAnswerRecordMapper questionnaireAnswerRecordMapper,
      ReportService reportService, UserService userService, ObjectMapper objectMapper,
      DictionaryService dictionaryService) {
    this.questionnaireAnswerRecordMapper = questionnaireAnswerRecordMapper;
    this.reportService = reportService;
    this.userService = userService;
    this.objectMapper = objectMapper;
    this.dictionaryService = dictionaryService;
  }

  @Transactional
  public QuestionnaireSubmissionResult submitQuestionnaire(String name, String phone, String email,
      String identityTypeCode, String teamSizeCode, String companySizeCode, String industryCode,
      List<String> improvementGoalCodes, Long startTime, Long endTime, List<QuestionnaireItem> questionnaireJson) {
    try {
      Integer industry = dictionaryService.getDictionaryIdByCode("industry", industryCode);
      List<Integer> improvementGoalIds = improvementGoalCodes.stream()
          .map(code -> dictionaryService.getDictionaryIdByCode("improvement_goal", code))
          .collect(java.util.stream.Collectors.toList());
      String improvementGoalStr = improvementGoalIds.stream()
          .map(String::valueOf)
          .collect(java.util.stream.Collectors.joining(","));

      var user = userService.findOrCreateUser(name, phone, email, identityTypeCode, teamSizeCode, companySizeCode,
          industryCode, improvementGoalStr);

      LocalDateTime startDateTime = Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
      LocalDateTime endDateTime = Instant.ofEpochMilli(endTime).atZone(ZoneId.systemDefault()).toLocalDateTime();

      Integer identityTypeId = dictionaryService.getDictionaryIdByCode("identity_type", identityTypeCode);
      Integer teamSizeId = dictionaryService.getDictionaryIdByCode("team_size", teamSizeCode);
      Integer companySizeId = dictionaryService.getDictionaryIdByCode("company_size", companySizeCode);

      String questionnaireJsonStr = objectMapper.writeValueAsString(questionnaireJson);
      QuestionnaireAnswerRecord record = new QuestionnaireAnswerRecord();
      record.setUserId(user.getId());
      record.setQuestionnaireJson(questionnaireJsonStr);
      record.setStartTime(startDateTime);
      record.setEndTime(endDateTime);
      record.setRecordNo(generateRecordNo());
      record.setIdentityType(identityTypeId);
      record.setTeamSize(teamSizeId);
      record.setCompanySize(companySizeId);
      record.setIndustry(industry);
      record.setImprovementGoal(improvementGoalStr);
      questionnaireAnswerRecordMapper.insert(record);

      List<DimensionScore> dimensionScores = calculateDimensionScores(questionnaireJson);
      Integer comprehensiveScore = ScoreCalculator.calculateComprehensiveScore(dimensionScores);
      String maturityLevel = ScoreCalculator.determineMaturityLevel(comprehensiveScore);

      var report = reportService.createReport(user.getId(), record.getId());

      return new QuestionnaireSubmissionResult(
          report.getId(),
          record.getId(),
          user.getId(),
          user.getName(),
          dimensionScores,
          comprehensiveScore,
          maturityLevel);
    } catch (Exception e) {
      throw new RuntimeException("Failed to submit questionnaire: " + e.getMessage(), e);
    }
  }

  private List<DimensionScore> calculateDimensionScores(List<QuestionnaireItem> questionnaireJson) {
    List<DimensionScore> dimensionScores = new ArrayList<>();

    for (QuestionnaireItem item : questionnaireJson) {
      String dimension = item.getDimension();
      List<QuestionnaireItem.Question> questions = item.getQuestions();
      List<Integer> answers = new ArrayList<>();

      for (QuestionnaireItem.Question question : questions) {
        answers.add(question.getAnswerIndex());
      }

      Integer dimensionScore = ScoreCalculator.calculateDimensionScore(answers);
      dimensionScores.add(new DimensionScore(dimension, dimensionScore));
    }

    return dimensionScores;
  }

  private String generateRecordNo() {
    return "REC" + System.currentTimeMillis();
  }

  public record QuestionnaireSubmissionResult(
      Integer reportId,
      Integer recordId,
      Integer userId,
      String userName,
      List<DimensionScore> dimensionScores,
      Integer comprehensiveScore,
      String maturityLevel) {
  }
}
