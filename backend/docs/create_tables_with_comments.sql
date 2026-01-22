-- 创建管理员表 (administrator)
CREATE TABLE administrator (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    created_date DATETIME NOT NULL COMMENT '创建时间',
    update_date DATETIME NOT NULL COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标记（0：未删除，1：已删除）'
);

-- 创建用户表 (user)
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    wwUserid VARCHAR(64) COMMENT '企业微信用户ID',
    created_date DATETIME NOT NULL COMMENT '创建时间',
    update_date DATETIME NOT NULL COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标记（0：未删除，1：已删除）'
);

-- 创建数据字典表 (dictionary)
CREATE TABLE dictionary (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '字典ID',
    category VARCHAR(50) NOT NULL COMMENT '类别（如：identity_type, team_size, company_size等）',
    code VARCHAR(20) NOT NULL COMMENT '编码',
    name VARCHAR(100) NOT NULL COMMENT '显示名称',
    description VARCHAR(255) COMMENT '描述',
    `order` INT DEFAULT 0 COMMENT '排序',
    created_date DATETIME NOT NULL COMMENT '创建时间',
    update_date DATETIME NOT NULL COMMENT '更新时间'
);

-- 创建问卷答题记录表 (questionnaire_answer_record)
CREATE TABLE questionnaire_answer_record (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '内部ID',
    record_no VARCHAR(32) NOT NULL UNIQUE COMMENT '业务记录编号（格式：RPT+时间戳+随机数）',
    user_id INT NOT NULL COMMENT '用户ID（外键，关联user表）',
    identity_type INT NOT NULL COMMENT '身份类型（外键，关联dictionary表）',
    team_size INT COMMENT '团队规模（外键，关联dictionary表）',
    company_size INT COMMENT '企业规模（外键，关联dictionary表）',
    industry INT COMMENT '所在行业（外键，关联dictionary表）',
    improvement_goal VARCHAR(50) COMMENT '最希望解决或改进的问题（逗号分隔的INT，关联dictionary表）',
    questionnaire_json JSON NOT NULL COMMENT '问卷JSON数据（AI生成，包含问题、选项和用户答案）',
    start_time DATETIME NOT NULL COMMENT '开始答题时间',
    end_time DATETIME COMMENT '结束答题时间',
    created_date DATETIME NOT NULL COMMENT '创建时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标记（0：未删除，1：已删除）'
);

-- 创建报告表 (report)
CREATE TABLE report (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '报告ID',
    user_id INT NOT NULL COMMENT '用户ID（外键，关联user表）',
    record_id INT NOT NULL COMMENT '答题记录ID（外键，关联questionnaire_answer_record表）',
    comprehensive_data JSON COMMENT '综合分析数据（包含行业对标分析）',
    dimension_data JSON COMMENT '维度详细分析数据',
    implementation_data JSON COMMENT '落地实施分析数据',
    created_date DATETIME NOT NULL COMMENT '创建时间',
    update_date DATETIME NOT NULL COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标记（0：未删除，1：已删除）'
);