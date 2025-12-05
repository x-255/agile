package com.perf.backend.service;

import com.perf.backend.dto.QuestionnaireItem;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AiService {

  private final ChatClient chatClient;
  private final RedisTemplate<String, Object> redisTemplate;
  private final CacheUpdateService cacheUpdateService;

  public AiService(ChatClient.Builder chatClientBuilder, RedisTemplate<String, Object> redisTemplate,
      CacheUpdateService cacheUpdateService) {
    this.chatClient = chatClientBuilder.build();
    this.redisTemplate = redisTemplate;
    this.cacheUpdateService = cacheUpdateService;
  }

  public QuestionnaireItem[] generateQuestions(String userProfile, int length) {
    if (userProfile == null) {
      userProfile = "";
    }

    String baseKey = "ai:questions:" + userProfile + ":" + length;
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

                  **用户画像结构：**
                  - 视角：个人、团队、企业
                  - 行业：互联网/科技、金融/保险、医疗/制药等
                  - 痛点：提升效率、优化协作、提高质量等

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
}