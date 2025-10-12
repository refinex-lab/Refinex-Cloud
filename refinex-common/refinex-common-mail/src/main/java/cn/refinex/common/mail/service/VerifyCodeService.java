package cn.refinex.common.mail.service;

import cn.hutool.core.util.RandomUtil;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.mail.config.properties.MailProperties;
import cn.refinex.common.mail.domain.dto.EmailSendRequest;
import cn.refinex.common.mail.domain.dto.EmailSendResult;
import cn.refinex.common.mail.domain.dto.VerifyCodeRequest;
import cn.refinex.common.mail.domain.dto.VerifyCodeResult;
import cn.refinex.common.mail.domain.dto.VerifyCodeValidateRequest;
import cn.refinex.common.mail.domain.entity.EmailVerifyCode;
import cn.refinex.common.mail.enums.VerifyCodeStatus;
import cn.refinex.common.mail.constants.EmailErrorMessageConstants;
import cn.refinex.common.mail.repository.EmailVerifyCodeRepository;
import cn.refinex.common.redis.RedisService;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyCodeService {

    private final EmailVerifyCodeRepository verifyCodeRepository;
    private final EmailSendService emailSendService;
    private final MailProperties mailProperties;
    private final RedisService redisService;
    private final SnowflakeIdGenerator idGenerator;

    private static final String REDIS_KEY_EMAIL_PREFIX = "verify_code:email:";
    private static final String REDIS_KEY_IP_PREFIX = "verify_code:ip:";

    /**
     * 发送验证码
     *
     * @param request 验证码请求
     * @return 验证码结果
     */
    public VerifyCodeResult sendVerifyCode(VerifyCodeRequest request) {
        // 1. 频率限制检查
        checkRateLimit(request.getEmail(), request.getClientIp());

        // 2. 生成验证码
        String verifyCode = generateVerifyCode();

        // 3. 计算过期时间
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(mailProperties.getVerifyCode().getExpireMinutes());

        // 4. 保存验证码到数据库
        EmailVerifyCode codeEntity = new EmailVerifyCode();
        codeEntity.setId(idGenerator.nextId());
        codeEntity.setEmail(request.getEmail());
        codeEntity.setVerifyCode(verifyCode);
        codeEntity.setCodeType(request.getCodeType());
        codeEntity.setVerifyScene(request.getVerifyScene());
        codeEntity.setIsUsed(0);
        codeEntity.setExpireTime(expireTime);
        codeEntity.setClientIp(request.getClientIp());
        codeEntity.setStatus(VerifyCodeStatus.UNUSED.getCode());
        codeEntity.setCreateTime(LocalDateTime.now());

        int rows = verifyCodeRepository.insert(codeEntity);
        if (rows <= 0) {
            log.error("保存验证码失败: email={}", request.getEmail());
            return VerifyCodeResult.failure(request.getEmail(), request.getCodeType(), "保存验证码失败");
        }

        // 5. 使同一邮箱同一类型的其他验证码失效
        verifyCodeRepository.invalidateOtherCodes(request.getEmail(), request.getCodeType(), codeEntity.getId());

        // 6. 发送验证码邮件
        try {
            String templateCode = request.getTemplateCode() != null 
                    ? request.getTemplateCode() 
                    : mailProperties.getVerifyCode().getTemplateCode();

            Map<String, Object> variables = new HashMap<>();
            variables.put("code", verifyCode);
            variables.put("expireMinutes", mailProperties.getVerifyCode().getExpireMinutes());
            variables.put("email", request.getEmail());
            variables.put("codeType", request.getCodeType());

            EmailSendRequest sendRequest = EmailSendRequest.builder()
                    .recipientEmail(request.getEmail())
                    .templateCode(templateCode)
                    .templateVariables(variables)
                    .smtpConfigId(request.getSmtpConfigId())
                    .build();

            EmailSendResult sendResult = emailSendService.sendSync(sendRequest);

            if (Boolean.FALSE.equals(sendResult.getSuccess())) {
                log.error("发送验证码邮件失败: email={}, error={}", request.getEmail(), sendResult.getErrorMessage());
                return VerifyCodeResult.failure(
                        request.getEmail(), 
                        request.getCodeType(), 
                        "发送验证码邮件失败: " + sendResult.getErrorMessage()
                );
            }

            // 7. 更新 Redis 频率限制
            updateRateLimit(request.getEmail(), request.getClientIp());

            log.info("发送验证码成功: email={}, codeType={}", request.getEmail(), request.getCodeType());
            return VerifyCodeResult.success(request.getEmail(), request.getCodeType(), verifyCode, expireTime);

        } catch (Exception e) {
            log.error("发送验证码异常: email={}", request.getEmail(), e);
            return VerifyCodeResult.failure(request.getEmail(), request.getCodeType(), e.getMessage());
        }
    }

    /**
     * 验证验证码
     *
     * @param request 验证请求
     * @return 是否验证成功
     */
    public boolean verifyCode(VerifyCodeValidateRequest request) {
        // 1. 查询验证码
        EmailVerifyCode codeEntity = verifyCodeRepository.findByEmailAndCode(
                request.getEmail(),
                request.getVerifyCode(),
                request.getCodeType()
        );

        if (Objects.isNull(codeEntity)) {
            log.error("验证码不存在: email={}, code={}", request.getEmail(), request.getVerifyCode());
            throw new SystemException(EmailErrorMessageConstants.VERIFY_CODE_INVALID);
        }

        // 2. 检查验证码状态
        if (!VerifyCodeStatus.UNUSED.getCode().equals(codeEntity.getStatus())) {
            log.error("验证码状态异常: email={}, status={}", request.getEmail(), codeEntity.getStatus());
            throw new SystemException(EmailErrorMessageConstants.VERIFY_CODE_INVALID);
        }

        // 3. 检查是否已使用
        if (Objects.nonNull(codeEntity.getIsUsed()) && codeEntity.getIsUsed() == 1) {
            log.error("验证码已使用: email={}", request.getEmail());
            throw new SystemException(EmailErrorMessageConstants.VERIFY_CODE_USED);
        }

        // 4. 检查是否过期
        if (codeEntity.getExpireTime().isBefore(LocalDateTime.now())) {
            log.error("验证码已过期: email={}, expireTime={}", request.getEmail(), codeEntity.getExpireTime());
            // 更新状态为已过期
            verifyCodeRepository.markAsInvalid(codeEntity.getId());
            throw new SystemException(EmailErrorMessageConstants.VERIFY_CODE_EXPIRED);
        }

        // 5. 标记为已使用
        if (Boolean.TRUE.equals(request.getDeleteAfterValidate())) {
            verifyCodeRepository.markAsUsed(codeEntity.getId());
        }

        log.info("验证码验证成功: email={}, codeType={}", request.getEmail(), request.getCodeType());
        return true;
    }

    /**
     * 生成验证码
     *
     * @return 验证码
     */
    private String generateVerifyCode() {
        int codeLength = mailProperties.getVerifyCode().getCodeLength();
        String codeType = mailProperties.getVerifyCode().getCodeType();

        StringBuilder code = new StringBuilder();

        switch (codeType) {
            case "NUMERIC":
                // 纯数字
                for (int i = 0; i < codeLength; i++) {
                    code.append(RandomUtil.randomInt(0, 10));
                }
                break;
            case "ALPHA":
                // 纯字母
                for (int i = 0; i < codeLength; i++) {
                    code.append((char) (RandomUtil.randomInt(0, 26) + 'A'));
                }
                break;
            case "ALPHANUMERIC":
                // 字母+数字
                String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                for (int i = 0; i < codeLength; i++) {
                    code.append(chars.charAt(RandomUtil.randomInt(0, chars.length())));
                }
                break;
            default:
                // 默认纯数字
                for (int i = 0; i < codeLength; i++) {
                    code.append(RandomUtil.randomInt(0, 10));
                }
        }

        return code.toString();
    }

    /**
     * 检查频率限制
     *
     * @param email    邮箱
     * @param clientIp 客户端 IP
     */
    private void checkRateLimit(String email, String clientIp) {
        if (Boolean.FALSE.equals(mailProperties.getVerifyCode().getRateLimit().getEnabled())) {
            return;
        }

        // 1. 检查邮箱频率限制
        String emailKey = REDIS_KEY_EMAIL_PREFIX + email;
        Integer emailCount = redisService.getStringService().get(emailKey, Integer.class);
        if (Objects.nonNull(emailCount) && emailCount >= mailProperties.getVerifyCode().getRateLimit().getEmailPerMinute()) {
            log.error("邮箱发送频率超限: email={}, count={}", email, emailCount);
            throw new SystemException(EmailErrorMessageConstants.VERIFY_CODE_SEND_TOO_FREQUENT);
        }

        // 2. 检查 IP 频率限制
        if (StringUtils.isNotBlank(clientIp)) {
            String ipKey = REDIS_KEY_IP_PREFIX + clientIp;
            Integer ipCount = redisService.getStringService().get(ipKey, Integer.class);
            if (ipCount != null && ipCount >= mailProperties.getVerifyCode().getRateLimit().getIpPerMinute()) {
                log.error("IP 发送频率超限: ip={}, count={}", clientIp, ipCount);
                throw new SystemException(EmailErrorMessageConstants.VERIFY_CODE_SEND_TOO_FREQUENT);
            }
        }
    }

    /**
     * 更新频率限制
     *
     * @param email    邮箱
     * @param clientIp 客户端 IP
     */
    private void updateRateLimit(String email, String clientIp) {
        if (Boolean.FALSE.equals(mailProperties.getVerifyCode().getRateLimit().getEnabled())) {
            return;
        }

        // 1. 更新邮箱计数
        String emailKey = REDIS_KEY_EMAIL_PREFIX + email;
        redisService.getStringService().increment(emailKey);
        redisService.expire(emailKey, 1, TimeUnit.MINUTES);

        // 2. 更新 IP 计数
        if (clientIp != null) {
            String ipKey = REDIS_KEY_IP_PREFIX + clientIp;
            redisService.getStringService().increment(ipKey);
            redisService.expire(ipKey, 1, TimeUnit.MINUTES);
        }
    }

    /**
     * 更新过期验证码状态
     *
     * @return 更新数量
     */
    public int updateExpiredCodes() {
        return verifyCodeRepository.updateExpiredCodes();
    }
}

