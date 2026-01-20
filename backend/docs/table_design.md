# 数据库表设计文档

## 1. 管理员表 (administrator)

### 1.1 表结构
| 字段名 | 数据类型 | 约束 | 描述 |
| --- | --- | --- | --- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 管理员ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(255) | NOT NULL | 密码（加密存储） |
| created_date | DATETIME | NOT NULL | 创建时间 |
| update_date | DATETIME | NOT NULL | 更新时间 |
| deleted | INT | DEFAULT 0 | 逻辑删除标记（0：未删除，1：已删除） |

### 1.2 表描述
用于存储系统管理员信息，支持登录和管理功能。

## 2. 用户表 (user)

### 2.1 表结构
| 字段名           | 数据类型         | 约束                          | 描述                     |
| ------------- | ------------ | --------------------------- | ---------------------- |
| id            | INT          | PRIMARY KEY, AUTO_INCREMENT | 用户ID                   |
| name          | VARCHAR(100) | NOT NULL                    | 姓名                     |
| phone         | VARCHAR(20)  | NOT NULL, UNIQUE            | 手机号                    |
| email         | VARCHAR(100) | NOT NULL, UNIQUE            | 邮箱                     |
| wwUserid      | VARCHAR(64)  |                             | 企业微信用户ID               |
| created_date  | DATETIME     | NOT NULL                    | 创建时间                   |
| update_date   | DATETIME     | NOT NULL                    | 更新时间                   |
| deleted       | INT          | DEFAULT 0                   | 逻辑删除标记（0：未删除，1：已删除）    |

### 2.2 表描述
用于存储系统用户信息，包括姓名、手机号、邮箱、企业微信用户ID等基本信息。

## 3. 数据字典表 (dictionary)

### 3.1 表结构
| 字段名       | 数据类型     | 约束                        | 描述                                                 |
| ------------ | ------------ | --------------------------- | ---------------------------------------------------- |
| id           | INT          | PRIMARY KEY, AUTO_INCREMENT | 字典ID                                               |
| category     | VARCHAR(50)  | NOT NULL                    | 类别（如：identity_type, team_size, company_size等） |
| code         | VARCHAR(20)  | NOT NULL                    | 编码                                                 |
| name         | VARCHAR(100) | NOT NULL                    | 显示名称                                             |
| description  | VARCHAR(255) |                             | 描述                                                 |
| order        | INT          | DEFAULT 0                   | 排序                                                 |
| created_date | DATETIME     | NOT NULL                    | 创建时间                                             |
| update_date  | DATETIME     | NOT NULL                    | 更新时间                                             |

### 3.2 表描述
用于存储系统中各类枚举值和字典数据，支持动态扩展和维护。

## 4. 问卷答题记录表 (questionnaire_answer_record)

### 4.1 表结构
| 字段名             | 数据类型    | 约束                        | 描述                                             |
| ------------------ | ----------- | --------------------------- | ------------------------------------------------ |
| id                 | INT         | PRIMARY KEY, AUTO_INCREMENT | 内部ID                                           |
| record_no          | VARCHAR(32) | NOT NULL, UNIQUE            | 业务记录编号（格式：RPT+时间戳+随机数）          |
| user_id            | INT         | NOT NULL                    | 用户ID（外键，关联user表）                       |
| identity_type      | INT         | NOT NULL                    | 身份类型（外键，关联dictionary表）               |
| team_size          | INT         |                             | 团队规模（外键，关联dictionary表）               |
| company_size       | INT         |                             | 企业规模（外键，关联dictionary表）               |
| industry           | VARCHAR(20) |                             | 所在行业                                         |
| improvement_goal   | VARCHAR(50) |                             | 最希望解决或改进的问题                             |
| questionnaire_json | JSON        | NOT NULL                    | 问卷JSON数据（AI生成，包含问题、选项和用户答案） |
| start_time         | DATETIME    | NOT NULL                    | 开始答题时间                                     |
| end_time           | DATETIME    |                             | 结束答题时间                                     |
| created_date       | DATETIME    | NOT NULL                    | 创建时间                                         |
| deleted            | INT         | DEFAULT 0                   | 逻辑删除标记（0：未删除，1：已删除）             |

### 4.2 表描述
用于存储用户答题记录，包括答题时的身份信息快照、行业信息、改进目标、AI生成的问卷JSON数据、用户答案JSON数据、答题时间、状态等信息。

## 5. 报告表 (report)

### 5.1 表结构
| 字段名 | 数据类型 | 约束 | 描述 |
| --- | --- | --- | --- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 报告ID |
| user_id | INT | NOT NULL | 用户ID（外键，关联user表） |
| record_id | INT | NOT NULL | 答题记录ID（外键，关联questionnaire_answer_record表） |
| comprehensive_data | JSON | | 综合分析数据（包含行业对标分析） |
| dimension_data | JSON | | 维度详细分析数据 |
| implementation_data | JSON | | 落地实施分析数据 |
| created_date | DATETIME | NOT NULL | 创建时间 |
| update_date | DATETIME | NOT NULL | 更新时间 |
| deleted | INT | DEFAULT 0 | 逻辑删除标记（0：未删除，1：已删除） |

### 5.2 表描述
用于存储分步生成的评估报告信息，支持异步处理和容错机制。

### 5.3 接口调用流程
1. **接口1**：综合分析 + 行业对标分析 → 存储到`comprehensive_data`
2. **接口2**：维度详细分析 → 存储到`dimension_data`  
3. **接口3**：落地实施分析 → 存储到`implementation_data`

### 5.4 JSON数据结构示例
comprehensive_data 结构 - 综合分析 + 行业对标分析
```json
{
  "overallAssessment": "您的团队整体敏捷成熟度达到L4优化级，在团队协作和需求管理方面表现突出，但在部署发布和测试质量方面需要改进",
  "keyStrengths": "团队沟通协作机制完善，需求优先级管理良好，项目管理流程规范",
  "keyWeaknesses": "自动化部署流程需要优化，测试覆盖率和质量需要提升，代码审查流程需要改进",
  "benchmarkComprehensivePositioning": "您的团队在行业对标中处于中上水平，在团队协作和需求管理方面表现突出，具备较强的市场竞争力",
  "benchmarkCompetitiveAdvantages": "团队协作能力和需求管理能力是您的主要竞争优势，在同类企业中表现优异",
  "benchmarkGrowthPotential": "在技术实践和自动化方面存在提升空间，通过改进部署发布和测试质量可以进一步提升行业地位"
}
```

dimension_data 结构 - 维度详细分析
```json
[
  {
    "dimension": "迭代周期",
    "detailedAnalysis": "迭代周期为2周，符合敏捷开发的时间管理规范，团队成员能够及时响应需求变更。",
    "strengths": ["及时响应需求变更", "项目管理流程规范", "交付准时率"],
    "weaknesses": ["迭代周期过长，导致项目进度延迟"]
  },
  {
    "dimension": "部署发布",
    "detailedAnalysis": "部署发布流程为2天，符合敏捷开发的时间管理规范，团队成员能够及时发布新功能。",
    "strengths": ["及时发布新功能", "项目管理流程规范"],
    "weaknesses": ["部署发布流程过长，导致项目进度延迟"]
  }
]
```

implementation_data 结构 - 落地实施分析
```json
{
  "roadmap": [
    {
      "name": "初始探索阶段",
      "timeSlot": "0-3个月",
      "description": "建立敏捷基本认知与初步实践",
      "actions": ["认识敏捷基本概念和价值观", "尝试实施每日站会，建立沟通节奏", "初步使用看板或任务板管理任务", "建立基本的迭代周期概念"]
    },
    {
      "name": "基础建设阶段",
      "timeSlot": "3-6个月",
      "description": "建立基本框架，形成稳定实践",
      "actions": [ "建立规范的Scrum仪式（站会、计划会、评审会、回顾会）", "实施故事点估算，统一工作量评估标准", "建立产品待办事项列表，明确优先级", "初步建立团队工作规范和准则"]
    }
  ],
  "suggest": {
    "location": "您的团队目前处于L4优化级，已建立系统化的敏捷流程，具备良好的持续改进基础。",
    "promote": "完成当前阶段的关键行动后，预计可在3-6个月内达到L5卓越级。重点需要提升知识管理和自动化能力。",
    "keysToSuccess": "建议任命一位改进负责人，每月评审进展，每季度评估效果。保持改进的持续性和连贯性。",
    "resourceInvestment": "当前阶段主要依赖现有资源，下一阶段可能需要引入外部工具和培训，建议提前规划资源。"
  }
}
```
## 数据字典表示例数据

### 身份类型 (category: identity_type)
| id  | category      | code | name |
| --- | ------------- | ---- | ---- |
| 1   | identity_type | 1    |  (我主要想评估自身的技能与实践)   |
| 2   | identity_type | 2    | 单个团队 (我评估我所在或我辅导的一个团队，通常5-9人)   |
| 3   | identity_type | 3    | 整个业务单元或公司 (我评估涉及多个部门、几十至上百人的大型组织)   |

### 团队规模 (category: team_size)
| id  | category  | code | name      |
| --- | --------- | ---- | --------- |
| 4   | team_size | 1    | 1-10人    |
| 5   | team_size | 2    | 11-50人   |
| 6   | team_size | 3    | 51-100人  |
| 7   | team_size | 4    | 100人以上 |

### 企业规模 (category: company_size)
| id  | category     | code | name       |
| --- | ------------ | ---- | ---------- |
| 8   | company_size | 1    | 小微企业（<2千万）   |
| 9   | company_size | 2    | 中型企业（2千万-4亿）   |
| 10  | company_size | 3    | 大型企业（>4亿）  |
