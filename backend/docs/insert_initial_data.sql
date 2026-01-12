-- 插入身份类型数据
INSERT INTO dictionary (id, category, code, name, description, `order`, created_date, update_date) VALUES
(1, 'identity_type', '1', '个人(我主要想评估自身的技能与实践)', NULL, 0, NOW(), NOW()),
(2, 'identity_type', '2', '单个团队 (我评估我所在或我辅导的一个团队，通常5-9人)', NULL, 0, NOW(), NOW()),
(3, 'identity_type', '3', '整个业务单元或公司 (我评估涉及多个部门、几十至上百人的大型组织)', NULL, 0, NOW(), NOW());

-- 插入团队规模数据
INSERT INTO dictionary (id, category, code, name, description, `order`, created_date, update_date) VALUES
(4, 'team_size', '1', '1-10人', NULL, 0, NOW(), NOW()),
(5, 'team_size', '2', '11-50人', NULL, 0, NOW(), NOW()),
(6, 'team_size', '3', '51-100人', NULL, 0, NOW(), NOW()),
(7, 'team_size', '4', '100人以上', NULL, 0, NOW(), NOW());

-- 插入企业规模数据
INSERT INTO dictionary (id, category, code, name, description, `order`, created_date, update_date) VALUES
(8, 'company_size', '1', '小微企业（<2千万）', NULL, 0, NOW(), NOW()),
(9, 'company_size', '2', '中型企业（2千万-4亿）', NULL, 0, NOW(), NOW()),
(10, 'company_size', '3', '大型企业（>4亿）', NULL, 0, NOW(), NOW());