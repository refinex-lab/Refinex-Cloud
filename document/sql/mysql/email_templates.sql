-- =============================================
-- 邮件模板初始化脚本
-- 用途：用户通知相关邮件模板
-- =============================================

USE `refinex_platform`;

-- 1. 踢人下线通知模板
INSERT INTO `email_template` (
    `id`,
    `template_code`,
    `template_name`,
    `template_subject`,
    `template_content`,
    `template_type`,
    `variables`,
    `template_category`,
    `is_system`,
    `create_by`,
    `create_time`,
    `update_by`,
    `update_time`,
    `deleted`,
    `version`,
    `remark`,
    `status`
) VALUES (
    1001,
    'USER_KICKOUT',
    '账号下线通知',
    '您的账号已被强制下线',
    '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>账号下线通知</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #f44336; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
        .content { background-color: #f9f9f9; padding: 30px; border: 1px solid #ddd; border-top: none; }
        .info-box { background-color: white; padding: 15px; margin: 20px 0; border-left: 4px solid #f44336; }
        .info-item { margin: 10px 0; }
        .label { font-weight: bold; color: #555; }
        .footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }
    </style>
</head>
<body>
    <div class="header">
        <h2>账号下线通知</h2>
    </div>
    <div class="content">
        <p>尊敬的 <strong th:text="${username}">用户</strong>：</p>
        <p>您的账号已被管理员强制下线，详细信息如下：</p>
        
        <div class="info-box">
            <div class="info-item">
                <span class="label">设备类型：</span>
                <span th:text="${deviceType}">PC</span>
            </div>
            <div class="info-item">
                <span class="label">下线时间：</span>
                <span th:text="${kickoutTime}">2025-10-05 10:00:00</span>
            </div>
            <div class="info-item">
                <span class="label">操作人员：</span>
                <span th:text="${operatorName}">管理员</span>
            </div>
            <div class="info-item">
                <span class="label">下线原因：</span>
                <span th:text="${reason}">管理员操作</span>
            </div>
        </div>
        
        <p>如有疑问，请联系客服或管理员。</p>
        <p>感谢您的理解与配合。</p>
    </div>
    <div class="footer">
        <p>此邮件由系统自动发送，请勿直接回复。</p>
    </div>
</body>
</html>',
    'HTML',
    '["username", "deviceType", "kickoutTime", "operatorName", "reason"]',
    '通知',
    1,
    NULL,
    NOW(),
    NULL,
    NOW(),
    0,
    0,
    '用户被踢下线时发送的通知邮件',
    0
);

-- 2. 账号封禁通知模板
INSERT INTO `email_template` (
    `id`,
    `template_code`,
    `template_name`,
    `template_subject`,
    `template_content`,
    `template_type`,
    `variables`,
    `template_category`,
    `is_system`,
    `create_by`,
    `create_time`,
    `update_by`,
    `update_time`,
    `deleted`,
    `version`,
    `remark`,
    `status`
) VALUES (
    1002,
    'USER_DISABLE',
    '账号封禁通知',
    '您的账号已被封禁',
    '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>账号封禁通知</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #ff5722; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
        .content { background-color: #f9f9f9; padding: 30px; border: 1px solid #ddd; border-top: none; }
        .warning-box { background-color: #fff3cd; padding: 15px; margin: 20px 0; border-left: 4px solid #ff5722; border-radius: 4px; }
        .info-item { margin: 10px 0; }
        .label { font-weight: bold; color: #555; }
        .highlight { color: #ff5722; font-weight: bold; }
        .footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }
    </style>
</head>
<body>
    <div class="header">
        <h2>账号封禁通知</h2>
    </div>
    <div class="content">
        <p>尊敬的 <strong th:text="${username}">用户</strong>：</p>
        <p>您的账号已被封禁，详细信息如下：</p>
        
        <div class="warning-box">
            <div class="info-item">
                <span class="label">封禁范围：</span>
                <span class="highlight" th:text="${service}">全局</span>
            </div>
            <div class="info-item">
                <span class="label">封禁时长：</span>
                <span th:text="${disableSeconds}">86400 秒</span>
            </div>
            <div class="info-item">
                <span class="label">封禁时间：</span>
                <span th:text="${disableTime}">2025-10-05 10:00:00</span>
            </div>
            <div class="info-item">
                <span class="label">到期时间：</span>
                <span th:text="${expireTime}">2025-10-06 10:00:00</span>
            </div>
            <div class="info-item">
                <span class="label">操作人员：</span>
                <span th:text="${operatorName}">管理员</span>
            </div>
            <div class="info-item">
                <span class="label">封禁原因：</span>
                <span th:text="${reason}">违规操作</span>
            </div>
        </div>
        
        <p>在封禁期间，您将无法使用相关功能。</p>
        <p>如有疑问或认为封禁有误，请联系客服或管理员申诉。</p>
    </div>
    <div class="footer">
        <p>此邮件由系统自动发送，请勿直接回复。</p>
    </div>
</body>
</html>',
    'HTML',
    '["username", "service", "disableSeconds", "disableTime", "expireTime", "operatorName", "reason"]',
    '通知',
    1,
    NULL,
    NOW(),
    NULL,
    NOW(),
    0,
    0,
    '用户被封禁时发送的通知邮件',
    0
);

-- 3. 账号解封通知模板
INSERT INTO `email_template` (
    `id`,
    `template_code`,
    `template_name`,
    `template_subject`,
    `template_content`,
    `template_type`,
    `variables`,
    `template_category`,
    `is_system`,
    `create_by`,
    `create_time`,
    `update_by`,
    `update_time`,
    `deleted`,
    `version`,
    `remark`,
    `status`
) VALUES (
    1003,
    'USER_UNTIE',
    '账号解封通知',
    '您的账号已解封',
    '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>账号解封通知</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #4caf50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
        .content { background-color: #f9f9f9; padding: 30px; border: 1px solid #ddd; border-top: none; }
        .success-box { background-color: #d4edda; padding: 15px; margin: 20px 0; border-left: 4px solid #4caf50; border-radius: 4px; }
        .info-item { margin: 10px 0; }
        .label { font-weight: bold; color: #555; }
        .footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }
    </style>
</head>
<body>
    <div class="header">
        <h2>账号解封通知</h2>
    </div>
    <div class="content">
        <p>尊敬的 <strong th:text="${username}">用户</strong>：</p>
        <p>您的账号已解封，可以正常使用。</p>
        
        <div class="success-box">
            <div class="info-item">
                <span class="label">解封范围：</span>
                <span th:text="${service}">全局</span>
            </div>
            <div class="info-item">
                <span class="label">解封时间：</span>
                <span th:text="${untieTime}">2025-10-05 10:00:00</span>
            </div>
            <div class="info-item">
                <span class="label">操作人员：</span>
                <span th:text="${operatorName}">管理员</span>
            </div>
        </div>
        
        <p>请遵守平台规则，避免再次被封禁。</p>
        <p>感谢您的理解与配合。</p>
    </div>
    <div class="footer">
        <p>此邮件由系统自动发送，请勿直接回复。</p>
    </div>
</body>
</html>',
    'HTML',
    '["username", "service", "untieTime", "operatorName"]',
    '通知',
    1,
    NULL,
    NOW(),
    NULL,
    NOW(),
    0,
    0,
    '用户被解封时发送的通知邮件',
    0
);

