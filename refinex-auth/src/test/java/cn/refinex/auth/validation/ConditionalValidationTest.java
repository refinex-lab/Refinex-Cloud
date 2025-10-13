package cn.refinex.auth.validation;

import cn.refinex.auth.domain.dto.request.LoginRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 条件校验注解单元测试
 *
 * @author Refinex
 * @since 1.0.0
 */
@DisplayName("条件校验测试")
class ConditionalValidationTest {

    private static Validator validator;

    /**
     * 初始化校验器
     */
    @BeforeAll
    static void setUp() {
        // 构建校验器工厂，获取校验器实例
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("密码登录时用户名不能为空")
    void testPasswordLoginRequiresUsername() {
        LoginRequest request = new LoginRequest();
        request.setLoginType(1);
        request.setPassword("123456");
        // username 为 null

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("登录类型为密码登录时，用户名不能为空");
    }

    @Test
    @DisplayName("密码登录时提供用户名应通过校验")
    void testPasswordLoginWithUsernameIsValid() {
        LoginRequest request = new LoginRequest();
        request.setLoginType(1);
        request.setUsername("admin");
        request.setPassword("123456");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("邮箱登录时邮箱不能为空")
    void testEmailLoginRequiresEmail() {
        LoginRequest request = new LoginRequest();
        request.setLoginType(2);
        request.setPassword("123456");
        // email 为 null

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("登录类型为邮箱登录时，邮箱不能为空");
    }

    @Test
    @DisplayName("邮箱登录时提供邮箱应通过校验")
    void testEmailLoginWithEmailIsValid() {
        LoginRequest request = new LoginRequest();
        request.setLoginType(2);
        request.setEmail("admin@example.com");
        request.setPassword("123456");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("密码登录时不需要校验邮箱 - 验证条件性跳过")
    void testPasswordLoginDoesNotRequireEmail() {
        LoginRequest request = new LoginRequest();
        request.setLoginType(1);
        request.setUsername("admin");
        request.setPassword("123456");
        // 特意将 email 设置为空字符串而不是 null，用于验证条件校验的跳过逻辑
        request.setEmail("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // 验证没有任何校验错误
        assertThat(violations).isEmpty();

        // 进一步验证：如果改为邮箱登录，空邮箱应该触发校验错误
        request.setLoginType(2);
        Set<ConstraintViolation<LoginRequest>> violationsForEmailLogin = validator.validate(request);

        assertThat(violationsForEmailLogin).isNotEmpty();
        assertThat(violationsForEmailLogin.stream()
                .map(ConstraintViolation::getMessage)
                .toList())
                .contains("登录类型为邮箱登录时，邮箱不能为空");
    }

    @Test
    @DisplayName("邮箱登录时不需要校验用户名 - 验证条件性跳过")
    void testEmailLoginDoesNotRequireUsername() {
        LoginRequest request = new LoginRequest();
        request.setLoginType(2);
        request.setEmail("admin@example.com");
        request.setPassword("123456");
        // 特意将 username 设置为空字符串而不是 null，用于验证条件校验的跳过逻辑
        request.setUsername("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // 验证没有任何校验错误
        assertThat(violations).isEmpty();

        // 进一步验证：如果改为密码登录，空用户名应该触发校验错误
        request.setLoginType(1);
        Set<ConstraintViolation<LoginRequest>> violationsForPasswordLogin = validator.validate(request);

        assertThat(violationsForPasswordLogin).isNotEmpty();
        assertThat(violationsForPasswordLogin.stream()
                .map(ConstraintViolation::getMessage)
                .toList())
                .contains("登录类型为密码登录时，用户名不能为空");
    }

    @Test
    @DisplayName("空白字符串应该校验失败")
    void testBlankValuesShouldFail() {
        LoginRequest request = new LoginRequest();
        request.setLoginType(1);
        request.setUsername("   "); // 空白字符串
        request.setPassword("123456");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("登录类型为密码登录时，用户名不能为空");
    }

    @Test
    @DisplayName("同时测试多个条件校验的独立性")
    void testMultipleConditionalValidationsAreIndependent() {
        // 场景1：密码登录，用户名和邮箱都为空
        LoginRequest request1 = new LoginRequest();
        request1.setLoginType(1);
        request1.setPassword("123456");

        Set<ConstraintViolation<LoginRequest>> violations1 = validator.validate(request1);

        // 只应该校验用户名，不校验邮箱
        assertThat(violations1).hasSize(1);
        assertThat(violations1.iterator().next().getMessage())
                .isEqualTo("登录类型为密码登录时，用户名不能为空");

        // 场景2：邮箱登录，用户名和邮箱都为空
        LoginRequest request2 = new LoginRequest();
        request2.setLoginType(2);
        request2.setPassword("123456");

        Set<ConstraintViolation<LoginRequest>> violations2 = validator.validate(request2);

        // 只应该校验邮箱，不校验用户名
        assertThat(violations2).hasSize(1);
        assertThat(violations2.iterator().next().getMessage())
                .isEqualTo("登录类型为邮箱登录时，邮箱不能为空");
    }
}
