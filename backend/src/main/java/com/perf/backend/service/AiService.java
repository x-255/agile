package com.perf.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perf.backend.dto.DimensionScore;
import com.perf.backend.util.ScoreCalculator;
import com.perf.backend.dto.ComprehensiveAnalysisData;
import com.perf.backend.dto.DimensionAnalysisData;
import com.perf.backend.dto.ImplementationAnalysisData;
import com.perf.backend.dto.QuestionnaireItem;
import com.perf.backend.entity.QuestionnaireAnswerRecord;
import com.perf.backend.entity.Report;
import com.perf.backend.mapper.QuestionnaireAnswerRecordMapper;

@Service
public class AiService {

  private final ChatClient chatClient;
  private final RedisTemplate<String, Object> redisTemplate;
  private final CacheUpdateService cacheUpdateService;
  private final ReportService reportService;
  private final QuestionnaireAnswerRecordMapper questionnaireAnswerRecordMapper;
  private final ObjectMapper objectMapper;
  private final DictionaryService dictionaryService;

  public AiService(ChatClient.Builder chatClientBuilder, RedisTemplate<String, Object> redisTemplate,
      CacheUpdateService cacheUpdateService, ReportService reportService, UserService userService,
      QuestionnaireAnswerRecordMapper questionnaireAnswerRecordMapper, ObjectMapper objectMapper,
      DictionaryService dictionaryService) {
    this.chatClient = chatClientBuilder.build();
    this.redisTemplate = redisTemplate;
    this.cacheUpdateService = cacheUpdateService;
    this.reportService = reportService;
    this.questionnaireAnswerRecordMapper = questionnaireAnswerRecordMapper;
    this.objectMapper = objectMapper;
    this.dictionaryService = dictionaryService;
  }

  public QuestionnaireItem[] generateQuestions(String userProfile, int length, String cacheKeyPrefix) {
    if (userProfile == null) {
      userProfile = "";
    }

    String baseKey = "ai:questions:" + cacheKeyPrefix + ":" + length;
    String cacheKey = baseKey + ":data";
    String timestampKey = baseKey + ":timestamp";
    String generatingKey = baseKey + ":generating";

    Long cachedTimestamp = null;
    QuestionnaireItem[] cachedResult = null;

    if (redisTemplate != null) {
      Object timestampObj = redisTemplate.opsForValue().get(timestampKey);
      cachedTimestamp = timestampObj instanceof Long ? (Long) timestampObj : null;

      Object resultObj = redisTemplate.opsForValue().get(cacheKey);
      cachedResult = resultObj instanceof QuestionnaireItem[] ? (QuestionnaireItem[]) resultObj : null;
    }

    long currentTime = System.currentTimeMillis();
    long cacheDuration = (long) (2.5 * 60 * 60 * 1000);

    if (cachedResult != null && cachedTimestamp != null) {
      if (currentTime - cachedTimestamp < cacheDuration) {
        return cachedResult;
      } else {
        // 缓存过期，检查是否正在生成
        Boolean isGenerating = null;
        if (redisTemplate != null) {
          Object generatingObj = redisTemplate.opsForValue().get(generatingKey);
          isGenerating = generatingObj instanceof Boolean ? (Boolean) generatingObj : null;
        }
        if (Boolean.FALSE.equals(isGenerating) || isGenerating == null) {
          // 没有正在生成，异步更新
          cacheUpdateService.updateCacheAsync(userProfile, length, cacheKey, timestampKey, generatingKey, currentTime);
        }
        return cachedResult;
      }
    }

    // 没有缓存，检查是否正在生成
    Boolean isGenerating = null;
    if (redisTemplate != null) {
      Object generatingObj = redisTemplate.opsForValue().get(generatingKey);
      isGenerating = generatingObj instanceof Boolean ? (Boolean) generatingObj : null;
    }
    if (Boolean.TRUE.equals(isGenerating)) {
      // 正在生成，等待生成完成
      long waitStartTime = System.currentTimeMillis();
      long maxWaitTime = 5 * 60 * 1000;
      long checkInterval = 500;

      while (System.currentTimeMillis() - waitStartTime < maxWaitTime) {
        try {
          Thread.sleep(checkInterval);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }

        // 再次检查缓存
        Object resultObj = redisTemplate.opsForValue().get(cacheKey);
        QuestionnaireItem[] result = resultObj instanceof QuestionnaireItem[] ? (QuestionnaireItem[]) resultObj : null;

        // 检查是否还在生成
        Object generatingObj = redisTemplate.opsForValue().get(generatingKey);
        Boolean stillGenerating = generatingObj instanceof Boolean ? (Boolean) generatingObj : null;

        if (result != null) {
          return result;
        }

        if (Boolean.FALSE.equals(stillGenerating)) {
          break;
        }
      }

      // 等待超时或生成完成但没有结果，返回空数组
      return new QuestionnaireItem[0];
    }

    // 同步生成新缓存
    QuestionnaireItem[] result = null;
    if (redisTemplate != null) {
      try {
        redisTemplate.opsForValue().set(generatingKey, Boolean.TRUE.booleanValue());
        result = generateNewQuestions(userProfile, length);
        redisTemplate.opsForValue().set(cacheKey, java.util.Objects.requireNonNull(result));
        redisTemplate.opsForValue().set(timestampKey, java.util.Objects.requireNonNull(Long.valueOf(currentTime)));
      } finally {
        redisTemplate.opsForValue().set(generatingKey, Boolean.FALSE.booleanValue());
      }
    } else {
      result = generateNewQuestions(userProfile, length);
    }
    return result;
  }

  private QuestionnaireItem[] generateNewQuestions(String userProfile, int length) {
    try {
      QuestionnaireItem[] result = chatClient.prompt()
          .user(u -> u.text(
              """
                  **任务：**
                  根据用户画像生成一份通俗易懂的评估题目。

                  **输入信息：**
                  用户画像数据在以下标签中提供：
                  <user_profile>
                  {userProfile}
                  </user_profile>

                  **生成要求：**
                  1. **题目依据**：参考业界公认的敏捷开发或流程改进方法
                  2. **结构设计**：生成6个维度，每个维度的题目数量相近，总题数等于{count}
                  3. **题型统一**：全部为单选题，每题设5个选项
                  4. **选项规范**：
                      - 选项按负面程度和分数从低到高排列
                      - 每个选项需为完整的客观描述句
                      - 避免使用"从不""经常"等模糊词语
                      - 题目本身保持中立，不引导用户选择
                  5. **语言表达**：
                      - 题目和选项需用中文书写
                      - 避免专业术语，确保大多数人能轻松理解
                      - 语言简洁，题目和选项不宜冗长
                      - 维度名长度限制为2到4个汉字
                  6. **初始答案**： 默认都统一为-1
                  """)
              .param("userProfile", userProfile)
              .param("count", String.valueOf(length)))
          .call()
          .entity(QuestionnaireItem[].class);
      return result != null ? result : new QuestionnaireItem[0];
    } catch (Exception e) {
      return new QuestionnaireItem[0];
    }
  }

  public ComprehensiveAnalysisData generateComprehensiveData(Integer reportId) {
    try {
      Report report = reportService.findById(reportId);
      if (report == null) {
        throw new IllegalArgumentException("Report not found");
      }

      QuestionnaireAnswerRecord record = questionnaireAnswerRecordMapper.selectById(report.getRecordId());
      if (record == null) {
        throw new IllegalArgumentException("Questionnaire record not found");
      }

      QuestionnaireItem[] questionnaireItems = objectMapper.readValue(record.getQuestionnaireJson(),
          QuestionnaireItem[].class);

      // 计算分数和等级
      List<DimensionScore> dimensionScores = new ArrayList<>();
      for (QuestionnaireItem item : questionnaireItems) {
        List<Integer> answers = new ArrayList<>();
        for (QuestionnaireItem.Question question : item.getQuestions()) {
          answers.add(question.getAnswerIndex());
        }
        Integer dimensionScore = ScoreCalculator.calculateDimensionScore(answers);
        dimensionScores.add(new DimensionScore(item.getDimension(), dimensionScore));
      }

      Integer comprehensiveScore = ScoreCalculator.calculateComprehensiveScore(dimensionScores);
      String maturityLevel = ScoreCalculator.determineMaturityLevel(comprehensiveScore);

      StringBuilder prompt = new StringBuilder();
      prompt.append("**任务：**\n");
      prompt.append("根据用户的问卷答案、分数信息和相关信息，生成一份综合分析报告。\n\n");

      prompt.append("**输入信息：**\n");

      // 添加用户信息
      appendUserInfo(prompt, record);

      // 添加分数信息
      appendScoreInfo(prompt, dimensionScores, comprehensiveScore, maturityLevel);

      // 添加问卷答案
      appendQuestionnaireAnswers(prompt, questionnaireItems);

      prompt.append("**生成要求：**\n");
      prompt.append("1. 生成综合分析数据，包含：\n");
      prompt.append("   - overallAssessment：综合能力的描述，3句话，每句话不超过100字符\n");
      prompt.append("   - keyStrengths：综合能力的核心优势，一句话，不超过60字符\n");
      prompt.append("   - keyWeaknesses：综合能力的关键短板，一句话，不超过60字符\n");
      prompt.append("   - industrialPosition：综合能力的行业定位，一句话，不超过60字符\n");
      prompt.append("   - strategicPositioning：战略位置评估，一句话，不超过70字符\n");
      prompt.append("   - leverageAnalysis：杠杆效应分析，一句话，不超过70字符\n");
      prompt.append("   - roiFocus：投资回报聚焦，一句话，不超过70字符\n");
      prompt.append("2. 分数信息中的平均分和标杆分请在合理范围内自动生成\n");
      prompt.append(
          "3. 所有字符串字段必须使用HTML标签，用<span style='color:#1890ff; font-weight:bold;'>高亮关键词</span>，让用户能在大段内容中第一时间提炼有用信息\n");
      prompt.append("4. 输出格式为纯JSON，不要包含任何其他文字\n");

      ComprehensiveAnalysisData result = chatClient.prompt()
          .user(prompt.toString())
          .call()
          .entity(new ParameterizedTypeReference<ComprehensiveAnalysisData>() {
          });

      String resultJson = objectMapper.writeValueAsString(result);
      reportService.updateComprehensiveData(reportId, resultJson);
      return result;
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate comprehensive data: " + e.getMessage(), e);
    }
  }

  public DimensionAnalysisData[] generateDimensionData(Integer reportId) {
    try {
      Report report = reportService.findById(reportId);
      if (report == null) {
        throw new IllegalArgumentException("Report not found");
      }

      QuestionnaireAnswerRecord record = questionnaireAnswerRecordMapper.selectById(report.getRecordId());
      if (record == null) {
        throw new IllegalArgumentException("Questionnaire record not found");
      }

      QuestionnaireItem[] questionnaireItems = objectMapper.readValue(record.getQuestionnaireJson(),
          QuestionnaireItem[].class);

      // 计算分数和等级
      List<DimensionScore> dimensionScores = new ArrayList<>();
      for (QuestionnaireItem item : questionnaireItems) {
        List<Integer> answers = new ArrayList<>();
        for (QuestionnaireItem.Question question : item.getQuestions()) {
          answers.add(question.getAnswerIndex());
        }
        Integer dimensionScore = ScoreCalculator.calculateDimensionScore(answers);
        dimensionScores.add(new DimensionScore(item.getDimension(), dimensionScore));
      }

      Integer comprehensiveScore = ScoreCalculator.calculateComprehensiveScore(dimensionScores);
      String maturityLevel = ScoreCalculator.determineMaturityLevel(comprehensiveScore);

      StringBuilder prompt = new StringBuilder();
      prompt.append("**角色：**\n");
      prompt.append("你是一个专业的敏捷行业专家，具有丰富的敏捷开发和流程改进经验。\n\n");

      prompt.append("**任务：**\n");
      prompt.append("根据用户的问卷答案、分数信息和相关信息，生成维度详细分析报告。\n\n");

      prompt.append("**输入信息：**\n");

      // 添加用户信息
      appendUserInfo(prompt, record);

      // 添加分数信息
      appendScoreInfo(prompt, dimensionScores, comprehensiveScore, maturityLevel);

      // 添加问卷答案
      appendQuestionnaireAnswers(prompt, questionnaireItems);

      prompt.append("**生成要求：**\n");
      prompt.append("1. 为每个维度生成详细分析，包含：\n");
      prompt.append("   - dimension：维度名称\n");
      prompt.append("   - detailedAnalysis：维度分析，不超过80字符\n");
      prompt.append("   - strengths：核心优势，字符串数组，2-3项，每项不超过8个字符\n");
      prompt.append("   - weaknesses：待改进项，字符串数组，2-3项，每项不超过8个字符\n");
      prompt.append("   - industryAverage：该维度的行业平均分，根据你的行业经验给出合理分数\n");
      prompt.append("   - industryBenchmark：该维度的行业标杆分数，根据你的行业经验给出合理分数\n");
      prompt.append("2. 输出格式为纯JSON数组，不要包含任何其他文字\n");

      DimensionAnalysisData[] result = chatClient.prompt()
          .user(prompt.toString())
          .call()
          .entity(new ParameterizedTypeReference<DimensionAnalysisData[]>() {
          });

      String resultJson = objectMapper.writeValueAsString(result);
      reportService.updateDimensionData(reportId, resultJson);
      return result;
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate dimension data: " + e.getMessage(), e);
    }
  }

  public ImplementationAnalysisData generateImplementationData(Integer reportId) {
    try {
      Report report = reportService.findById(reportId);
      if (report == null) {
        throw new IllegalArgumentException("Report not found");
      }

      QuestionnaireAnswerRecord record = questionnaireAnswerRecordMapper.selectById(report.getRecordId());
      if (record == null) {
        throw new IllegalArgumentException("Questionnaire record not found");
      }

      QuestionnaireItem[] questionnaireItems = objectMapper.readValue(record.getQuestionnaireJson(),
          QuestionnaireItem[].class);

      // 计算分数和等级
      List<DimensionScore> dimensionScores = new ArrayList<>();
      for (QuestionnaireItem item : questionnaireItems) {
        List<Integer> answers = new ArrayList<>();
        for (QuestionnaireItem.Question question : item.getQuestions()) {
          answers.add(question.getAnswerIndex());
        }
        Integer dimensionScore = ScoreCalculator.calculateDimensionScore(answers);
        dimensionScores.add(new DimensionScore(item.getDimension(), dimensionScore));
      }

      Integer comprehensiveScore = ScoreCalculator.calculateComprehensiveScore(dimensionScores);
      String maturityLevel = ScoreCalculator.determineMaturityLevel(comprehensiveScore);

      StringBuilder prompt = new StringBuilder();
      prompt.append("**任务：**\n");
      prompt.append("根据用户的问卷答案、分数信息和相关信息，生成落地实施分析报告。\n\n");

      prompt.append("**输入信息：**\n");

      // 添加用户信息
      appendUserInfo(prompt, record);

      // 添加分数信息
      appendScoreInfo(prompt, dimensionScores, comprehensiveScore, maturityLevel);

      // 添加问卷答案
      appendQuestionnaireAnswers(prompt, questionnaireItems);

      prompt.append("**生成要求：**\n");
      prompt.append("1. 生成落地实施分析，包含：\n");
      prompt.append(
          "   - roadmap：实施路线图，每个阶段包含name（阶段名称）、timeSlot（时间区间，格式参考为0-3月，3-6月等）、description（阶段描述，不超过12字符）、actions（关键行动，字符串数组，四项，每项不超过30字符）\n");
      prompt.append(
          "   - suggest：建议，包含location（当前位置，不超过80字符）、promote（提升建议，不超过80字符）、keysToSuccess（成功关键，不超过80字符）、resourceInvestment（资源投入建议，不超过80字符）\n");
      prompt.append("2. 输出格式为纯JSON，不要包含任何其他文字\n");

      ImplementationAnalysisData result = chatClient.prompt()
          .user(prompt.toString())
          .call()
          .entity(new ParameterizedTypeReference<ImplementationAnalysisData>() {
          });

      String resultJson = objectMapper.writeValueAsString(result);
      reportService.updateImplementationData(reportId, resultJson);
      return result;
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate implementation data: " + e.getMessage(), e);
    }
  }

  private String convertAnswerToOption(QuestionnaireItem.Question question, Integer answerIndex) {
    if (question == null || question.getOptions() == null || answerIndex == null || answerIndex < 0
        || answerIndex >= question.getOptions().size()) {
      return "未选择";
    }
    return question.getOptions().get(answerIndex);
  }

  private String getDictionaryName(String category, Integer id) {
    if (id == null) {
      return null;
    }
    return dictionaryService.getDictionaryNameById(id);
  }

  /**
   * 生成统一的用户信息提示词
   */
  private void appendUserInfo(StringBuilder prompt, QuestionnaireAnswerRecord record) {
    prompt.append("用户信息：\n");

    String identityType = getDictionaryName("identity_type", record.getIdentityType());
    if (identityType != null) {
      prompt.append("- 身份：").append(identityType).append("\n");
    }

    String teamSize = getDictionaryName("team_size", record.getTeamSize());
    if (teamSize != null) {
      prompt.append("- 团队规模：").append(teamSize).append("\n");
    }

    String companySize = getDictionaryName("company_size", record.getCompanySize());
    if (companySize != null) {
      prompt.append("- 企业规模：").append(companySize).append("\n");
    }

    if (record.getIndustry() != null) {
      String industryName = dictionaryService.getDictionaryNameById(record.getIndustry());
      prompt.append("- 行业：").append(industryName != null ? industryName : record.getIndustry()).append("\n");
    }

    if (record.getImprovementGoal() != null && !record.getImprovementGoal().isEmpty()) {
      String[] goalIds = record.getImprovementGoal().split(",");
      StringBuilder goalNames = new StringBuilder();
      for (int i = 0; i < goalIds.length; i++) {
        try {
          Integer goalId = Integer.parseInt(goalIds[i].trim());
          String goalName = dictionaryService.getDictionaryNameById(goalId);
          if (goalName != null) {
            if (i > 0) {
              goalNames.append(", ");
            }
            goalNames.append(goalName);
          } else {
            if (i > 0) {
              goalNames.append(", ");
            }
            goalNames.append(goalId);
          }
        } catch (NumberFormatException e) {
          if (i > 0) {
            goalNames.append(", ");
          }
          goalNames.append(goalIds[i].trim());
        }
      }
      prompt.append("- 改进目标：").append(goalNames).append("\n");
    }
  }

  /**
   * 生成统一的分数信息提示词
   */
  private void appendScoreInfo(StringBuilder prompt, List<DimensionScore> dimensionScores, Integer comprehensiveScore,
      String maturityLevel) {
    prompt.append("\n");
    prompt.append("分数信息：\n");
    prompt.append("- 综合分数：").append(comprehensiveScore).append("\n");
    prompt.append("- 成熟度等级：").append(maturityLevel).append("\n");
    prompt.append("- 维度分数：\n");
    for (DimensionScore ds : dimensionScores) {
      prompt.append("  - " + ds.getDimension() + "：" + ds.getScore() + "\n");
    }

    // 添加等级划分信息
    prompt.append("- 等级划分：\n");
    prompt.append("  - [0, 20]：L1（初始级）\n");
    prompt.append("  - (20, 40]：L2（基础级）\n");
    prompt.append("  - (40, 60]：L3（规范级）\n");
    prompt.append("  - (60, 80]：L4（优化级）\n");
    prompt.append("  - (80, 100]：L5（卓越级）\n");
  }

  /**
   * 生成统一的问卷答案提示词
   */
  private void appendQuestionnaireAnswers(StringBuilder prompt, QuestionnaireItem[] questionnaireItems) {
    prompt.append("\n");
    prompt.append("问卷答案：\n");
    for (QuestionnaireItem item : questionnaireItems) {
      prompt.append("维度：").append(item.getDimension()).append("\n");
      for (QuestionnaireItem.Question question : item.getQuestions()) {
        prompt.append("  问题：").append(question.getContent()).append("\n");
        prompt.append("  答案：").append(convertAnswerToOption(question, question.getAnswerIndex())).append("\n");
      }
      prompt.append("\n");
    }
  }
}