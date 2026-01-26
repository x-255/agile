package com.perf.backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perf.backend.dto.ComprehensiveAnalysisData;
import com.perf.backend.dto.DimensionAnalysisData;
import com.perf.backend.dto.GenerateAiDataRequest;
import com.perf.backend.dto.ImplementationAnalysisData;
import com.perf.backend.dto.QuestionnaireItem;
import com.perf.backend.dto.Result;
import com.perf.backend.dto.UserProfileRequest;
import com.perf.backend.entity.Dictionary;
import com.perf.backend.service.AiService;
import com.perf.backend.service.DictionaryService;

@RestController
@RequestMapping("/ai")
public class AiController {

  private final AiService aiService;
  private final DictionaryService dictionaryService;
  private static final Logger logger = LoggerFactory.getLogger(AiController.class);

  public AiController(AiService aiService, DictionaryService dictionaryService) {
    this.aiService = aiService;
    this.dictionaryService = dictionaryService;
  }

  @PostMapping("/getQuestions")
  public Result getQuestions(@RequestBody UserProfileRequest request) {
    try {
      // 构建用于AI服务的用户画像（使用指定格式）
      StringBuilder userProfileBuilder = new StringBuilder();

      // 添加身份类型
      String identityTypeName = getDictionaryName("identity_type", request.getIdentityType());
      if (identityTypeName != null) {
        userProfileBuilder.append("身份：").append(identityTypeName);
      }

      // 添加行业类型
      String industryName = getDictionaryName("industry", request.getIndustry());
      if (industryName != null) {
        if (userProfileBuilder.length() > 0)
          userProfileBuilder.append("\n");
        userProfileBuilder.append("所在行业：").append(industryName);
      }

      // 添加改进目标（支持数组）
      if (request.getImprovementGoal() != null && request.getImprovementGoal().length > 0) {
        StringBuilder goalsBuilder = new StringBuilder();
        for (int i = 0; i < request.getImprovementGoal().length; i++) {
          String goalCode = request.getImprovementGoal()[i];
          String goalName = getDictionaryName("improvement_goal", goalCode);
          if (goalName != null) {
            if (goalsBuilder.length() > 0)
              goalsBuilder.append("，");
            goalsBuilder.append(goalName);
          }
        }

        if (goalsBuilder.length() > 0) {
          if (userProfileBuilder.length() > 0)
            userProfileBuilder.append("\n");
          userProfileBuilder.append("当前最希望解决或改进的问题：").append(goalsBuilder.toString());
        }
      }

      String userProfileForAI = userProfileBuilder.length() > 0 ? userProfileBuilder.toString()
          : "身份：团队\n所在行业：科技\n当前最希望解决或改进的问题：提升效率";

      // 构建用于缓存的简短key（使用code）
      StringBuilder cacheKeyBuilder = new StringBuilder();
      if (request.getIdentityType() != null && !request.getIdentityType().isEmpty())
        cacheKeyBuilder.append("IT-").append(request.getIdentityType()).append("-");
      if (request.getIndustry() != null && !request.getIndustry().isEmpty())
        cacheKeyBuilder.append("IN-").append(request.getIndustry()).append("-");
      if (request.getImprovementGoal() != null) {
        for (String goalCode : request.getImprovementGoal()) {
          cacheKeyBuilder.append("IG-").append(goalCode).append("-");
        }
      }

      String cacheKey = cacheKeyBuilder.length() > 0 ? cacheKeyBuilder.substring(0, cacheKeyBuilder.length() - 1)
          : "default";

      logger.info("User Profile for AI: {}", userProfileForAI);
      logger.info("Cache Key: {}", cacheKey);

      int length = request.getLength() != null ? request.getLength() : 15; // 默认为15
      QuestionnaireItem[] response = aiService.generateQuestions(userProfileForAI,
          length, cacheKey);
      return Result.success(response);
    } catch (Exception e) {
      return Result.fail(500, "AI服务异常: " + e.getMessage());
    }
  }

  private String getDictionaryName(String category, String code) {
    if (code == null || code.isEmpty()) {
      return null;
    }

    List<Dictionary> dictionaries = dictionaryService.getByCategory(category);
    return dictionaries.stream()
        .filter(dict -> dict.getCode().equals(code))
        .map(Dictionary::getName)
        .findFirst()
        .orElse(null);
  }

  @PostMapping("/generateComprehensiveData")
  public Result generateComprehensiveData(@RequestBody GenerateAiDataRequest request) {
    try {
      logger.info("Generating comprehensive data for reportId: {}", request.getReportId());
      ComprehensiveAnalysisData comprehensiveData = aiService.generateComprehensiveData(request.getReportId());
      logger.info("Successfully generated comprehensive data for reportId: {}", request.getReportId());
      return Result.success(comprehensiveData);
    } catch (Exception e) {
      logger.error("Failed to generate comprehensive data: {}", e.getMessage(), e);
      return Result.fail(500, "生成综合分析数据失败: " + e.getMessage());
    }
  }

  @PostMapping("/generateDimensionData")
  public Result generateDimensionData(@RequestBody GenerateAiDataRequest request) {
    try {
      logger.info("Generating dimension data for reportId: {}", request.getReportId());
      DimensionAnalysisData[] dimensionData = aiService.generateDimensionData(request.getReportId());
      logger.info("Successfully generated dimension data for reportId: {}", request.getReportId());
      return Result.success(dimensionData);
    } catch (Exception e) {
      logger.error("Failed to generate dimension data: {}", e.getMessage(), e);
      return Result.fail(500, "生成维度详细分析数据失败: " + e.getMessage());
    }
  }

  @PostMapping("/generateImplementationData")
  public Result generateImplementationData(@RequestBody GenerateAiDataRequest request) {
    try {
      logger.info("Generating implementation data for reportId: {}", request.getReportId());
      ImplementationAnalysisData implementationData = aiService.generateImplementationData(request.getReportId());
      logger.info("Successfully generated implementation data for reportId: {}", request.getReportId());
      return Result.success(implementationData);
    } catch (Exception e) {
      logger.error("Failed to generate implementation data: {}", e.getMessage(), e);
      return Result.fail(500, "生成落地实施分析数据失败: " + e.getMessage());
    }
  }
}