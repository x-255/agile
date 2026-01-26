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
| 字段名                | 数据类型        | 约束                          | 描述                          |
| ------------------ | ----------- | --------------------------- | --------------------------- |
| id                 | INT         | PRIMARY KEY, AUTO_INCREMENT | 内部ID                        |
| record_no          | VARCHAR(32) | NOT NULL, UNIQUE            | 业务记录编号（格式：RPT+时间戳+随机数）      |
| user_id            | INT         | NOT NULL                    | 用户ID（外键，关联user表）            |
| identity_type      | INT         | NOT NULL                    | 身份类型（外键，关联dictionary表）      |
| team_size          | INT         |                             | 团队规模（外键，关联dictionary表）      |
| company_size       | INT         |                             | 企业规模（外键，关联dictionary表）      |
| industry           | INT         |                             | 所在行业（外键，关联dictionary表）     |
| improvement_goal   | VARCHAR(50) |                             | 最希望解决或改进的问题（逗号分隔的INT，关联dictionary表） |
| questionnaire_json | JSON        | NOT NULL                    | 问卷JSON数据（AI生成，包含问题、选项和用户答案） |
| start_time         | DATETIME    | NOT NULL                    | 开始答题时间                      |
| end_time           | DATETIME    |                             | 结束答题时间                      |
| created_date       | DATETIME    | NOT NULL                    | 创建时间                        |
| deleted            | INT         | DEFAULT 0                   | 逻辑删除标记（0：未删除，1：已删除）         |

### 4.2 表描述
用于存储用户答题记录，包括答题时的身份信息快照、行业信息、改进目标、AI生成的问卷JSON数据、用户答案JSON数据、答题时间、状态等信息。

## 5. 报告表 (report)

### 5.1 表结构
| 字段名                 | 数据类型     | 约束                          | 描述                                        |
| ------------------- | -------- | --------------------------- | ----------------------------------------- |
| id                  | INT      | PRIMARY KEY, AUTO_INCREMENT | 报告ID                                      |
| user_id             | INT      | NOT NULL                    | 用户ID（外键，关联user表）                          |
| record_id           | INT      | NOT NULL                    | 答题记录ID（外键，关联questionnaire_answer_record表） |
| comprehensive_data  | JSON     |                             | 综合分析数据（包含行业对标分析）                          |
| dimension_data      | JSON     |                             | 维度详细分析数据                                  |
| implementation_data | JSON     |                             | 落地实施分析数据                                  |
| created_date        | DATETIME | NOT NULL                    | 创建时间                                      |
| update_date         | DATETIME | NOT NULL                    | 更新时间                                      |
| deleted             | INT      | DEFAULT 0                   | 逻辑删除标记（0：未删除，1：已删除）                       |

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
  "overallAssessment": "综合能力的描述，3句话，每句话不超过100字符",
  "keyStrengths": "综合能力的核心优势，一句话，不超过60字符",
  "keyWeaknesses": "综合能力的关键短板，一句话，不超过60字符",
  "industrialPosition": "综合能力的行业定位，一句话，不超过60字符",
  "benchmarkComprehensivePositioning": "行业对标的定位，一句话，不超过70字符",
  "benchmarkCompetitiveAdvantages": "行业对标的优势分析，一句话，不超过70字符",
  "benchmarkGrowthPotential": "行业对标的成长空间，一句话，不超过70字符",
  "comprehensiveScoreInfo": {
    "score": "上文中给出的综合能力分数",
    "industryAverage": "你根据自己的行业经验，给出的行业平均综合能力分数",
    "industryBenchmark": "你根据行业标准，给出的行业综合能力标杆分数"
  }
}
```

dimension_data 结构 - 维度详细分析
```json
[
  {
    "dimension": "维度名称",
    "detailedAnalysis": "维度分析，不超过80字符",
    "industryAverage": "你根据自己的行业经验，给出的行业平均综合能力分数",
    "industryBenchmark": "你根据行业标准，给出的行业综合能力标杆分数",
    "strengths": "核心优势，字符串数组，2-3项，每项不超过8个字符",
    "weaknesses": "待改进项，字符串数组，2-3项，每项不超过8个字符",
  }
]
```

implementation_data 结构 - 落地实施分析
```json
{
  "roadmap": [
    {
      "name": "阶段名称",
      "timeSlot": "时间槽，格式参考为0-3月，3-6月。。。",
      "description": "阶段描述，不超过12字符",
      "actions": "关键行动，字符串数组，四项，每项不超过30字符"
    }
  ],
  "suggest": {
    "location": "当前定位，不超过80字符",
    "promote": "晋升路径，不超过80字符",
    "keysToSuccess": "成功关键，不超过80字符",
    "resourceInvestment": "资源投入，不超过80字符"
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
