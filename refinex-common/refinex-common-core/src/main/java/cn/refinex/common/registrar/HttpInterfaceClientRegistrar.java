package cn.refinex.common.registrar;

import cn.refinex.common.annotation.EnableHttpInterfaceClients;
import cn.refinex.common.annotation.HttpInterfaceClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * HTTP Interface 客户端自动注册器
 * <p>
 * 扫描带有 @HttpInterfaceClient 注解的接口，自动注册为 Spring Bean
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public class HttpInterfaceClientRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * 注册 HTTP Interface 客户端 Bean 定义
     *
     * @param metadata 注解元数据
     * @param registry Bean 定义注册器
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, @NonNull BeanDefinitionRegistry registry) {
        // 获取 @EnableHttpInterfaceClients 注解的属性
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableHttpInterfaceClients.class.getName()));
        if (attributes == null) {
            log.warn("未找到 @EnableHttpInterfaceClients 注解");
            return;
        }

        // 确定要扫描的包路径
        Set<String> basePackages = getBasePackages(metadata, attributes);
        if (basePackages.isEmpty()) {
            log.warn("未指定要扫描的包路径，将不会注册任何 HTTP Interface 客户端");
            return;
        }

        log.info("开始扫描 HTTP Interface 客户端，扫描包: {}", basePackages);

        // 创建扫描器
        ClassPathScanningCandidateComponentProvider scanner = createScanner();

        // 扫描并注册
        int registeredCount = 0;
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {
                try {
                    registerHttpInterfaceClient(candidate, registry);
                    registeredCount++;
                } catch (Exception e) {
                    log.error("注册 HTTP Interface 客户端失败: {}", candidate.getBeanClassName(), e);
                }
            }
        }

        log.info("HTTP Interface 客户端扫描完成，共注册 {} 个客户端", registeredCount);
    }

    /**
     * 创建包扫描器
     *
     * @return 包扫描器实例
     */
    private ClassPathScanningCandidateComponentProvider createScanner() {
        // 创建扫描器，禁用默认过滤器
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            /**
             * 判断 Bean 定义是否为候选组件
             * 重写此方法允许接口作为候选组件（默认情况下 Spring 只接受具体类）
             *
             * @param beanDefinition Bean 定义
             * @return 如果是独立的接口则返回 true
             */
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                AnnotationMetadata metadata = beanDefinition.getMetadata();
                // 允许扫描独立的接口（HTTP Interface 客户端都是接口）
                return metadata.isIndependent() && metadata.isInterface();
            }
        };

        // 只扫描带有 @HttpInterfaceClient 注解的接口
        scanner.addIncludeFilter(new AnnotationTypeFilter(HttpInterfaceClient.class));
        return scanner;
    }

    /**
     * 获取要扫描的基础包路径
     *
     * @param metadata   注解元数据
     * @param attributes 注解属性
     * @return 基础包路径集合
     */
    private Set<String> getBasePackages(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        Set<String> basePackages = new HashSet<>();

        // 从 value 属性获取
        String[] value = attributes.getStringArray("value");
        for (String pkg : value) {
            if (StringUtils.isNotBlank(pkg)) {
                basePackages.add(pkg);
            }
        }

        // 从 basePackages 属性获取
        String[] packages = attributes.getStringArray("basePackages");
        for (String pkg : packages) {
            if (StringUtils.isNotBlank(pkg)) {
                basePackages.add(pkg);
            }
        }

        // 从 basePackageClasses 属性获取
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        for (Class<?> clazz : basePackageClasses) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        // 如果没有指定任何包，使用注解所在类的包
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(metadata.getClassName()));
        }

        return basePackages;
    }

    /**
     * 注册 HTTP Interface 客户端 Bean 定义
     *
     * @param candidate 候选 Bean 定义
     * @param registry  Bean 定义注册器
     */
    private void registerHttpInterfaceClient(BeanDefinition candidate, BeanDefinitionRegistry registry) {
        String className = candidate.getBeanClassName();
        if (className == null) {
            log.warn("无法获取类名，跳过注册");
            return;
        }

        try {
            // 加载接口类
            Class<?> clientClass = ClassUtils.forName(className, null);

            // 获取 @HttpInterfaceClient 注解
            HttpInterfaceClient annotation = clientClass.getAnnotation(HttpInterfaceClient.class);
            if (annotation == null) {
                log.warn("类 {} 未标注 @HttpInterfaceClient 注解，跳过注册", className);
                return;
            }

            // 获取服务名
            String serviceName = getServiceName(annotation);
            if (StringUtils.isBlank(serviceName)) {
                log.error("类 {} 的 @HttpInterfaceClient 注解未指定 serviceName，跳过注册", className);
                return;
            }

            // 确定 Bean 名称
            String beanName = determineBeanName(annotation, clientClass);

            // 检查 Bean 是否已存在
            if (registry.containsBeanDefinition(beanName)) {
                log.warn("Bean {} 已存在，跳过注册", beanName);
                return;
            }

            // 创建 Bean 定义
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clientClass);
            builder.setFactoryMethodOnBean("createClient", "httpServiceProxyFactoryCreator");
            builder.addConstructorArgValue(serviceName);
            builder.addConstructorArgValue(clientClass);
            builder.setLazyInit(false);

            // 注册 Bean
            registry.registerBeanDefinition(beanName, builder.getBeanDefinition());

            log.info("成功注册 HTTP Interface 客户端: {} -> {} (服务: {})", beanName, clientClass.getSimpleName(), serviceName);

        } catch (ClassNotFoundException e) {
            log.error("无法加载类: {}", className, e);
        }
    }

    /**
     * 获取服务名
     *
     * @param annotation @HttpInterfaceClient 注解实例
     * @return 服务名
     */
    private String getServiceName(HttpInterfaceClient annotation) {
        String serviceName = annotation.serviceName();
        if (StringUtils.isBlank(serviceName)) {
            serviceName = annotation.value();
        }
        return serviceName;
    }

    /**
     * 确定 Bean 名称
     *
     * @param annotation  @HttpInterfaceClient 注解实例
     * @param clientClass 客户端接口类
     * @return Bean 名称
     */
    private String determineBeanName(HttpInterfaceClient annotation, Class<?> clientClass) {
        String beanName = annotation.beanName();
        if (StringUtils.isNotBlank(beanName)) {
            return beanName;
        }

        // 使用接口类名首字母小写作为 Bean 名称
        String simpleName = clientClass.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }
}

