-- 初始化脚本：Refinex-Cloud 基础通用数据
-- 说明：
-- 1) 适配 MySQL 8.x，使用唯一键/主键 + INSERT ... ON DUPLICATE KEY UPDATE 或 INSERT IGNORE 实现幂等
-- 2) 不创建表，仅插入基础数据；请先执行 DDL（document/sql/mysql/refinex_platform.sql）
-- 3) 超级管理员账户建议走应用启动的自动初始化（来自配置中心），本脚本不强制插入用户

SET NAMES utf8mb4;
SET @now = NOW();

USE `refinex_platform`;

START TRANSACTION;

-- =============================================
-- 1. 角色（若 DDL 已含内置角色，可跳过；此处保证幂等）
-- =============================================
INSERT INTO `sys_role` (`id`, `system_flags`, `role_code`, `role_name`, `role_type`, `data_scope`, `priority`, `remark`, `status`, `create_time`, `update_time`)
VALUES
    (1, 1, 'SUPER_ADMIN', '超级管理员', 1, 1, 1000, '系统最高权限角色', 0, @now, @now)
ON DUPLICATE KEY UPDATE
    role_name = VALUES(role_name),
    update_time = @now;

INSERT INTO `sys_role` (`id`, `system_flags`, `role_code`, `role_name`, `role_type`, `data_scope`, `priority`, `remark`, `status`, `create_time`, `update_time`)
VALUES
    (2, 1, 'ROLE_USER', '普通用户', 0, 3, 100, '默认普通用户角色', 0, @now, @now)
ON DUPLICATE KEY UPDATE
    role_name = VALUES(role_name),
    update_time = @now;

-- =============================================
-- 2. 权限点（与常用菜单相匹配，便于给普通用户授权；超管走 *:*:* 逻辑）
-- permission_type: menu/button/api 三类，可结合前后端管控习惯
-- =============================================
INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `parent_id`, `module_name`, `status`, `create_time`, `update_time`)
VALUES
    (10001, 'dashboard:view', '仪表盘访问', 'menu', 0, 'system', 0, @now, @now),
    (10002, 'dashboard:analysis:view', '分析页访问', 'menu', 10001, 'system', 0, @now, @now),
    (10003, 'dashboard:monitor:view', '监控页访问', 'menu', 10001, 'system', 0, @now, @now),
    (10004, 'dashboard:workplace:view', '工作台访问', 'menu', 10001, 'system', 0, @now, @now),

    (10100, 'system:user:list', '用户列表', 'api', 0, 'system', 0, @now, @now),
    (10101, 'system:user:create', '用户新增', 'api', 0, 'system', 0, @now, @now),
    (10102, 'system:user:update', '用户编辑', 'api', 0, 'system', 0, @now, @now),
    (10103, 'system:user:resetPwd', '用户重置密码', 'api', 0, 'system', 0, @now, @now),

    (10110, 'system:role:list', '角色列表', 'api', 0, 'system', 0, @now, @now),
    (10111, 'system:role:assign', '角色分配', 'api', 0, 'system', 0, @now, @now),

    (10120, 'system:menu:list', '菜单列表', 'api', 0, 'system', 0, @now, @now),

    (10200, 'dict:type:list', '字典类型列表', 'api', 0, 'system', 0, @now, @now),
    (10201, 'dict:data:list', '字典数据列表', 'api', 0, 'system', 0, @now, @now),

    (10300, 'config:sys:list', '系统配置查询', 'api', 0, 'system', 0, @now, @now)
ON DUPLICATE KEY UPDATE
    permission_name = VALUES(permission_name),
    update_time = @now;

-- 角色-权限 绑定（为 ROLE_USER 赋予基础查看权限，避免空白页面）
INSERT IGNORE INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
SELECT 20000 + ROW_NUMBER() OVER (), 2 AS role_id, p.id, @now
FROM sys_permission p
WHERE p.permission_code IN (
    'dashboard:view','dashboard:analysis:view','dashboard:monitor:view','dashboard:workplace:view',
    'dict:type:list','dict:data:list','config:sys:list'
);

-- =============================================
-- 3. 菜单（与前端 Umi routes.ts 基本对齐，作为后端可控路由树）
-- menu_type: M目录, C菜单, F按钮；此处仅给出常用演示菜单
-- =============================================
-- 根目录：仪表盘
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `menu_type`, `route_path`, `component_path`, `menu_icon`, `is_visible`, `status`, `sort`, `create_time`, `update_time`)
VALUES (30000, '仪表盘', 0, 'M', '/dashboard', NULL, 'dashboard', 1, 0, 1, @now, @now)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), update_time = @now;

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `menu_type`, `route_path`, `component_path`, `menu_icon`, `is_visible`, `status`, `sort`, `create_time`, `update_time`)
VALUES
    (30001, '分析页', 30000, 'C', '/dashboard/analysis', 'dashboard/analysis', 'smile', 1, 0, 10, @now, @now),
    (30002, '监控页', 30000, 'C', '/dashboard/monitor', 'dashboard/monitor', 'smile', 1, 0, 20, @now, @now),
    (30003, '工作台', 30000, 'C', '/dashboard/workplace', 'dashboard/workplace', 'smile', 1, 0, 30, @now, @now)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), update_time = @now;

-- 根目录：系统管理（示例）
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `menu_type`, `route_path`, `component_path`, `menu_icon`, `is_visible`, `status`, `sort`, `create_time`, `update_time`)
VALUES (30100, '系统管理', 0, 'M', '/system', NULL, 'setting', 1, 0, 2, @now, @now)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), update_time = @now;

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `menu_type`, `route_path`, `component_path`, `menu_icon`, `is_visible`, `status`, `sort`, `create_time`, `update_time`)
VALUES
    (30101, '用户管理', 30100, 'C', '/system/user', 'system/user', 'user', 1, 0, 10, @now, @now),
    (30102, '角色管理', 30100, 'C', '/system/role', 'system/role', 'team', 1, 0, 20, @now, @now),
    (30103, '菜单管理', 30100, 'C', '/system/menu', 'system/menu', 'menu', 1, 0, 30, @now, @now)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), update_time = @now;

-- =============================================
-- 4. 系统配置（sys_config）
-- is_sensitive=1 的值建议走密文或敏感存储方案（此处仅示例）
-- =============================================
INSERT INTO `sys_config`
(`id`,`config_key`,`config_value`,`config_type`,`config_group`,`config_label`,`config_desc`,`is_sensitive`,`is_frontend`,`status`,`create_time`,`update_time`)
VALUES
    (40001,'system.title','Refinex Cloud','STRING','system','系统标题','站点标题',0,1,0,@now,@now),
    (40002,'system.subtitle','现代化微服务云平台','STRING','system','系统副标题','站点副标题',0,1,0,@now,@now),
    (40003,'site.logo','/public/logo.svg','STRING','system','站点Logo','前端展示Logo路径',0,1,0,@now,@now),
    (40004,'captcha.enabled','true','BOOLEAN','auth','启用验证码','登录是否启用图形验证码',0,0,0,@now,@now),
    (40005,'file.upload.maxSize','100MB','STRING','file','最大上传大小','前端/后端统一提示',0,0,0,@now,@now),
    (40006,'file.default.storageType','S3','STRING','file','默认存储类型','S3/OSS/COS/KODO/DATABASE',0,0,0,@now,@now)
ON DUPLICATE KEY UPDATE
    config_value = VALUES(config_value),
    update_time = @now;

-- =============================================
-- 5. 数据字典（类型 + 数据项）
-- 按代码注释和表注释补齐：用户状态、通用状态、性别、菜单类型、布尔、订单/订阅/退款状态等
-- =============================================

-- 字典类型
INSERT INTO `sys_dict_type` (`id`,`dict_code`,`dict_name`,`dict_desc`,`status`,`create_time`,`update_time`)
VALUES
    (50001,'common_status','通用状态','系统通用状态字典',0,@now,@now),
    (50002,'user_status','用户状态','用户账号状态字典',0,@now,@now),
    (50003,'sex','性别','用户性别字典',0,@now,@now),
    (50004,'menu_type','菜单类型','系统菜单类型字典',0,@now,@now),
    (50005,'boolean','布尔值','通用布尔值字典',0,@now,@now),
    (50006,'order_status','订单状态','订单状态字典',0,@now,@now),
    (50007,'subscription_status','订阅状态','会员订阅状态字典',0,@now,@now),
    (50008,'refund_status','退款状态','退款申请状态字典',0,@now,@now),
    (50009,'pay_channel','支付渠道','支付渠道类型字典',0,@now,@now),
    (50010,'user_type','用户类型','用户类型字典',0,@now,@now),
    (50011,'role_type','角色类型','角色类型字典',0,@now,@now),
    (50012,'data_scope','数据权限范围','角色数据权限范围字典',0,@now,@now)
ON DUPLICATE KEY UPDATE dict_name = VALUES(dict_name), dict_desc = VALUES(dict_desc), update_time = @now;

-- 字典数据（通过类型编码定位类型ID，幂等插入）
-- 通用状态
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51001, t.id, '正常','0',1,1,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='common_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51002, t.id, '停用','1',2,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='common_status';

-- 用户状态（与 sys_user.user_status 注释一致）
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51010, t.id, '待激活','0',1,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='user_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51011, t.id, '正常','1',2,1,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='user_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51012, t.id, '冻结','2',3,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='user_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51013, t.id, '注销','3',4,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='user_status';

-- 性别
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51020, t.id, '男','male',1,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='sex';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51021, t.id, '女','female',2,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='sex';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51022, t.id, '其他','other',3,1,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='sex';

-- 菜单类型
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51030, t.id, '目录','M',1,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='menu_type';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51031, t.id, '菜单','C',2,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='menu_type';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51032, t.id, '按钮','F',3,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='menu_type';

-- 布尔
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51040, t.id, '否','0',1,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='boolean';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51041, t.id, '是','1',2,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='boolean';

-- 订单/订阅/退款状态（与表注释保持一致）
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51050, t.id, '待支付','0',1,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='order_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51051, t.id, '已支付','1',2,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='order_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51052, t.id, '已取消','2',3,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='order_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51053, t.id, '已关闭','3',4,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='order_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51054, t.id, '已退款','4',5,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='order_status';

INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51060, t.id, '试用中','0',1,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='subscription_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51061, t.id, '正常','1',2,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='subscription_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51062, t.id, '已过期','2',3,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='subscription_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51063, t.id, '已取消','3',4,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='subscription_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51064, t.id, '已退款','4',5,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='subscription_status';

INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51070, t.id, '待审核','0',1,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='refund_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51071, t.id, '审核通过','1',2,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='refund_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51072, t.id, '退款中','2',3,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='refund_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51073, t.id, '退款成功','3',4,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='refund_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51074, t.id, '退款失败','4',5,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='refund_status';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51075, t.id, '已拒绝','5',6,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='refund_status';

-- 支付渠道
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51080, t.id, '微信支付','WXPAY',1,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='pay_channel';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51081, t.id, '支付宝','ALIPAY',2,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='pay_channel';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51082, t.id, '余额','BALANCE',3,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='pay_channel';

-- 用户类型
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51090, t.id, '后台用户','sys_user',1,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='user_type';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51091, t.id, '移动端用户','app_user',2,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='user_type';

-- 角色类型
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51100, t.id, '前台角色','0',1,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='role_type';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51101, t.id, '后台角色','1',2,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='role_type';

-- 数据权限范围
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51110, t.id, '所有数据权限','1',1,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='data_scope';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51111, t.id, '自定义数据权限','2',2,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='data_scope';
INSERT INTO `sys_dict_data`(`id`,`dict_type_id`,`dict_label`,`dict_value`,`dict_sort`,`is_default`,`status`,`create_time`,`update_time`)
SELECT 51112, t.id, '仅本人数据权限','3',3,0,0,@now,@now FROM sys_dict_type t WHERE t.dict_code='data_scope';

-- =============================================
-- 6. 文件存储配置（file_storage_config）
-- 初始提供示例配置并默认禁用，运维后续补齐密钥并启用；默认类型以 sys_config 或应用配置为准
-- =============================================
INSERT INTO `file_storage_config`
(`id`,`config_code`,`config_name`,`storage_type`,`provider`,`endpoint`,`region`,`bucket_name`,`base_path`,`domain_url`,`is_default`,`is_enabled`,`priority`,`create_time`,`update_time`)
VALUES
    (60001,'S3_DEFAULT','AWS S3 默认','S3','AWS','https://s3.amazonaws.com',NULL,'refinex-files','files/','',1,0,10,@now,@now),
    (60002,'OSS_ALIYUN','阿里云 OSS','OSS','ALIYUN','https://oss-cn-shanghai.aliyuncs.com','cn-shanghai','refinex-files','files/','',0,0,9,@now,@now),
    (60003,'COS_TENCENT','腾讯云 COS','COS','TENCENT','https://cos.ap-guangzhou.myqcloud.com','ap-guangzhou','refinex-files','files/','',0,0,8,@now,@now),
    (60004,'KODO_QINIU','七牛云 KODO','KODO','QINIU','https://s3-cn-east-1.qiniucs.com','cn-east-1','refinex-files','files/','',0,0,7,@now,@now),
    (60005,'DB_STORAGE','数据库存储','DATABASE','INTERNAL',NULL,NULL,NULL,'files/','',0,0,1,@now,@now)
ON DUPLICATE KEY UPDATE
    config_name = VALUES(config_name),
    update_time = @now;

-- =============================================
-- 7. 会员等级与订阅计划（mbr_level / mbr_plan）
-- =============================================
INSERT INTO `mbr_level` (`id`,`level_code`,`level_name`,`level_benefits`,`priority`,`status`,`create_time`,`update_time`)
VALUES
    (70001,'FREE','免费用户',JSON_OBJECT('chatQuotaPerDay', 20), 0, 0, @now, @now),
    (70002,'PRO','专业版',JSON_OBJECT('chatQuotaPerDay', 200,'imagePerDay',20), 10, 0, @now, @now)
ON DUPLICATE KEY UPDATE
    level_name = VALUES(level_name),
    update_time = @now;

INSERT INTO `mbr_plan` (`id`,`plan_code`,`plan_name`,`level_id`,`duration_value`,`duration_unit`,`original_price`,`current_price`,`discount`,`is_renewable`,`is_published`,`status`,`create_time`,`update_time`)
VALUES
    (71001,'PLAN_PRO_MONTH','专业版-月度',70002,1,'month',2990,1990,0.67,1,1,0,@now,@now),
    (71002,'PLAN_PRO_YEAR','专业版-年度',70002,1,'year',29900,19900,0.67,1,1,0,@now,@now)
ON DUPLICATE KEY UPDATE
    plan_name = VALUES(plan_name),
    update_time = @now;

-- =============================================
-- 8. 支付渠道（mbr_pay_channel）默认禁用，待运维配置
-- =============================================
INSERT INTO `mbr_pay_channel`
(`id`,`channel_code`,`channel_name`,`channel_type`,`merchant_id`,`app_id`,`api_key`,`api_secret`,`api_url`,`notify_url`,`is_enabled`,`priority`,`create_time`,`update_time`)
VALUES
    (80001,'WXPAY','微信支付','第三方支付',NULL,NULL,NULL,NULL,NULL,NULL,0,100,@now,@now),
    (80002,'ALIPAY','支付宝','第三方支付',NULL,NULL,NULL,NULL,NULL,NULL,0,90,@now,@now),
    (80003,'BALANCE','余额支付','钱包余额',NULL,NULL,NULL,NULL,NULL,NULL,1,10,@now,@now)
ON DUPLICATE KEY UPDATE
    channel_name = VALUES(channel_name),
    update_time = @now;

-- =============================================
-- 9. AI 模型配置（ai_model_config）默认禁用，便于后续启用
-- =============================================
INSERT INTO `ai_model_config`
(`id`,`model_code`,`model_name`,`provider`,`model_type`,`api_endpoint`,`api_key`,`api_version`,`model_capabilities`,`context_window`,`max_tokens`,`temperature`,`pricing_input`,`pricing_output`,`is_enabled`,`priority`,`create_time`,`update_time`)
VALUES
    (90001,'GPT4','OpenAI GPT-4','OPENAI','CHAT',NULL,NULL,'v1',JSON_OBJECT('tools',true,'vision',true), 8192, 2048, 0.70, 0, 0, 0, 100, @now, @now),
    (90002,'QWEN_MAX','通义千问Max','ALIBABA','CHAT',NULL,NULL,'v1',JSON_OBJECT('tools',true,'vision',true), 8000, 2000, 0.70, 0, 0, 0, 90, @now, @now)
ON DUPLICATE KEY UPDATE
    model_name = VALUES(model_name),
    update_time = @now;

-- =============================================
-- 10. 邮件模板（email_template）
-- 提供“验证码邮件”系统模板；其余模板见 document/sql/mysql/email_templates.sql
-- =============================================
INSERT INTO `email_template`
(`id`,`template_code`,`template_name`,`template_subject`,`template_content`,`template_type`,`variables`,`template_category`,`is_system`,`status`,`create_time`,`update_time`)
VALUES
    (1000,'VERIFY_CODE','验证码邮件','您的验证码','<p>您的验证码为：<strong th:text=\"${code}\">123456</strong>，有效期 <span th:text=\"${minutes}\">10</span> 分钟。</p>','HTML','[\"code\",\"minutes\"]','验证码',1,0,@now,@now)
ON DUPLICATE KEY UPDATE
    template_name = VALUES(template_name),
    update_time = @now;

COMMIT;