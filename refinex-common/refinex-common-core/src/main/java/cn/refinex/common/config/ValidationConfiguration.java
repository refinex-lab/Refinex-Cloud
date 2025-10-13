package cn.refinex.common.config;

import jakarta.validation.Validator;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * 校验配置类
 * <p>
 * 配置 Hibernate Validator 并启用快速失败模式
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Configuration
public class ValidationConfiguration {

    /**
     * 配置 Validator Bean
     * <p>
     * 启用快速失败模式：一旦检测到第一个校验失败就立即返回，提高性能
     * </p>
     */
    @Bean
    public Validator validator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.getValidationPropertyMap().put("hibernate.validator.fail_fast", "true");
        return validatorFactoryBean;
    }

    /**
     * 配置方法级别的校验
     * <p>
     * 支持在 Service 层方法参数上使用 @Valid 注解
     * </p>
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(Validator validator) {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        return processor;
    }
}
