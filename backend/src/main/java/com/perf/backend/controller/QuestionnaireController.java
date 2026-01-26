package com.perf.backend.controller;

import com.perf.backend.dto.Result;
import com.perf.backend.dto.SubmitQuestionnaireRequest;
import com.perf.backend.service.QuestionnaireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questionnaire")
public class QuestionnaireController {

  private final QuestionnaireService questionnaireService;
  private static final Logger logger = LoggerFactory.getLogger(QuestionnaireController.class);

  public QuestionnaireController(QuestionnaireService questionnaireService) {
    this.questionnaireService = questionnaireService;
  }

  @PostMapping("/submit")
  public Result submitQuestionnaire(@RequestBody SubmitQuestionnaireRequest request) {
    try {
      logger.info("Received questionnaire submission request: name={}, phone={}", request.getName(),
          request.getPhone());

      var result = questionnaireService.submitQuestionnaire(
          request.getName(),
          request.getPhone(),
          request.getEmail(),
          request.getIdentityType(),
          request.getTeamSize(),
          request.getCompanySize(),
          request.getIndustry(),
          request.getImprovementGoal(),
          request.getStartTime(),
          request.getEndTime(),
          request.getQuestionnaireJson());

      return Result.success(result);
    } catch (Exception e) {
      logger.error("Failed to submit questionnaire: {}", e.getMessage(), e);
      return Result.fail(500, "提交问卷失败: " + e.getMessage());
    }
  }
}
