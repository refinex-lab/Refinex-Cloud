SET NAMES utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

CREATE DATABASE IF NOT EXISTS `refinex_platform`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `refinex_platform`;

CREATE TABLE `sys_user`
(
    `id`              BIGINT       NOT NULL COMMENT '主键ID',
    `username`        VARCHAR(50)  NOT NULL COMMENT '登录用户名',
    `mobile`          VARCHAR(20)           DEFAULT NULL COMMENT '脱敏手机号,如138****1234',
    `email`           VARCHAR(100)          DEFAULT NULL COMMENT '脱敏邮箱,如abc***@gmail.com',
    `password`        VARCHAR(100) NOT NULL COMMENT 'BCrypt加密后的密码哈希',
    `nickname`        VARCHAR(50)           DEFAULT NULL COMMENT '用户昵称',
    `sex`             VARCHAR(10)           DEFAULT NULL COMMENT '性别:male,female,other',
    `avatar`          VARCHAR(500)          DEFAULT NULL COMMENT '头像URL',
    `user_status`     TINYINT      NOT NULL DEFAULT 0 COMMENT '用户状态:0待激活,1正常,2冻结,3注销',
    `user_type`       VARCHAR(20)  NOT NULL DEFAULT 'sys_user' COMMENT '用户类型(sys_user:后台用户, app_user:移动端用户)',
    `register_source` VARCHAR(20)           DEFAULT NULL COMMENT '注册来源:web,ios,android,h5',
    `last_login_time` DATETIME              DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip`   VARCHAR(50)           DEFAULT NULL COMMENT '最后登录IP',
    `create_by`       BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`          VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    `sort`            INT          NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`      JSON                  DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_username` (`username`) COMMENT '用户名唯一索引',
    KEY `idx_mobile` (`mobile`) COMMENT '手机号索引',
    KEY `idx_email` (`email`) COMMENT '邮箱索引',
    KEY `idx_status_deleted` (`user_status`, `deleted`) COMMENT '状态和删除标记联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表-存储用户基础身份信息';

CREATE TABLE `sys_sensitive`
(
    `id`                   BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `row_guid`             VARCHAR(64)           NOT NULL COMMENT '被加密记录的唯一标识,对应业务表主键',
    `table_name`           VARCHAR(64)           NOT NULL COMMENT '来源表名,如sys_user',
    `field_code`           VARCHAR(64)           NOT NULL COMMENT '字段代码,如mobile或email',
    `encrypted_value`      TEXT                  NOT NULL COMMENT '加密后的值,AES-256加密',
    `encryption_algorithm` VARCHAR(20)           NOT NULL DEFAULT 'AES256-GCM' COMMENT '加密算法标识,默认AES256-GCM(带认证)',
    `create_time`          DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`          DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_unique_sensitive` (`row_guid`, `table_name`, `field_code`) COMMENT '确保同一记录同一字段只有一条加密记录',
    KEY `idx_row_guid` (`row_guid`) COMMENT '记录唯一标识索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='敏感数据表-全局敏感信息加密存储';

CREATE TABLE `sys_role`
(
    `id`          BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `role_code`   VARCHAR(50)           NOT NULL COMMENT '角色编码,如SUPER_ADMIN,ROLE_USER,ROLE_VIP_MONTHLY',
    `role_name`   VARCHAR(50)           NOT NULL COMMENT '角色名称,如普通用户,月度会员',
    `role_type`   TINYINT               NOT NULL DEFAULT 0 COMMENT '角色类型:0前台角色,1后台角色',
    `data_scope`  INT                   NOT NULL COMMENT '角色权限范围(1:所有数据权限 2:自定义数据权限 3:仅本人数据权限)',
    `priority`    INT                   NOT NULL DEFAULT 0 COMMENT '优先级,数字越大优先级越高',
    `create_by`   BIGINT                         DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   BIGINT                         DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT               NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`     INT                   NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`      VARCHAR(500)                   DEFAULT NULL COMMENT '备注说明',
    `sort`        INT                   NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`      TINYINT               NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`  JSON                           DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_role_code` (`role_code`) COMMENT '角色编码唯一索引',
    KEY `idx_role_type` (`role_type`) COMMENT '角色类型索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='角色表-定义系统中的所有角色';

-- 补充系统内置角色标识(0: 非系统内部角色，1: 系统内部角色，禁止删除)
ALTER TABLE `sys_role`
    ADD COLUMN `is_builtin` TINYINT NOT NULL DEFAULT 0 COMMENT '系统内置角色标识:0非系统内部角色,1系统内部角色' AFTER `id`;

-- 初始化超级管理员角色
INSERT INTO `sys_role` (`id`, `is_builtin`, `role_code`, `role_name`, `role_type`, `data_scope`, `priority`, `remark`,
                        `status`)
VALUES (1, 1, 'SUPER_ADMIN', '超级管理员', 1, 1, 1000, '系统最高权限角色', 0);

-- 初始化普通用户角色
INSERT INTO `sys_role` (`id`, `is_builtin`, `role_code`, `role_name`, `role_type`, `data_scope`, `priority`, `remark`,
                        `status`)
VALUES (2, 1, 'ROLE_USER', '普通用户', 0, 3, 100, '默认普通用户角色', 0);

CREATE TABLE `sys_permission`
(
    `id`              BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `permission_code` VARCHAR(100)          NOT NULL COMMENT '权限编码,如content:create,ai:chat',
    `permission_name` VARCHAR(100)          NOT NULL COMMENT '权限名称',
    `permission_type` VARCHAR(20)           NOT NULL COMMENT '权限类型:menu菜单,button按钮,api接口',
    `parent_id`       BIGINT                NOT NULL DEFAULT 0 COMMENT '父权限ID,支持权限树',
    `resource_path`   VARCHAR(200)                   DEFAULT NULL COMMENT '资源路径',
    `module_name`     VARCHAR(50)                    DEFAULT NULL COMMENT '所属模块,如知识库模块,博客模块',
    `create_by`       BIGINT                         DEFAULT NULL COMMENT '创建人ID',
    `create_time`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT                         DEFAULT NULL COMMENT '更新人ID',
    `update_time`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT               NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`         INT                   NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`          VARCHAR(500)                   DEFAULT NULL COMMENT '备注说明',
    `sort`            INT                   NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`          TINYINT               NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`      JSON                           DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_permission_code` (`permission_code`) COMMENT '权限编码唯一索引',
    KEY `idx_parent_id` (`parent_id`) COMMENT '父权限索引',
    KEY `idx_module_name` (`module_name`) COMMENT '模块名称索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='权限资源表-定义系统权限点';

CREATE TABLE `sys_menu`
(
    `id`             BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `menu_name`      VARCHAR(50)           NOT NULL COMMENT '菜单名称',
    `parent_id`      BIGINT                NOT NULL DEFAULT 0 COMMENT '父菜单ID,根菜单为0',
    `menu_type`      CHAR(1)               NOT NULL COMMENT '菜单类型:M目录,C菜单,F按钮',
    `route_path`     VARCHAR(200)                   DEFAULT NULL COMMENT '路由路径',
    `component_path` VARCHAR(200)                   DEFAULT NULL COMMENT '组件路径',
    `menu_icon`      VARCHAR(100)                   DEFAULT NULL COMMENT '菜单图标',
    `is_external`    TINYINT               NOT NULL DEFAULT 0 COMMENT '是否外链:0否,1是',
    `is_cached`      TINYINT               NOT NULL DEFAULT 0 COMMENT '是否缓存:0否,1是',
    `is_visible`     TINYINT               NOT NULL DEFAULT 1 COMMENT '是否可见:0隐藏,1显示',
    `create_by`      BIGINT                         DEFAULT NULL COMMENT '创建人ID',
    `create_time`    DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      BIGINT                         DEFAULT NULL COMMENT '更新人ID',
    `update_time`    DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT               NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`        INT                   NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`         VARCHAR(500)                   DEFAULT NULL COMMENT '备注说明',
    `sort`           INT                   NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`         TINYINT               NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`     JSON                           DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`) COMMENT '父菜单索引',
    KEY `idx_menu_type` (`menu_type`) COMMENT '菜单类型索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='菜单表-前端路由菜单配置';

CREATE TABLE `sys_user_role`
(
    `id`          BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `user_id`     BIGINT                NOT NULL COMMENT '用户ID',
    `role_id`     BIGINT                NOT NULL COMMENT '角色ID',
    `valid_from`  DATETIME                       DEFAULT NULL COMMENT '有效开始时间',
    `valid_until` DATETIME                       DEFAULT NULL COMMENT '有效结束时间,用于临时授权',
    `create_by`   BIGINT                         DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_user_role` (`user_id`, `role_id`) COMMENT '用户角色唯一索引',
    KEY `idx_user` (`user_id`) COMMENT '用户索引',
    KEY `idx_role` (`role_id`) COMMENT '角色索引',
    KEY `idx_valid_until` (`valid_until`) COMMENT '有效期索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户角色关联表-用户与角色多对多关系';

CREATE TABLE `sys_role_permission`
(
    `id`            BIGINT   NOT NULL COMMENT '主键ID',
    `role_id`       BIGINT   NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT   NOT NULL COMMENT '权限ID',
    `create_by`     BIGINT            DEFAULT NULL COMMENT '创建人ID',
    `create_time`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_role_permission` (`role_id`, `permission_id`) COMMENT '角色权限唯一索引',
    KEY `idx_role` (`role_id`) COMMENT '角色索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='角色权限关联表-角色与权限多对多关系';

CREATE TABLE `mbr_level`
(
    `id`             BIGINT      NOT NULL COMMENT '主键ID',
    `level_code`     VARCHAR(50) NOT NULL COMMENT '等级编码,如FREE,MONTHLY,YEARLY,LIFETIME',
    `level_name`     VARCHAR(50) NOT NULL COMMENT '等级名称,如免费用户,月度会员',
    `level_benefits` JSON                 DEFAULT NULL COMMENT '权益配置,JSON格式存储各项权益限制',
    `priority`       INT         NOT NULL DEFAULT 0 COMMENT '优先级,数字越大权限越高',
    `create_by`      BIGINT               DEFAULT NULL COMMENT '创建人ID',
    `create_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      BIGINT               DEFAULT NULL COMMENT '更新人ID',
    `update_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`        INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`         VARCHAR(500)         DEFAULT NULL COMMENT '备注说明',
    `sort`           INT         NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`         TINYINT     NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`     JSON                 DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_level_code` (`level_code`) COMMENT '等级编码唯一索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='会员等级表-定义会员等级体系及权益';

CREATE TABLE `mbr_plan`
(
    `id`              BIGINT       NOT NULL COMMENT '主键ID',
    `plan_code`       VARCHAR(50)  NOT NULL COMMENT '计划编码,如PLAN_MONTH_29',
    `plan_name`       VARCHAR(100) NOT NULL COMMENT '计划名称',
    `level_id`        BIGINT       NOT NULL COMMENT '关联的会员等级ID',
    `duration_value`  INT          NOT NULL COMMENT '有效时长数值',
    `duration_unit`   VARCHAR(20)  NOT NULL COMMENT '时长单位:month,quarter,year,lifetime',
    `original_price`  BIGINT       NOT NULL COMMENT '原价,单位为分',
    `current_price`   BIGINT       NOT NULL COMMENT '现价,单位为分',
    `discount`        DECIMAL(3, 2)         DEFAULT NULL COMMENT '折扣,如0.8表示8折',
    `plan_desc`       VARCHAR(500)          DEFAULT NULL COMMENT '计划描述',
    `is_renewable`    TINYINT      NOT NULL DEFAULT 1 COMMENT '是否可续费:0否,1是',
    `is_published`    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否上架:0否,1是',
    `sale_start_time` DATETIME              DEFAULT NULL COMMENT '销售开始时间',
    `sale_end_time`   DATETIME              DEFAULT NULL COMMENT '销售结束时间',
    `create_by`       BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`          VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    `sort`            INT          NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`      JSON                  DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_plan_code` (`plan_code`) COMMENT '计划编码唯一索引',
    KEY `idx_level_id` (`level_id`) COMMENT '会员等级索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='订阅计划表-定义可售卖的订阅套餐';

CREATE TABLE `mbr_subscription`
(
    `id`                  BIGINT   NOT NULL COMMENT '主键ID',
    `user_id`             BIGINT   NOT NULL COMMENT '用户ID',
    `level_id`            BIGINT   NOT NULL COMMENT '会员等级ID',
    `plan_id`             BIGINT            DEFAULT NULL COMMENT '订阅计划ID,赠送会员可为NULL',
    `subscription_status` TINYINT  NOT NULL DEFAULT 0 COMMENT '订阅状态:0试用中,1正常,2已过期,3已取消,4已退款',
    `start_time`          DATETIME NOT NULL COMMENT '订阅开始时间',
    `expire_time`         DATETIME NOT NULL COMMENT '订阅到期时间',
    `is_auto_renew`       TINYINT  NOT NULL DEFAULT 0 COMMENT '是否自动续费:0否,1是',
    `auto_renew_plan_id`  BIGINT            DEFAULT NULL COMMENT '自动续费计划ID',
    `cancel_time`         DATETIME          DEFAULT NULL COMMENT '取消时间',
    `cancel_reason`       VARCHAR(500)      DEFAULT NULL COMMENT '取消原因',
    `create_by`           BIGINT            DEFAULT NULL COMMENT '创建人ID',
    `create_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`           BIGINT            DEFAULT NULL COMMENT '更新人ID',
    `update_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`             INT      NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`              VARCHAR(500)      DEFAULT NULL COMMENT '备注说明',
    PRIMARY KEY (`id`),
    KEY `idx_user_status` (`user_id`, `subscription_status`) COMMENT '用户状态联合索引',
    KEY `idx_expire` (`expire_time`) COMMENT '到期时间索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户订阅表-记录用户订阅状态';

CREATE TABLE `mbr_order`
(
    `id`              BIGINT      NOT NULL COMMENT '主键ID',
    `order_no`        VARCHAR(64) NOT NULL COMMENT '订单号,格式:ORD+时间戳+随机数',
    `user_id`         BIGINT      NOT NULL COMMENT '用户ID',
    `plan_id`         BIGINT      NOT NULL COMMENT '订阅计划ID',
    `order_amount`    BIGINT      NOT NULL COMMENT '订单金额,单位为分',
    `discount_amount` BIGINT      NOT NULL DEFAULT 0 COMMENT '优惠金额,单位为分',
    `coupon_id`       BIGINT               DEFAULT NULL COMMENT '优惠券ID',
    `actual_amount`   BIGINT      NOT NULL COMMENT '实付金额,单位为分',
    `order_status`    TINYINT     NOT NULL DEFAULT 0 COMMENT '订单状态:0待支付,1已支付,2已取消,3已关闭,4已退款',
    `order_source`    VARCHAR(20)          DEFAULT NULL COMMENT '订单来源:web,app,h5',
    `expire_time`     DATETIME    NOT NULL COMMENT '订单过期时间',
    `pay_time`        DATETIME             DEFAULT NULL COMMENT '支付完成时间',
    `cancel_time`     DATETIME             DEFAULT NULL COMMENT '取消时间',
    `cancel_reason`   VARCHAR(500)         DEFAULT NULL COMMENT '取消原因',
    `create_by`       BIGINT               DEFAULT NULL COMMENT '创建人ID',
    `create_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT               DEFAULT NULL COMMENT '更新人ID',
    `update_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`         INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`          VARCHAR(500)         DEFAULT NULL COMMENT '备注说明',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_order_no` (`order_no`) COMMENT '订单号唯一索引',
    KEY `idx_user_status` (`user_id`, `order_status`) COMMENT '用户状态联合索引',
    KEY `idx_expire` (`expire_time`) COMMENT '过期时间索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='订单表-记录用户购买订单';

CREATE TABLE `mbr_pay_channel`
(
    `id`           BIGINT       NOT NULL COMMENT '主键ID',
    `channel_code` VARCHAR(50)  NOT NULL COMMENT '渠道编码,如WXPAY,ALIPAY,BALANCE',
    `channel_name` VARCHAR(100) NOT NULL COMMENT '渠道名称',
    `channel_type` VARCHAR(50)  NOT NULL COMMENT '渠道类型,如第三方支付,钱包余额',
    `merchant_id`  VARCHAR(100)          DEFAULT NULL COMMENT '商户号',
    `app_id`       VARCHAR(100)          DEFAULT NULL COMMENT '应用ID',
    `api_key`      VARCHAR(200)          DEFAULT NULL COMMENT 'API密钥,加密存储',
    `api_secret`   VARCHAR(200)          DEFAULT NULL COMMENT 'API密钥,加密存储',
    `api_url`      VARCHAR(500)          DEFAULT NULL COMMENT 'API接口地址',
    `notify_url`   VARCHAR(500)          DEFAULT NULL COMMENT '异步通知回调地址',
    `cert_path`    VARCHAR(500)          DEFAULT NULL COMMENT '证书路径',
    `fee_rate`     DECIMAL(5, 4)         DEFAULT NULL COMMENT '手续费费率',
    `is_enabled`   TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用:0否,1是',
    `priority`     INT          NOT NULL DEFAULT 0 COMMENT '优先级',
    `create_by`    BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`      INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`       VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_channel_code` (`channel_code`) COMMENT '渠道编码唯一索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='支付渠道配置表-存储支付渠道信息';

CREATE TABLE `mbr_payment_order`
(
    `id`                   BIGINT      NOT NULL COMMENT '主键ID',
    `payment_no`           VARCHAR(64) NOT NULL COMMENT '支付流水号,格式:PAY+时间戳+随机数',
    `order_id`             BIGINT      NOT NULL COMMENT '关联的业务订单ID',
    `user_id`              BIGINT      NOT NULL COMMENT '用户ID',
    `channel_code`         VARCHAR(50) NOT NULL COMMENT '支付渠道编码',
    `payment_amount`       BIGINT      NOT NULL COMMENT '支付金额,单位为分',
    `payment_status`       TINYINT     NOT NULL DEFAULT 0 COMMENT '支付状态:0待支付,1支付中,2支付成功,3支付失败,4已关闭',
    `third_party_order_no` VARCHAR(100)         DEFAULT NULL COMMENT '第三方支付订单号',
    `third_party_user_id`  VARCHAR(100)         DEFAULT NULL COMMENT '第三方用户标识',
    `qr_code`              VARCHAR(500)         DEFAULT NULL COMMENT '支付二维码内容',
    `expire_time`          DATETIME    NOT NULL COMMENT '支付过期时间',
    `success_time`         DATETIME             DEFAULT NULL COMMENT '支付成功时间',
    `fail_reason`          VARCHAR(500)         DEFAULT NULL COMMENT '支付失败原因',
    `create_by`            BIGINT               DEFAULT NULL COMMENT '创建人ID',
    `create_time`          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            BIGINT               DEFAULT NULL COMMENT '更新人ID',
    `update_time`          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`              TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`              INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`               VARCHAR(500)         DEFAULT NULL COMMENT '备注说明',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_payment_no` (`payment_no`) COMMENT '支付流水号唯一索引',
    UNIQUE KEY `uni_third_order` (`third_party_order_no`) COMMENT '第三方订单号唯一索引',
    KEY `idx_order` (`order_id`) COMMENT '订单索引',
    KEY `idx_user_status` (`user_id`, `payment_status`) COMMENT '用户状态联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='支付订单表-记录支付流水';

CREATE TABLE `mbr_payment_event`
(
    `id`            BIGINT      NOT NULL COMMENT '主键ID',
    `payment_id`    BIGINT      NOT NULL COMMENT '关联的支付订单ID',
    `event_type`    VARCHAR(50) NOT NULL COMMENT '事件类型:CREATE_ORDER,CALL_GATEWAY,RECEIVE_NOTIFY,PAY_SUCCESS,PAY_FAIL',
    `event_status`  VARCHAR(20) NOT NULL COMMENT '事件状态:processing,success,failed',
    `request_data`  TEXT                 DEFAULT NULL COMMENT '请求数据,JSON格式',
    `response_data` TEXT                 DEFAULT NULL COMMENT '响应数据,JSON格式',
    `event_time`    DATETIME    NOT NULL COMMENT '事件发生时间',
    `process_time`  INT                  DEFAULT NULL COMMENT '事件处理时长,单位毫秒',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_payment` (`payment_id`) COMMENT '支付订单索引',
    KEY `idx_type_time` (`event_type`, `event_time`) COMMENT '事件类型时间联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='支付事件表-记录支付流程事件';

CREATE TABLE `mbr_payment_notify`
(
    `id`                   BIGINT      NOT NULL COMMENT '主键ID',
    `payment_id`           BIGINT               DEFAULT NULL COMMENT '关联的支付订单ID',
    `channel_code`         VARCHAR(50) NOT NULL COMMENT '支付渠道编码',
    `third_party_order_no` VARCHAR(100)         DEFAULT NULL COMMENT '第三方订单号',
    `notify_type`          VARCHAR(50) NOT NULL COMMENT '通知类型:PAY_SUCCESS,REFUND_SUCCESS',
    `raw_request`          TEXT        NOT NULL COMMENT '原始请求数据',
    `raw_response`         TEXT                 DEFAULT NULL COMMENT '原始响应数据',
    `notify_status`        VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '通知处理状态:pending,success,failed',
    `process_result`       VARCHAR(500)         DEFAULT NULL COMMENT '处理结果说明',
    `retry_count`          INT         NOT NULL DEFAULT 0 COMMENT '重试次数',
    `notify_time`          DATETIME    NOT NULL COMMENT '通知时间',
    `process_time`         DATETIME             DEFAULT NULL COMMENT '处理时间',
    `create_time`          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_third_order` (`third_party_order_no`) COMMENT '第三方订单号索引',
    KEY `idx_status` (`notify_status`) COMMENT '通知状态索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='支付异步通知表-记录支付网关回调';

CREATE TABLE `mbr_refund`
(
    `id`                    BIGINT      NOT NULL COMMENT '主键ID',
    `refund_no`             VARCHAR(64) NOT NULL COMMENT '退款单号,格式:REFUND+时间戳',
    `order_id`              BIGINT      NOT NULL COMMENT '原订单ID',
    `payment_id`            BIGINT      NOT NULL COMMENT '原支付订单ID',
    `user_id`               BIGINT      NOT NULL COMMENT '用户ID',
    `refund_amount`         BIGINT      NOT NULL COMMENT '退款金额,单位为分',
    `refund_reason`         VARCHAR(500)         DEFAULT NULL COMMENT '退款原因',
    `refund_type`           VARCHAR(20) NOT NULL COMMENT '退款类型:FULL全额,PARTIAL部分,CANCEL取消订单',
    `refund_status`         TINYINT     NOT NULL DEFAULT 0 COMMENT '退款状态:0待审核,1审核通过,2退款中,3退款成功,4退款失败,5已拒绝',
    `third_party_refund_no` VARCHAR(100)         DEFAULT NULL COMMENT '第三方退款单号',
    `apply_time`            DATETIME    NOT NULL COMMENT '申请时间',
    `audit_time`            DATETIME             DEFAULT NULL COMMENT '审核时间',
    `audit_by`              BIGINT               DEFAULT NULL COMMENT '审核人ID',
    `success_time`          DATETIME             DEFAULT NULL COMMENT '退款成功时间',
    `fail_reason`           VARCHAR(500)         DEFAULT NULL COMMENT '退款失败原因',
    `create_by`             BIGINT               DEFAULT NULL COMMENT '创建人ID',
    `create_time`           DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`             BIGINT               DEFAULT NULL COMMENT '更新人ID',
    `update_time`           DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`               TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`               INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`                VARCHAR(500)         DEFAULT NULL COMMENT '备注说明',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_refund_no` (`refund_no`) COMMENT '退款单号唯一索引',
    KEY `idx_order` (`order_id`) COMMENT '订单索引',
    KEY `idx_user_status` (`user_id`, `refund_status`) COMMENT '用户状态联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='退款记录表-记录退款申请和处理';

CREATE TABLE `mbr_wallet`
(
    `id`             BIGINT   NOT NULL COMMENT '主键ID',
    `user_id`        BIGINT   NOT NULL COMMENT '用户ID',
    `balance`        BIGINT   NOT NULL DEFAULT 0 COMMENT '余额,单位为分',
    `freeze_balance` BIGINT   NOT NULL DEFAULT 0 COMMENT '冻结余额,单位为分',
    `gift_balance`   BIGINT   NOT NULL DEFAULT 0 COMMENT '赠送余额,单位为分',
    `total_recharge` BIGINT   NOT NULL DEFAULT 0 COMMENT '累计充值金额',
    `total_consume`  BIGINT   NOT NULL DEFAULT 0 COMMENT '累计消费金额',
    `version`        INT      NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `update_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_user` (`user_id`) COMMENT '用户唯一索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户钱包表-管理用户虚拟账户余额';

CREATE TABLE `mbr_wallet_log`
(
    `id`                    BIGINT      NOT NULL COMMENT '主键ID',
    `user_id`               BIGINT      NOT NULL COMMENT '用户ID',
    `wallet_id`             BIGINT      NOT NULL COMMENT '钱包ID',
    `change_type`           VARCHAR(20) NOT NULL COMMENT '变动类型:RECHARGE,CONSUME,REFUND,GIFT,FREEZE,UNFREEZE',
    `change_amount`         BIGINT      NOT NULL COMMENT '变动金额,单位为分,正数增加负数减少',
    `balance_before`        BIGINT      NOT NULL COMMENT '变动前余额',
    `balance_after`         BIGINT      NOT NULL COMMENT '变动后余额',
    `freeze_balance_before` BIGINT      NOT NULL DEFAULT 0 COMMENT '变动前冻结余额',
    `freeze_balance_after`  BIGINT      NOT NULL DEFAULT 0 COMMENT '变动后冻结余额',
    `biz_type`              VARCHAR(50)          DEFAULT NULL COMMENT '业务类型',
    `biz_id`                VARCHAR(64)          DEFAULT NULL COMMENT '业务单号',
    `remark`                VARCHAR(500)         DEFAULT NULL COMMENT '备注',
    `create_time`           DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_time` (`user_id`, `create_time`) COMMENT '用户时间联合索引',
    KEY `idx_biz` (`biz_type`, `biz_id`) COMMENT '业务类型单号联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='钱包流水表-记录每笔资金变动';

CREATE TABLE `content_space`
(
    `id`              BIGINT       NOT NULL COMMENT '主键ID',
    `space_code`      VARCHAR(50)  NOT NULL COMMENT '空间编码,全局唯一',
    `space_name`      VARCHAR(100) NOT NULL COMMENT '空间名称',
    `space_desc`      VARCHAR(500)          DEFAULT NULL COMMENT '空间描述',
    `cover_image`     VARCHAR(500)          DEFAULT NULL COMMENT '封面图URL',
    `owner_id`        BIGINT       NOT NULL COMMENT '空间拥有者用户ID',
    `space_type`      TINYINT      NOT NULL DEFAULT 0 COMMENT '空间类型:0个人知识库,1课程专栏,2视频专栏',
    `access_type`     TINYINT      NOT NULL DEFAULT 0 COMMENT '访问类型:0私有,1公开,2密码访问',
    `access_password` VARCHAR(100)          DEFAULT NULL COMMENT '访问密码,加密存储',
    `is_published`    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已发布:0否,1是',
    `publish_time`    DATETIME              DEFAULT NULL COMMENT '发布时间',
    `view_count`      BIGINT       NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `create_by`       BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`          VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    `sort`            INT          NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`      JSON                  DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_space_code` (`space_code`) COMMENT '空间编码唯一索引',
    KEY `idx_owner_type` (`owner_id`, `space_type`) COMMENT '所有者类型联合索引',
    KEY `idx_published` (`is_published`) COMMENT '发布状态索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='内容空间表-顶层内容组织单元';

CREATE TABLE `content_directory`
(
    `id`             BIGINT       NOT NULL COMMENT '主键ID',
    `space_id`       BIGINT       NOT NULL COMMENT '所属空间ID',
    `parent_id`      BIGINT       NOT NULL DEFAULT 0 COMMENT '父目录ID,根目录为0',
    `directory_name` VARCHAR(100) NOT NULL COMMENT '目录名称',
    `directory_path` VARCHAR(500)          DEFAULT NULL COMMENT '完整路径',
    `depth_level`    INT          NOT NULL DEFAULT 1 COMMENT '目录深度,从1开始',
    `create_by`      BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`        INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`         VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    `sort`           INT          NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`         TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`     JSON                  DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    KEY `idx_space_parent` (`space_id`, `parent_id`) COMMENT '空间父目录联合索引',
    KEY `idx_path` (`directory_path`(255)) COMMENT '路径索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='内容目录表-空间内的目录层级结构';

CREATE TABLE `content_document`
(
    `id`                    BIGINT       NOT NULL COMMENT '主键ID',
    `doc_guid`              VARCHAR(64)  NOT NULL COMMENT '全局唯一标识,UUID格式',
    `space_id`              BIGINT       NOT NULL COMMENT '所属空间ID',
    `directory_id`          BIGINT                DEFAULT NULL COMMENT '所属目录ID',
    `doc_title`             VARCHAR(200) NOT NULL COMMENT '文档标题',
    `doc_summary`           VARCHAR(500)          DEFAULT NULL COMMENT '文档摘要',
    `content_type`          VARCHAR(20)  NOT NULL COMMENT '内容类型:MARKDOWN,RICHTEXT,VIDEO,MIXED',
    `content_body`          LONGTEXT              DEFAULT NULL COMMENT '内容正文',
    `cover_image`           VARCHAR(500)          DEFAULT NULL COMMENT '封面图URL',
    `file_id`               BIGINT                DEFAULT NULL COMMENT '关联的文件ID',
    `access_type`           TINYINT      NOT NULL DEFAULT 0 COMMENT '访问类型:0继承空间,1自定义私有,2自定义公开',
    `is_paid`               TINYINT      NOT NULL DEFAULT 0 COMMENT '是否付费文档:0否,1是',
    `paid_amount`           BIGINT                DEFAULT NULL COMMENT '付费金额,单位为分',
    `doc_status`            TINYINT      NOT NULL DEFAULT 0 COMMENT '文档状态:0草稿,1已发布,2已下架',
    `publish_time`          DATETIME              DEFAULT NULL COMMENT '发布时间',
    `schedule_publish_time` DATETIME              DEFAULT NULL COMMENT '定时发布时间',
    `word_count`            INT          NOT NULL DEFAULT 0 COMMENT '字数统计',
    `read_duration`         INT          NOT NULL DEFAULT 0 COMMENT '预计阅读时长,单位分钟',
    `view_count`            BIGINT       NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `like_count`            BIGINT       NOT NULL DEFAULT 0 COMMENT '点赞数',
    `collect_count`         BIGINT       NOT NULL DEFAULT 0 COMMENT '收藏数',
    `comment_count`         BIGINT       NOT NULL DEFAULT 0 COMMENT '评论数',
    `share_count`           BIGINT       NOT NULL DEFAULT 0 COMMENT '分享次数',
    `seo_keywords`          VARCHAR(200)          DEFAULT NULL COMMENT 'SEO关键词',
    `seo_description`       VARCHAR(500)          DEFAULT NULL COMMENT 'SEO描述',
    `version_number`        INT          NOT NULL DEFAULT 1 COMMENT '当前版本号',
    `create_by`             BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`             BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`               TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`               INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`                VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    `sort`                  INT          NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`                TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`            JSON                  DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_doc_guid` (`doc_guid`) COMMENT '文档唯一标识索引',
    KEY `idx_space_status` (`space_id`, `doc_status`) COMMENT '空间状态联合索引',
    KEY `idx_directory` (`directory_id`) COMMENT '目录索引',
    KEY `idx_publish` (`publish_time`) COMMENT '发布时间索引',
    KEY `idx_paid_status` (`is_paid`, `doc_status`) COMMENT '付费状态联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='内容文档表-统一的内容文档';

CREATE TABLE `content_document_version`
(
    `id`             BIGINT   NOT NULL COMMENT '主键ID',
    `document_id`    BIGINT   NOT NULL COMMENT '文档ID',
    `version_number` INT      NOT NULL COMMENT '版本号,从1开始递增',
    `content_body`   LONGTEXT NOT NULL COMMENT '内容快照',
    `file_id`        BIGINT            DEFAULT NULL COMMENT '文件快照',
    `change_summary` VARCHAR(500)      DEFAULT NULL COMMENT '变更说明',
    `word_count`     INT      NOT NULL DEFAULT 0 COMMENT '字数统计',
    `created_by`     BIGINT            DEFAULT NULL COMMENT '创建人ID',
    `create_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_doc_version` (`document_id`, `version_number`) COMMENT '文档版本号唯一索引',
    KEY `idx_doc_time` (`document_id`, `create_time`) COMMENT '文档时间联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文档版本表-文档历史版本快照';

CREATE TABLE `content_tag`
(
    `id`          BIGINT      NOT NULL COMMENT '主键ID',
    `tag_name`    VARCHAR(50) NOT NULL COMMENT '标签名称',
    `tag_color`   VARCHAR(20)          DEFAULT NULL COMMENT '标签颜色,十六进制值',
    `tag_type`    TINYINT     NOT NULL DEFAULT 1 COMMENT '标签类型:0系统标签,1用户自定义标签',
    `usage_count` BIGINT      NOT NULL DEFAULT 0 COMMENT '使用次数',
    `creator_id`  BIGINT               DEFAULT NULL COMMENT '创建者ID',
    `create_by`   BIGINT               DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   BIGINT               DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`     INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`      VARCHAR(500)         DEFAULT NULL COMMENT '备注说明',
    `status`      TINYINT     NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_tag_name` (`tag_name`) COMMENT '标签名称唯一索引',
    KEY `idx_usage` (`usage_count`) COMMENT '使用次数索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='内容标签表-统一的标签词库';

CREATE TABLE `content_document_tag`
(
    `id`          BIGINT   NOT NULL COMMENT '主键ID',
    `document_id` BIGINT   NOT NULL COMMENT '文档ID',
    `tag_id`      BIGINT   NOT NULL COMMENT '标签ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_doc_tag` (`document_id`, `tag_id`) COMMENT '文档标签唯一索引',
    KEY `idx_tag` (`tag_id`) COMMENT '标签索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文档标签关联表-文档与标签多对多关系';

CREATE TABLE `content_category`
(
    `id`            BIGINT       NOT NULL COMMENT '主键ID',
    `category_code` VARCHAR(50)  NOT NULL COMMENT '分类编码',
    `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id`     BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类ID,一级分类为0',
    `category_icon` VARCHAR(200)          DEFAULT NULL COMMENT '分类图标',
    `category_desc` VARCHAR(500)          DEFAULT NULL COMMENT '分类描述',
    `create_by`     BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`       INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`        VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    `sort`          INT          NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`    JSON                  DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_code` (`category_code`) COMMENT '分类编码唯一索引',
    KEY `idx_parent` (`parent_id`) COMMENT '父分类索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='内容分类表-文档分类体系';

CREATE TABLE `content_action`
(
    `id`           BIGINT      NOT NULL COMMENT '主键ID',
    `user_id`      BIGINT      NOT NULL COMMENT '用户ID',
    `document_id`  BIGINT      NOT NULL COMMENT '文档ID',
    `action_type`  VARCHAR(20) NOT NULL COMMENT '行为类型:VIEW,LIKE,COLLECT,SHARE',
    `action_value` INT                  DEFAULT NULL COMMENT '行为值,如浏览时长秒数',
    `action_time`  DATETIME    NOT NULL COMMENT '行为时间',
    `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_user_doc_action` (`user_id`, `document_id`, `action_type`) COMMENT '用户文档行为唯一索引',
    KEY `idx_doc_action` (`document_id`, `action_type`) COMMENT '文档行为联合索引',
    KEY `idx_user_action` (`user_id`, `action_type`) COMMENT '用户行为联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户行为表-记录用户对内容的交互行为';

CREATE TABLE `content_comment`
(
    `id`               BIGINT   NOT NULL COMMENT '主键ID',
    `document_id`      BIGINT   NOT NULL COMMENT '文档ID',
    `user_id`          BIGINT   NOT NULL COMMENT '评论者ID',
    `parent_id`        BIGINT   NOT NULL DEFAULT 0 COMMENT '父评论ID,一级评论为0',
    `root_id`          BIGINT   NOT NULL DEFAULT 0 COMMENT '根评论ID,一级评论为0',
    `reply_to_user_id` BIGINT            DEFAULT NULL COMMENT '被回复者ID',
    `comment_content`  TEXT     NOT NULL COMMENT '评论内容',
    `comment_images`   JSON              DEFAULT NULL COMMENT '评论图片,JSON数组格式',
    `ip_address`       VARCHAR(50)       DEFAULT NULL COMMENT '评论IP',
    `like_count`       BIGINT   NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_status`   TINYINT  NOT NULL DEFAULT 1 COMMENT '评论状态:0待审核,1已发布,2已删除',
    `audit_time`       DATETIME          DEFAULT NULL COMMENT '审核时间',
    `audit_by`         BIGINT            DEFAULT NULL COMMENT '审核人ID',
    `create_by`        BIGINT            DEFAULT NULL COMMENT '创建人ID',
    `create_time`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        BIGINT            DEFAULT NULL COMMENT '更新人ID',
    `update_time`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`          INT      NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_doc_root` (`document_id`, `root_id`) COMMENT '文档根评论联合索引',
    KEY `idx_user` (`user_id`) COMMENT '用户索引',
    KEY `idx_parent` (`parent_id`) COMMENT '父评论索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='评论表-统一的评论系统';

CREATE TABLE `video_course`
(
    `id`              BIGINT       NOT NULL COMMENT '主键ID',
    `course_code`     VARCHAR(50)  NOT NULL COMMENT '课程编码,全局唯一',
    `space_id`        BIGINT       NOT NULL COMMENT '关联的内容空间ID',
    `course_name`     VARCHAR(200) NOT NULL COMMENT '课程名称',
    `course_subtitle` VARCHAR(200)          DEFAULT NULL COMMENT '课程副标题',
    `course_desc`     TEXT                  DEFAULT NULL COMMENT '课程描述',
    `cover_image`     VARCHAR(500)          DEFAULT NULL COMMENT '课程封面',
    `course_intro`    BIGINT                DEFAULT NULL COMMENT '课程介绍视频ID',
    `instructor_id`   BIGINT       NOT NULL COMMENT '讲师用户ID',
    `course_level`    TINYINT      NOT NULL DEFAULT 0 COMMENT '课程难度:0入门,1进阶,2高级',
    `course_category` VARCHAR(50)           DEFAULT NULL COMMENT '课程分类',
    `total_duration`  INT          NOT NULL DEFAULT 0 COMMENT '课程总时长,单位秒',
    `chapter_count`   INT          NOT NULL DEFAULT 0 COMMENT '章节数量',
    `video_count`     INT          NOT NULL DEFAULT 0 COMMENT '视频数量',
    `is_free`         TINYINT      NOT NULL DEFAULT 0 COMMENT '是否免费课程:0否,1是',
    `price`           BIGINT                DEFAULT NULL COMMENT '课程价格,单位为分',
    `original_price`  BIGINT                DEFAULT NULL COMMENT '原价',
    `enroll_count`    BIGINT       NOT NULL DEFAULT 0 COMMENT '报名人数',
    `completion_rate` DECIMAL(5, 2)         DEFAULT NULL COMMENT '完成率',
    `avg_rating`      DECIMAL(3, 2)         DEFAULT NULL COMMENT '平均评分',
    `review_count`    BIGINT       NOT NULL DEFAULT 0 COMMENT '评价数量',
    `is_published`    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已发布:0否,1是',
    `publish_time`    DATETIME              DEFAULT NULL COMMENT '发布时间',
    `create_by`       BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`          VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    `sort`            INT          NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`      JSON                  DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_course_code` (`course_code`) COMMENT '课程编码唯一索引',
    UNIQUE KEY `uni_space` (`space_id`) COMMENT '空间唯一索引',
    KEY `idx_published_category` (`is_published`, `course_category`) COMMENT '发布状态分类联合索引',
    KEY `idx_instructor` (`instructor_id`) COMMENT '讲师索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='视频课程表-视频课程专栏';

CREATE TABLE `video_chapter`
(
    `id`             BIGINT       NOT NULL COMMENT '主键ID',
    `course_id`      BIGINT       NOT NULL COMMENT '所属课程ID',
    `chapter_title`  VARCHAR(200) NOT NULL COMMENT '章节标题',
    `chapter_desc`   VARCHAR(500)          DEFAULT NULL COMMENT '章节描述',
    `chapter_order`  INT          NOT NULL COMMENT '章节序号,从1开始',
    `video_count`    INT          NOT NULL DEFAULT 0 COMMENT '本章视频数量',
    `total_duration` INT          NOT NULL DEFAULT 0 COMMENT '本章总时长,单位秒',
    `is_free`        TINYINT      NOT NULL DEFAULT 0 COMMENT '是否免费章节:0否,1是',
    `create_by`      BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`        INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`         VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    `sort`           INT          NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`         TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`     JSON                  DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    KEY `idx_course_order` (`course_id`, `chapter_order`) COMMENT '课程序号联合索引',
    KEY `idx_course` (`course_id`) COMMENT '课程索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='视频章节表-课程的章节结构';

CREATE TABLE `video_resource`
(
    `id`                  BIGINT       NOT NULL COMMENT '主键ID',
    `resource_guid`       VARCHAR(64)  NOT NULL COMMENT '全局唯一标识,UUID格式',
    `course_id`           BIGINT       NOT NULL COMMENT '所属课程ID',
    `chapter_id`          BIGINT       NOT NULL COMMENT '所属章节ID',
    `video_title`         VARCHAR(200) NOT NULL COMMENT '视频标题',
    `video_desc`          VARCHAR(500)          DEFAULT NULL COMMENT '视频描述',
    `video_order`         INT          NOT NULL COMMENT '视频序号,从1开始',
    `file_id`             BIGINT       NOT NULL COMMENT '原始视频文件ID',
    `cover_image`         VARCHAR(500)          DEFAULT NULL COMMENT '视频封面',
    `duration`            INT          NOT NULL DEFAULT 0 COMMENT '视频时长,单位秒',
    `original_resolution` VARCHAR(20)           DEFAULT NULL COMMENT '原始分辨率,如1920x1080',
    `transcode_status`    TINYINT      NOT NULL DEFAULT 0 COMMENT '转码状态:0待转码,1转码中,2转码完成,3转码失败',
    `is_free`             TINYINT      NOT NULL DEFAULT 0 COMMENT '是否免费试看:0否,1是',
    `view_count`          BIGINT       NOT NULL DEFAULT 0 COMMENT '播放次数',
    `like_count`          BIGINT       NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count`       BIGINT       NOT NULL DEFAULT 0 COMMENT '评论数',
    `danmaku_count`       BIGINT       NOT NULL DEFAULT 0 COMMENT '弹幕数',
    `create_by`           BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`           BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`             INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`              VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    `sort`                INT          NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`              TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`          JSON                  DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_guid` (`resource_guid`) COMMENT '资源唯一标识索引',
    KEY `idx_chapter_order` (`chapter_id`, `video_order`) COMMENT '章节序号联合索引',
    KEY `idx_course` (`course_id`) COMMENT '课程索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='视频资源表-具体的视频文件资源';

CREATE TABLE `video_quality`
(
    `id`             BIGINT      NOT NULL COMMENT '主键ID',
    `video_id`       BIGINT      NOT NULL COMMENT '视频资源ID',
    `quality_level`  VARCHAR(20) NOT NULL COMMENT '清晰度级别:360P,480P,720P,1080P,4K',
    `file_id`        BIGINT      NOT NULL COMMENT '转码后的文件ID',
    `resolution`     VARCHAR(20) NOT NULL COMMENT '分辨率,如1280x720',
    `bitrate`        INT         NOT NULL COMMENT '码率,单位kbps',
    `file_size`      BIGINT      NOT NULL COMMENT '文件大小,单位字节',
    `codec_name`     VARCHAR(20)          DEFAULT NULL COMMENT '编码格式,如H264,H265',
    `transcode_time` DATETIME             DEFAULT NULL COMMENT '转码完成时间',
    `create_by`      BIGINT               DEFAULT NULL COMMENT '创建人ID',
    `create_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      BIGINT               DEFAULT NULL COMMENT '更新人ID',
    `update_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`        INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `status`         TINYINT     NOT NULL DEFAULT 0 COMMENT '状态:0转码中,1可用,2失败',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_video_quality` (`video_id`, `quality_level`) COMMENT '视频清晰度唯一索引',
    KEY `idx_video` (`video_id`) COMMENT '视频索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='视频清晰度表-转码后的多清晰度版本';

CREATE TABLE `video_play_record`
(
    `id`              BIGINT   NOT NULL COMMENT '主键ID',
    `user_id`         BIGINT   NOT NULL COMMENT '用户ID',
    `video_id`        BIGINT   NOT NULL COMMENT '视频资源ID',
    `course_id`       BIGINT            DEFAULT NULL COMMENT '课程ID',
    `play_progress`   INT      NOT NULL DEFAULT 0 COMMENT '播放进度,单位秒',
    `total_duration`  INT      NOT NULL COMMENT '视频总时长,单位秒',
    `play_percentage` DECIMAL(5, 2)     DEFAULT NULL COMMENT '播放百分比',
    `quality_level`   VARCHAR(20)       DEFAULT NULL COMMENT '当前清晰度',
    `is_completed`    TINYINT  NOT NULL DEFAULT 0 COMMENT '是否已完成:0否,1是',
    `last_play_time`  DATETIME NOT NULL COMMENT '最后播放时间',
    `update_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_user_video` (`user_id`, `video_id`) COMMENT '用户视频唯一索引',
    KEY `idx_user_course` (`user_id`, `course_id`) COMMENT '用户课程联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='播放记录表-记录用户视频播放进度';

CREATE TABLE `ai_model_config`
(
    `id`                 BIGINT       NOT NULL COMMENT '主键ID',
    `model_code`         VARCHAR(50)  NOT NULL COMMENT '模型编码,如GPT4,CLAUDE3,QWEN_MAX',
    `model_name`         VARCHAR(100) NOT NULL COMMENT '模型显示名称',
    `provider`           VARCHAR(50)  NOT NULL COMMENT '供应商:OPENAI,ANTHROPIC,ALIBABA,ZHIPU',
    `model_type`         VARCHAR(20)  NOT NULL COMMENT '模型类型:CHAT,IMAGE,VIDEO,EMBEDDING',
    `api_endpoint`       VARCHAR(500)          DEFAULT NULL COMMENT 'API接口地址',
    `api_key`            VARCHAR(200)          DEFAULT NULL COMMENT 'API密钥,加密存储',
    `api_version`        VARCHAR(20)           DEFAULT NULL COMMENT 'API版本',
    `model_capabilities` JSON                  DEFAULT NULL COMMENT '模型能力,JSON格式',
    `context_window`     INT                   DEFAULT NULL COMMENT '上下文窗口大小',
    `max_tokens`         INT                   DEFAULT NULL COMMENT '最大输出token数',
    `temperature`        DECIMAL(3, 2)         DEFAULT 0.70 COMMENT '默认温度参数',
    `pricing_input`      BIGINT                DEFAULT NULL COMMENT '输入定价,每千token价格,单位分',
    `pricing_output`     BIGINT                DEFAULT NULL COMMENT '输出定价,每千token价格,单位分',
    `is_enabled`         TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用:0否,1是',
    `priority`           INT          NOT NULL DEFAULT 0 COMMENT '优先级',
    `create_by`          BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`          BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`            TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`            INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`             VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    `status`             TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    `extra_data`         JSON                  DEFAULT NULL COMMENT '扩展数据',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_model_code` (`model_code`) COMMENT '模型编码唯一索引',
    KEY `idx_provider_type` (`provider`, `model_type`) COMMENT '供应商类型联合索引',
    KEY `idx_enabled` (`is_enabled`) COMMENT '启用状态索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='AI模型配置表-配置可用的AI模型';

CREATE TABLE `ai_conversation`
(
    `id`                  BIGINT      NOT NULL COMMENT '主键ID',
    `conversation_guid`   VARCHAR(64) NOT NULL COMMENT '全局唯一标识,UUID格式',
    `user_id`             BIGINT      NOT NULL COMMENT '用户ID',
    `conversation_title`  VARCHAR(200)         DEFAULT NULL COMMENT '会话标题',
    `conversation_type`   VARCHAR(20) NOT NULL COMMENT '会话类型:GENERAL,KB_QA,IMAGE_GEN,VIDEO_GEN',
    `model_code`          VARCHAR(50) NOT NULL COMMENT '使用的模型编码',
    `kb_space_id`         BIGINT               DEFAULT NULL COMMENT '关联的知识库空间ID',
    `system_prompt`       TEXT                 DEFAULT NULL COMMENT '系统提示词',
    `conversation_status` TINYINT     NOT NULL DEFAULT 0 COMMENT '会话状态:0进行中,1已结束,2已删除',
    `message_count`       INT         NOT NULL DEFAULT 0 COMMENT '消息数量',
    `total_tokens`        BIGINT      NOT NULL DEFAULT 0 COMMENT '总消耗token数',
    `total_cost`          BIGINT      NOT NULL DEFAULT 0 COMMENT '总消耗金额,单位分',
    `last_message_time`   DATETIME             DEFAULT NULL COMMENT '最后消息时间',
    `create_by`           BIGINT               DEFAULT NULL COMMENT '创建人ID',
    `create_time`         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`           BIGINT               DEFAULT NULL COMMENT '更新人ID',
    `update_time`         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`             INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `sort`                INT         NOT NULL DEFAULT 0 COMMENT '排序字段',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_guid` (`conversation_guid`) COMMENT '会话唯一标识索引',
    KEY `idx_user_status` (`user_id`, `conversation_status`) COMMENT '用户状态联合索引',
    KEY `idx_kb` (`kb_space_id`) COMMENT '知识库空间索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='AI对话会话表-用户与AI的对话会话';

CREATE TABLE `ai_message`
(
    `id`                BIGINT      NOT NULL COMMENT '主键ID',
    `conversation_id`   BIGINT      NOT NULL COMMENT '对话会话ID',
    `message_role`      VARCHAR(20) NOT NULL COMMENT '消息角色:USER,ASSISTANT,SYSTEM',
    `message_content`   TEXT        NOT NULL COMMENT '消息内容',
    `message_type`      VARCHAR(20) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型:TEXT,IMAGE,VIDEO,FUNCTION',
    `media_urls`        JSON                 DEFAULT NULL COMMENT '媒体URL数组',
    `prompt_tokens`     INT                  DEFAULT NULL COMMENT '输入token数',
    `completion_tokens` INT                  DEFAULT NULL COMMENT '输出token数',
    `total_tokens`      INT                  DEFAULT NULL COMMENT '总token数',
    `message_cost`      BIGINT               DEFAULT NULL COMMENT '消息成本,单位分',
    `model_code`        VARCHAR(50)          DEFAULT NULL COMMENT '使用的模型',
    `model_params`      JSON                 DEFAULT NULL COMMENT '模型参数',
    `function_call`     JSON                 DEFAULT NULL COMMENT '函数调用信息',
    `kb_references`     JSON                 DEFAULT NULL COMMENT '知识库引用',
    `message_status`    TINYINT     NOT NULL DEFAULT 1 COMMENT '消息状态:0发送中,1成功,2失败',
    `error_message`     VARCHAR(500)         DEFAULT NULL COMMENT '错误信息',
    `create_time`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_conv_time` (`conversation_id`, `create_time`) COMMENT '会话时间联合索引',
    KEY `idx_conv` (`conversation_id`) COMMENT '会话索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='AI消息表-会话中的每条消息';

CREATE TABLE `ai_prompt_template`
(
    `id`                BIGINT       NOT NULL COMMENT '主键ID',
    `template_code`     VARCHAR(50)  NOT NULL COMMENT '模板编码',
    `template_name`     VARCHAR(100) NOT NULL COMMENT '模板名称',
    `template_content`  TEXT         NOT NULL COMMENT '模板内容,支持变量占位符',
    `template_type`     VARCHAR(20)  NOT NULL COMMENT '模板类型:SYSTEM,USER',
    `template_category` VARCHAR(50)           DEFAULT NULL COMMENT '模板分类:写作助手,代码助手,翻译助手',
    `applicable_models` JSON                  DEFAULT NULL COMMENT '适用模型数组',
    `is_system`         TINYINT      NOT NULL DEFAULT 0 COMMENT '是否系统模板:0否,1是',
    `is_public`         TINYINT      NOT NULL DEFAULT 0 COMMENT '是否公开:0否,1是',
    `creator_id`        BIGINT                DEFAULT NULL COMMENT '创建者ID',
    `usage_count`       BIGINT       NOT NULL DEFAULT 0 COMMENT '使用次数',
    `like_count`        BIGINT       NOT NULL DEFAULT 0 COMMENT '点赞数',
    `create_by`         BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`           INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`            VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    `status`            TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_code` (`template_code`) COMMENT '模板编码唯一索引',
    KEY `idx_category_public` (`template_category`, `is_public`) COMMENT '分类公开联合索引',
    KEY `idx_creator` (`creator_id`) COMMENT '创建者索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='提示词模板表-系统和用户自定义模板';

CREATE TABLE `ai_quota`
(
    `id`                BIGINT      NOT NULL COMMENT '主键ID',
    `user_id`           BIGINT      NOT NULL COMMENT '用户ID',
    `level_id`          BIGINT      NOT NULL COMMENT '会员等级ID',
    `chat_quota_total`  INT         NOT NULL DEFAULT 0 COMMENT '对话总配额',
    `chat_quota_used`   INT         NOT NULL DEFAULT 0 COMMENT '对话已使用配额',
    `image_quota_total` INT         NOT NULL DEFAULT 0 COMMENT '图像生成总配额',
    `image_quota_used`  INT         NOT NULL DEFAULT 0 COMMENT '图像生成已使用配额',
    `video_quota_total` INT         NOT NULL DEFAULT 0 COMMENT '视频生成总配额',
    `video_quota_used`  INT         NOT NULL DEFAULT 0 COMMENT '视频生成已使用配额',
    `reset_cycle`       VARCHAR(20) NOT NULL COMMENT '重置周期:DAILY,MONTHLY',
    `last_reset_time`   DATETIME    NOT NULL COMMENT '上次重置时间',
    `next_reset_time`   DATETIME    NOT NULL COMMENT '下次重置时间',
    `update_time`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_user` (`user_id`) COMMENT '用户唯一索引',
    KEY `idx_next_reset` (`next_reset_time`) COMMENT '下次重置时间索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户AI配额表-记录用户AI使用配额';

CREATE TABLE `ai_consumption`
(
    `id`                BIGINT      NOT NULL COMMENT '主键ID',
    `user_id`           BIGINT      NOT NULL COMMENT '用户ID',
    `conversation_id`   BIGINT               DEFAULT NULL COMMENT '会话ID',
    `message_id`        BIGINT               DEFAULT NULL COMMENT '消息ID',
    `model_code`        VARCHAR(50) NOT NULL COMMENT '模型编码',
    `consumption_type`  VARCHAR(20) NOT NULL COMMENT '消费类型:CHAT,IMAGE,VIDEO',
    `prompt_tokens`     INT                  DEFAULT NULL COMMENT '输入token数',
    `completion_tokens` INT                  DEFAULT NULL COMMENT '输出token数',
    `total_tokens`      INT                  DEFAULT NULL COMMENT '总token数',
    `unit_price`        BIGINT               DEFAULT NULL COMMENT '单价,每千token价格,单位分',
    `total_cost`        BIGINT      NOT NULL COMMENT '总成本,单位分',
    `consumption_time`  DATETIME    NOT NULL COMMENT '消费时间',
    `create_time`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_time` (`user_id`, `create_time`) COMMENT '用户时间联合索引',
    KEY `idx_conv` (`conversation_id`) COMMENT '会话索引',
    KEY `idx_time` (`consumption_time`) COMMENT '消费时间索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='AI消费记录表-详细记录每次AI调用';

CREATE TABLE `ai_vector_store`
(
    `id`              BIGINT   NOT NULL COMMENT '主键ID',
    `document_id`     BIGINT   NOT NULL COMMENT '文档ID',
    `chunk_index`     INT      NOT NULL COMMENT '分块索引',
    `chunk_content`   TEXT     NOT NULL COMMENT '分块内容',
    `chunk_tokens`    INT      NOT NULL COMMENT '分块token数',
    `vector_id`       VARCHAR(100)      DEFAULT NULL COMMENT '向量数据库中的向量ID',
    `collection_name` VARCHAR(100)      DEFAULT NULL COMMENT '向量库集合名称',
    `embedding_model` VARCHAR(50)       DEFAULT NULL COMMENT '嵌入模型',
    `metadata`        JSON              DEFAULT NULL COMMENT '元数据',
    `create_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_doc_chunk` (`document_id`, `chunk_index`) COMMENT '文档分块联合索引',
    KEY `idx_vector` (`vector_id`) COMMENT '向量ID索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='向量存储索引表-知识库文档向量化映射';

CREATE TABLE `ai_generation_task`
(
    `id`              BIGINT      NOT NULL COMMENT '主键ID',
    `task_guid`       VARCHAR(64) NOT NULL COMMENT '任务唯一标识,UUID格式',
    `user_id`         BIGINT      NOT NULL COMMENT '用户ID',
    `task_type`       VARCHAR(20) NOT NULL COMMENT '任务类型:IMAGE,VIDEO',
    `model_code`      VARCHAR(50) NOT NULL COMMENT '模型编码',
    `prompt`          TEXT        NOT NULL COMMENT '提示词',
    `negative_prompt` TEXT                 DEFAULT NULL COMMENT '反向提示词',
    `task_params`     JSON                 DEFAULT NULL COMMENT '任务参数',
    `task_status`     TINYINT     NOT NULL DEFAULT 0 COMMENT '任务状态:0排队中,1生成中,2已完成,3失败,4已取消',
    `progress`        INT         NOT NULL DEFAULT 0 COMMENT '进度百分比0-100',
    `result_urls`     JSON                 DEFAULT NULL COMMENT '生成结果URL数组',
    `task_cost`       BIGINT               DEFAULT NULL COMMENT '任务成本,单位分',
    `start_time`      DATETIME             DEFAULT NULL COMMENT '开始时间',
    `complete_time`   DATETIME             DEFAULT NULL COMMENT '完成时间',
    `fail_reason`     VARCHAR(500)         DEFAULT NULL COMMENT '失败原因',
    `create_by`       BIGINT               DEFAULT NULL COMMENT '创建人ID',
    `create_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT               DEFAULT NULL COMMENT '更新人ID',
    `update_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`         INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_guid` (`task_guid`) COMMENT '任务唯一标识索引',
    KEY `idx_user_status` (`user_id`, `task_status`) COMMENT '用户状态联合索引',
    KEY `idx_status` (`task_status`) COMMENT '任务状态索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='生成任务表-异步生成任务状态跟踪';

CREATE TABLE `file_info`
(
    `id`                BIGINT       NOT NULL COMMENT '主键ID',
    `file_guid`         VARCHAR(64)  NOT NULL COMMENT '文件全局唯一标识,UUID格式',
    `file_name`         VARCHAR(255) NOT NULL COMMENT '文件名',
    `original_name`     VARCHAR(255) NOT NULL COMMENT '原始文件名',
    `file_extension`    VARCHAR(20)           DEFAULT NULL COMMENT '文件扩展名',
    `file_size`         BIGINT       NOT NULL COMMENT '文件大小,单位字节',
    `file_type`         VARCHAR(100)          DEFAULT NULL COMMENT '文件MIME类型',
    `storage_strategy`  VARCHAR(20)  NOT NULL COMMENT '存储策略:OSS,MINIO,S3,DB',
    `storage_config_id` BIGINT                DEFAULT NULL COMMENT '存储配置ID',
    `storage_path`      VARCHAR(500)          DEFAULT NULL COMMENT '存储路径或对象key',
    `bucket_name`       VARCHAR(100)          DEFAULT NULL COMMENT '存储桶名称',
    `access_url`        VARCHAR(1000)         DEFAULT NULL COMMENT '访问URL',
    `file_md5`          VARCHAR(64)           DEFAULT NULL COMMENT '文件MD5哈希值',
    `thumbnail_file_id` BIGINT                DEFAULT NULL COMMENT '缩略图文件ID',
    `uploader_id`       BIGINT                DEFAULT NULL COMMENT '上传者ID',
    `biz_type`          VARCHAR(50)           DEFAULT NULL COMMENT '业务类型:AVATAR,COVER,DOCUMENT,BLOG_IMAGE,VIDEO',
    `biz_id`            BIGINT                DEFAULT NULL COMMENT '业务关联ID',
    `ref_count`         INT          NOT NULL DEFAULT 0 COMMENT '引用次数',
    `is_public`         TINYINT      NOT NULL DEFAULT 0 COMMENT '是否公开访问:0否,1是',
    `access_expires`    DATETIME              DEFAULT NULL COMMENT '访问过期时间',
    `create_by`         BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`           INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`            VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_guid` (`file_guid`) COMMENT '文件唯一标识索引',
    KEY `idx_md5` (`file_md5`) COMMENT 'MD5索引',
    KEY `idx_uploader` (`uploader_id`) COMMENT '上传者索引',
    KEY `idx_biz` (`biz_type`, `biz_id`) COMMENT '业务类型ID联合索引',
    KEY `idx_thumbnail` (`thumbnail_file_id`) COMMENT '缩略图索引',
    KEY `idx_storage_strategy` (`storage_strategy`) COMMENT '存储策略索引',
    KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引',
    KEY `idx_uploader_time` (`uploader_id`, `create_time`) COMMENT '上传者时间联合索引',
    KEY `idx_biz_deleted` (`biz_type`, `biz_id`, `deleted`) COMMENT '业务关联删除状态联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文件元数据表-统一的文件管理';

CREATE TABLE `file_content`
(
    `id`           BIGINT   NOT NULL COMMENT '主键ID',
    `file_id`      BIGINT   NOT NULL COMMENT '文件ID',
    `content_data` LONGBLOB NOT NULL COMMENT '文件二进制内容',
    `create_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_file` (`file_id`) COMMENT '文件唯一索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文件内容表-数据库存储策略的文件内容';

CREATE TABLE `file_chunk`
(
    `id`              BIGINT       NOT NULL COMMENT '主键ID',
    `upload_id`       VARCHAR(64)  NOT NULL COMMENT '上传任务ID',
    `file_md5`        VARCHAR(64)  NOT NULL COMMENT '文件MD5',
    `file_name`       VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_size`       BIGINT       NOT NULL COMMENT '文件总大小',
    `chunk_size`      INT          NOT NULL COMMENT '分片大小',
    `total_chunks`    INT          NOT NULL COMMENT '总分片数',
    `uploaded_chunks` JSON                  DEFAULT NULL COMMENT '已上传分片号列表',
    `merge_status`    TINYINT      NOT NULL DEFAULT 0 COMMENT '合并状态:0上传中,1已合并,2合并失败',
    `uploader_id`     BIGINT       NOT NULL COMMENT '上传者ID',
    `expire_time`     DATETIME     NOT NULL COMMENT '过期时间',
    `create_by`       BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_upload` (`upload_id`) COMMENT '上传任务唯一索引',
    KEY `idx_md5` (`file_md5`) COMMENT 'MD5索引',
    KEY `idx_expire` (`expire_time`) COMMENT '过期时间索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文件分片表-大文件分片上传临时记录';

CREATE TABLE `file_storage_config`
(
    `id`            BIGINT       NOT NULL COMMENT '主键ID',
    `config_code`   VARCHAR(50)  NOT NULL COMMENT '配置编码,如OSS_ALIYUN,MINIO_DEFAULT',
    `config_name`   VARCHAR(100) NOT NULL COMMENT '配置名称',
    `storage_type`  VARCHAR(20)  NOT NULL COMMENT '存储类型:OSS,MINIO,S3,DB',
    `provider`      VARCHAR(50)           DEFAULT NULL COMMENT '服务提供商:ALIYUN,TENCENT,AWS',
    `access_key`    VARCHAR(200)          DEFAULT NULL COMMENT '访问密钥,加密存储',
    `secret_key`    VARCHAR(200)          DEFAULT NULL COMMENT '访问密钥,加密存储',
    `endpoint`      VARCHAR(500)          DEFAULT NULL COMMENT '访问端点URL',
    `region`        VARCHAR(50)           DEFAULT NULL COMMENT '区域',
    `bucket_name`   VARCHAR(100)          DEFAULT NULL COMMENT '默认存储桶名称',
    `base_path`     VARCHAR(200)          DEFAULT NULL COMMENT '基础路径前缀',
    `domain_url`    VARCHAR(500)          DEFAULT NULL COMMENT '访问域名',
    `is_default`    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认配置:0否,1是',
    `is_enabled`    TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用:0否,1是',
    `priority`      INT          NOT NULL DEFAULT 0 COMMENT '优先级',
    `max_file_size` BIGINT                DEFAULT NULL COMMENT '最大文件大小限制,单位字节',
    `create_by`     BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`       INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`        VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_code` (`config_code`) COMMENT '配置编码唯一索引',
    KEY `idx_type_enabled_priority` (`storage_type`, `is_enabled`, `priority`) COMMENT '类型启用优先级联合索引',
    KEY `idx_default` (`is_default`) COMMENT '默认配置索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='存储策略配置表-配置各种存储方式';

CREATE TABLE `file_access_log`
(
    `id`            BIGINT   NOT NULL COMMENT '主键ID',
    `file_id`       BIGINT   NOT NULL COMMENT '文件ID',
    `user_id`       BIGINT            DEFAULT NULL COMMENT '访问者ID',
    `access_ip`     VARCHAR(50)       DEFAULT NULL COMMENT '访问IP',
    `access_region` VARCHAR(100)      DEFAULT NULL COMMENT '访问地区',
    `user_agent`    VARCHAR(500)      DEFAULT NULL COMMENT '用户代理',
    `referer`       VARCHAR(500)      DEFAULT NULL COMMENT '来源页面',
    `access_result` TINYINT  NOT NULL DEFAULT 0 COMMENT '访问结果:0成功,1鉴权失败,2文件不存在',
    `traffic_bytes` BIGINT            DEFAULT NULL COMMENT '流量消耗字节数',
    `access_time`   DATETIME NOT NULL COMMENT '访问时间',
    `create_time`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_file_time` (`file_id`, `create_time`) COMMENT '文件时间联合索引',
    KEY `idx_ip` (`access_ip`) COMMENT 'IP索引',
    KEY `idx_user` (`user_id`) COMMENT '用户索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文件访问日志表-记录文件访问情况';

CREATE TABLE `sys_config`
(
    `id`           BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `config_key`   VARCHAR(100)          NOT NULL COMMENT '配置键,如system.title',
    `config_value` TEXT                           DEFAULT NULL COMMENT '配置值',
    `config_type`  VARCHAR(20)           NOT NULL DEFAULT 'STRING' COMMENT '配置类型:STRING,NUMBER,BOOLEAN,JSON',
    `config_group` VARCHAR(50)                    DEFAULT NULL COMMENT '配置分组',
    `config_label` VARCHAR(100)                   DEFAULT NULL COMMENT '配置标签',
    `config_desc`  VARCHAR(500)                   DEFAULT NULL COMMENT '配置说明',
    `is_sensitive` TINYINT               NOT NULL DEFAULT 0 COMMENT '是否敏感配置:0否,1是',
    `is_frontend`  TINYINT               NOT NULL DEFAULT 0 COMMENT '是否前端可见:0否,1是',
    `create_by`    BIGINT                         DEFAULT NULL COMMENT '创建人ID',
    `create_time`  DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    BIGINT                         DEFAULT NULL COMMENT '更新人ID',
    `update_time`  DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT               NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`      INT                   NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`       VARCHAR(500)                   DEFAULT NULL COMMENT '备注说明',
    `sort`         INT                   NOT NULL DEFAULT 0 COMMENT '排序字段',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_key` (`config_key`) COMMENT '配置键唯一索引',
    KEY `idx_group` (`config_group`) COMMENT '配置分组索引',
    KEY `idx_frontend` (`is_frontend`) COMMENT '前端可见索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统配置表-系统级别的配置项';

CREATE TABLE `sys_dict_type`
(
    `id`          BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `dict_code`   VARCHAR(50)           NOT NULL COMMENT '字典编码,如user_status',
    `dict_name`   VARCHAR(100)          NOT NULL COMMENT '字典名称',
    `dict_desc`   VARCHAR(500)                   DEFAULT NULL COMMENT '字典描述',
    `dict_sort`   INT                   NOT NULL DEFAULT 0 COMMENT '排序字段',
    `create_by`   BIGINT                         DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   BIGINT                         DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT               NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`     INT                   NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`      VARCHAR(500)                   DEFAULT NULL COMMENT '备注说明',
    `status`      TINYINT               NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_code` (`dict_code`) COMMENT '字典编码唯一索引',
    KEY `idx_status` (`status`) COMMENT '状态索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='字典类型表-数据字典分类管理';

CREATE TABLE `sys_dict_data`
(
    `id`           BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `dict_type_id` BIGINT                NOT NULL COMMENT '字典类型ID',
    `dict_label`   VARCHAR(100)          NOT NULL COMMENT '字典标签,用于展示',
    `dict_value`   VARCHAR(100)          NOT NULL COMMENT '字典值,用于存储',
    `dict_sort`    INT                   NOT NULL DEFAULT 0 COMMENT '排序',
    `css_class`    VARCHAR(100)                   DEFAULT NULL COMMENT '样式类名',
    `list_class`   VARCHAR(100)                   DEFAULT NULL COMMENT '列表样式',
    `is_default`   TINYINT               NOT NULL DEFAULT 0 COMMENT '是否默认选项:0否,1是',
    `create_by`    BIGINT                         DEFAULT NULL COMMENT '创建人ID',
    `create_time`  DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    BIGINT                         DEFAULT NULL COMMENT '更新人ID',
    `update_time`  DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT               NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`      INT                   NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`       VARCHAR(500)                   DEFAULT NULL COMMENT '备注说明',
    `status`       TINYINT               NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    PRIMARY KEY (`id`),
    KEY `idx_type_value` (`dict_type_id`, `dict_value`) COMMENT '类型值联合索引',
    KEY `idx_type_sort` (`dict_type_id`, `dict_sort`) COMMENT '类型排序联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='字典数据表-字典的具体数据项';

CREATE TABLE `email_template`
(
    `id`                BIGINT       NOT NULL COMMENT '主键ID',
    `template_code`     VARCHAR(50)  NOT NULL COMMENT '模板编码,如VERIFY_CODE',
    `template_name`     VARCHAR(100) NOT NULL COMMENT '模板名称',
    `template_subject`  VARCHAR(200) NOT NULL COMMENT '邮件主题',
    `template_content`  TEXT         NOT NULL COMMENT '模板内容,HTML格式',
    `template_type`     VARCHAR(20)  NOT NULL DEFAULT 'HTML' COMMENT '模板类型:HTML,TEXT',
    `variables`         JSON                  DEFAULT NULL COMMENT '变量列表',
    `template_category` VARCHAR(50)           DEFAULT NULL COMMENT '模板分类:验证码,通知,营销',
    `is_system`         TINYINT      NOT NULL DEFAULT 0 COMMENT '是否系统模板:0否,1是',
    `create_by`         BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记:0未删除,1已删除',
    `version`           INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `remark`            VARCHAR(500)          DEFAULT NULL COMMENT '备注说明',
    `status`            TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0正常,1停用',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_code` (`template_code`) COMMENT '模板编码唯一索引',
    KEY `idx_category_status` (`template_category`, `status`) COMMENT '分类状态联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='邮件模板表-预定义的邮件模板';

CREATE TABLE `email_send_queue`
(
    `id`               BIGINT       NOT NULL COMMENT '主键ID',
    `queue_id`         VARCHAR(64)  NOT NULL COMMENT '队列任务ID,UUID格式',
    `template_code`    VARCHAR(50)           DEFAULT NULL COMMENT '模板编码',
    `recipient_email`  VARCHAR(100) NOT NULL COMMENT '收件人邮箱',
    `recipient_name`   VARCHAR(100)          DEFAULT NULL COMMENT '收件人姓名',
    `mail_subject`     VARCHAR(200) NOT NULL COMMENT '邮件主题',
    `mail_content`     TEXT         NOT NULL COMMENT '邮件内容,HTML格式',
    `mail_attachments` JSON                  DEFAULT NULL COMMENT '附件列表',
    `send_status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '发送状态:0待发送,1发送中,2已发送,3失败',
    `priority`         TINYINT      NOT NULL DEFAULT 5 COMMENT '优先级0-9',
    `retry_count`      INT          NOT NULL DEFAULT 0 COMMENT '重试次数',
    `max_retry`        INT          NOT NULL DEFAULT 3 COMMENT '最大重试次数',
    `schedule_time`    DATETIME              DEFAULT NULL COMMENT '定时发送时间',
    `error_message`    VARCHAR(500)          DEFAULT NULL COMMENT '错误信息',
    `send_time`        DATETIME              DEFAULT NULL COMMENT '实际发送时间',
    `create_by`        BIGINT                DEFAULT NULL COMMENT '创建人ID',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        BIGINT                DEFAULT NULL COMMENT '更新人ID',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_queue` (`queue_id`) COMMENT '队列任务唯一索引',
    KEY `idx_status_priority` (`send_status`, `priority`) COMMENT '状态优先级联合索引',
    KEY `idx_schedule` (`schedule_time`) COMMENT '定时发送时间索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='邮件发送队列表-待发送的邮件队列';

CREATE TABLE `email_send_log`
(
    `id`               BIGINT       NOT NULL COMMENT '主键ID',
    `queue_id`         VARCHAR(64)  NOT NULL COMMENT '关联的队列任务ID',
    `template_code`    VARCHAR(50)           DEFAULT NULL COMMENT '使用的模板编码',
    `recipient_email`  VARCHAR(100) NOT NULL COMMENT '收件人邮箱',
    `mail_subject`     VARCHAR(200) NOT NULL COMMENT '邮件主题',
    `send_status`      TINYINT      NOT NULL COMMENT '发送状态:0发送中,1成功,2失败',
    `smtp_server`      VARCHAR(100)          DEFAULT NULL COMMENT 'SMTP服务器',
    `smtp_response`    TEXT                  DEFAULT NULL COMMENT 'SMTP响应信息',
    `error_message`    VARCHAR(500)          DEFAULT NULL COMMENT '错误信息',
    `send_time`        DATETIME     NOT NULL COMMENT '发送时间',
    `process_duration` INT                   DEFAULT NULL COMMENT '处理耗时,单位毫秒',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_queue` (`queue_id`) COMMENT '队列任务索引',
    KEY `idx_email_time` (`recipient_email`, `create_time`) COMMENT '邮箱时间联合索引',
    KEY `idx_status` (`send_status`) COMMENT '发送状态索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='邮件发送日志表-记录每次发送尝试';

CREATE TABLE `email_verify_code`
(
    `id`           BIGINT       NOT NULL COMMENT '主键ID',
    `email`        VARCHAR(100) NOT NULL COMMENT '邮箱地址',
    `verify_code`  VARCHAR(10)  NOT NULL COMMENT '验证码',
    `code_type`    VARCHAR(50)  NOT NULL COMMENT '业务类型:REGISTER,LOGIN,RESET_PASSWORD,CHANGE_EMAIL',
    `verify_scene` VARCHAR(100)          DEFAULT NULL COMMENT '验证场景补充说明',
    `use_time`     DATETIME              DEFAULT NULL COMMENT '使用时间',
    `expire_time`  DATETIME     NOT NULL COMMENT '过期时间',
    `client_ip`    VARCHAR(50)           DEFAULT NULL COMMENT '客户端IP',
    `status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0未使用,1已使用,2已过期,3已失效',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_email_type_status` (`email`, `code_type`, `status`) COMMENT '邮箱类型状态联合索引',
    KEY `idx_expire` (`expire_time`) COMMENT '过期时间索引',
    KEY `idx_ip_time` (`client_ip`, `create_time`) COMMENT 'IP时间联合索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='邮箱验证码表-邮箱验证码存储和校验';

CREATE TABLE `log_operation`
(
    `id`                 BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `user_id`            BIGINT                         DEFAULT NULL COMMENT '操作人ID',
    `username`           VARCHAR(50)                    DEFAULT NULL COMMENT '操作人用户名',
    `application_name`   VARCHAR(50)                    DEFAULT NULL COMMENT '应用名称',
    `operation_module`   VARCHAR(50)           NOT NULL COMMENT '操作模块',
    `operation_type`     VARCHAR(20)           NOT NULL COMMENT '操作类型:CREATE,UPDATE,DELETE,PUBLISH',
    `operation_desc`     VARCHAR(500)                   DEFAULT NULL COMMENT '操作描述',
    `request_method`     VARCHAR(10)                    DEFAULT NULL COMMENT '请求方法',
    `request_url`        VARCHAR(500)                   DEFAULT NULL COMMENT '请求URL',
    `request_params`     TEXT                           DEFAULT NULL COMMENT '请求参数',
    `request_body`       TEXT                           DEFAULT NULL COMMENT '请求体',
    `response_result`    TEXT                           DEFAULT NULL COMMENT '响应结果',
    `operation_ip`       VARCHAR(50)                    DEFAULT NULL COMMENT '操作IP',
    `operation_location` VARCHAR(500)                   DEFAULT NULL COMMENT '操作地点',
    `browser`            VARCHAR(100)                   DEFAULT NULL COMMENT '浏览器信息',
    `os`                 VARCHAR(100)                   DEFAULT NULL COMMENT '操作系统',
    `operation_status`   TINYINT               NOT NULL DEFAULT 0 COMMENT '操作状态:0成功,1失败',
    `error_message`      VARCHAR(500)                   DEFAULT NULL COMMENT '错误信息',
    `execution_time`     INT                            DEFAULT NULL COMMENT '执行耗时,单位毫秒',
    `create_time`        DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_time` (`user_id`, `create_time`) COMMENT '用户时间联合索引',
    KEY `idx_module_type` (`operation_module`, `operation_type`) COMMENT '模块类型联合索引',
    KEY `idx_status` (`operation_status`) COMMENT '操作状态索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='操作日志表-记录用户关键操作';

CREATE TABLE `log_login`
(
    `id`             BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `user_id`        BIGINT                         DEFAULT NULL COMMENT '用户ID',
    `username`       VARCHAR(50)           NOT NULL COMMENT '登录用户名',
    `login_type`     VARCHAR(20)           NOT NULL COMMENT '登录方式:PASSWORD,VERIFY_CODE,WECHAT',
    `login_ip`       VARCHAR(50)                    DEFAULT NULL COMMENT '登录IP',
    `login_location` VARCHAR(100)                   DEFAULT NULL COMMENT '登录地点',
    `browser`        VARCHAR(100)                   DEFAULT NULL COMMENT '浏览器信息',
    `os`             VARCHAR(100)                   DEFAULT NULL COMMENT '操作系统',
    `device_type`    VARCHAR(20)                    DEFAULT NULL COMMENT '设备类型:PC,Mobile,Tablet',
    `login_status`   TINYINT               NOT NULL COMMENT '登录状态:0成功,1失败',
    `fail_reason`    VARCHAR(200)                   DEFAULT NULL COMMENT '失败原因',
    `create_time`    DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_time` (`user_id`, `create_time`) COMMENT '用户时间联合索引',
    KEY `idx_ip_time` (`login_ip`, `create_time`) COMMENT 'IP时间联合索引',
    KEY `idx_status` (`login_status`) COMMENT '登录状态索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='登录日志表-记录用户登录行为';

CREATE TABLE `log_api`
(
    `id`              BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
    `trace_id`        VARCHAR(64)                    DEFAULT NULL COMMENT '链路追踪ID',
    `user_id`         BIGINT                         DEFAULT NULL COMMENT '用户ID',
    `request_method`  VARCHAR(10)           NOT NULL COMMENT '请求方法',
    `request_url`     VARCHAR(500)          NOT NULL COMMENT '请求URL',
    `request_params`  TEXT                           DEFAULT NULL COMMENT '请求参数',
    `response_status` INT                            DEFAULT NULL COMMENT '响应状态码',
    `response_body`   TEXT                           DEFAULT NULL COMMENT '响应内容',
    `execution_time`  INT                            DEFAULT NULL COMMENT '执行耗时,单位毫秒',
    `client_ip`       VARCHAR(50)                    DEFAULT NULL COMMENT '客户端IP',
    `user_agent`      VARCHAR(500)                   DEFAULT NULL COMMENT '用户代理',
    `create_time`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '请求时间',
    PRIMARY KEY (`id`),
    KEY `idx_trace` (`trace_id`) COMMENT '追踪ID索引',
    KEY `idx_url_time` (`request_url`(255), `create_time`) COMMENT 'URL时间联合索引',
    KEY `idx_time` (`execution_time`) COMMENT '执行时间索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='API调用日志表-记录API调用情况';