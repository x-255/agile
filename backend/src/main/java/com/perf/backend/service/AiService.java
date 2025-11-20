package com.perf.backend.service;

import com.perf.backend.dto.QuestionnaireData;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

  private final ChatClient chatClient;

  public AiService(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  public QuestionnaireData[] generateQuestions(int length) {
    String userProfile = "企业、金融、合规审计";
    return chatClient.prompt()
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
                {profile}
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
                """)
            .param("profile", userProfile)
            .param("count", String.valueOf(length)))
        .call()
        .entity(QuestionnaireData[].class);
  }
}