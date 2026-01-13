-- 插入身份类型数据
INSERT INTO dictionary (id, category, code, name, description, `order`, created_date, update_date) VALUES
(1, 'identity_type', 'A', '个人(我主要想评估自身的技能与实践)', NULL, 0, NOW(), NOW()),
(2, 'identity_type', 'B', '单个团队 (我评估我所在或我辅导的一个团队，通常5-9人)', NULL, 0, NOW(), NOW()),
(3, 'identity_type', 'C', '整个业务单元或公司 (我评估涉及多个部门、几十至上百人的大型组织)', NULL, 0, NOW(), NOW());

-- 插入团队规模数据
INSERT INTO dictionary (id, category, code, name, description, `order`, created_date, update_date) VALUES
(4, 'team_size', 'A', '1-10人', NULL, 0, NOW(), NOW()),
(5, 'team_size', 'B', '11-50人', NULL, 0, NOW(), NOW()),
(6, 'team_size', 'C', '51-100人', NULL, 0, NOW(), NOW()),
(7, 'team_size', 'D', '100人以上', NULL, 0, NOW(), NOW());

-- 插入企业规模数据
INSERT INTO dictionary (id, category, code, name, description, `order`, created_date, update_date) VALUES
(8, 'company_size', 'A', '小微企业（<2千万）', NULL, 0, NOW(), NOW()),
(9, 'company_size', 'B', '中型企业（2千万-4亿）', NULL, 0, NOW(), NOW()),
(10, 'company_size', 'C', '大型企业（>4亿）', NULL, 0, NOW(), NOW());


-- 插入行业类型数据
INSERT INTO dictionary (id, category, code, name, description, `order`, created_date, update_date) VALUES
(11, 'industry', 'A', '互联网/科技/软件', NULL, 0, NOW(), NOW()),
(12, 'industry', 'B', '金融/保险', NULL, 0, NOW(), NOW()),
(13, 'industry', 'C', '医疗/制药', NULL, 0, NOW(), NOW()),
(14, 'industry', 'D', '政府/公共事业', NULL, 0, NOW(), NOW()),
(15, 'industry', 'E', '制造业/其它', NULL, 0, NOW(), NOW());

-- 插入希望解决的问题类型数据
INSERT INTO dictionary (id, category, code, name, description, `order`, created_date, update_date) VALUES
(16, 'improvement_goal', 'A', '提升交付速度，更快响应市场', NULL, 0, NOW(), NOW()),
(17, 'improvement_goal', 'B', '提高产品质量，减少线上故障', NULL, 0, NOW(), NOW()),
(18, 'improvement_goal', 'C', '改善团队协作、士气与文化', NULL, 0, NOW(), NOW()),
(19, 'improvement_goal', 'D', '满足审计、合规或安全要求', NULL, 0, NOW(), NOW()),
(20, 'improvement_goal', 'E', '让大型/复杂项目的管理更有序、更透明', NULL, 0, NOW(), NOW());