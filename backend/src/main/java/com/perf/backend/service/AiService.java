package com.perf.backend.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

  private final ChatClient chatClient;

  private final String sysMessage = """
          你是一个精通敏捷流程的专家级助手，擅长帮助个人、团队及企业系统评估敏捷实践成熟度，明确敏捷落地过程中的优势与短板，\
          为敏捷转型与优化提供科学决策依据。你会仔细分析用户的需求，严格按照用户的要求输出答案。
      """;

  public AiService(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  public String generateQuestions(int length) {
    String userProfile = "企业、金融、合规审计";
    String message = """
                  你是一个严谨的AI助手，专门生成可被机器直接解析的纯净JSON数据。

        **任务：**
        根据用户画像生成一份评估题目。

        **用户画像结构：**
        - 视角：个人、团队、企业
        - 行业：互联网/科技/软件、金融/保险、医疗/制药等
        - 痛点：提升交付速度、提高产品质量、改善团队协作等

        **输入信息：**
        用户画像数据将包含在以下标签内：
        <user_profile>
        %s
        </user_profile>
        需要生成 %d 道题目。

        **生成要求：**
        1.  **框架基础**：结合scrum、cmmi、safe等成熟的敏捷或过程改进模型。
        2.  **题目结构**：生成6个维度，每个维度下的题目数量基本相同。
        3.  **题目类型**：全部为单选题，每题提供5个选项。
        4.  **选项设计**：
            - 越靠前的选项越负面，对应的分数越低。
            - 每个选项必须是完整的一句话，客观描述一种行为或状态。禁止使用“从不”、“有时”、“经常”等模糊或主观性词汇。
            - 确保题目本身是客观的，不引导答题者。

        **输出格式：**
        你必须且只能输出一个标准的JSON数组，格式如下，不得包含任何JSON之外的其他字符、空格、换行符（如`\r`, `\n`）、注释或说明文字。

        ```json
        [{"dimension": "维度名称1", "questions": [{"content": "题目内容1", "answers": ["负面行为描述", "较负面行为描述", "中性行为描述", "较好行为描述", "积极行为描述"]}, ...]}, {"dimension": "维度名称2", "questions": [{"content": "题目内容2", "answers": ["负面行为描述", "较负面行为描述", "中性行为描述", "较好行为描述", "积极行为描述"]}, ...]}, ...]
                """
        .formatted(userProfile, length);
    return chatClient.prompt()
        .system(sysMessage)
        .user(message)
        .call()
        .content();
  }
}