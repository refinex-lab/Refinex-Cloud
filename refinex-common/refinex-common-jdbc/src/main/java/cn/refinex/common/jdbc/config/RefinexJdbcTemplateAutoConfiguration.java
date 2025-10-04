package cn.refinex.common.jdbc.config;

import cn.refinex.common.jdbc.config.properties.JdbcTemplateProperties;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.dialect.DatabaseDialect;
import cn.refinex.common.jdbc.dialect.MySQLDialect;
import cn.refinex.common.jdbc.dialect.OracleDialect;
import cn.refinex.common.jdbc.dialect.PostgreSQLDialect;
import cn.refinex.common.jdbc.masker.DefaultSensitiveDataMasker;
import cn.refinex.common.jdbc.masker.SensitiveDataMasker;
import cn.refinex.common.jdbc.sql.NamedSqlManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * JDBC 模板自动配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(JdbcTemplate.class) // 当类路径下存在 JdbcTemplate 类时，才启用自动配置
@EnableConfigurationProperties(JdbcTemplateProperties.class)
public class RefinexJdbcTemplateAutoConfiguration {

    /**
     * 配置敏感数据脱敏器
     *
     * @param properties 配置属性
     * @return 脱敏器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SensitiveDataMasker sensitiveDataMasker(JdbcTemplateProperties properties) {
        log.info("初始化敏感数据脱敏器");
        return new DefaultSensitiveDataMasker(
                properties.getSensitiveKeys(),
                properties.getSensitivePatterns(),
                properties.getMaskValue()
        );
    }

    /**
     * 配置数据库方言
     *
     * @param properties 配置属性
     * @return 数据库方言实例
     */
    @Bean
    @ConditionalOnMissingBean
    public DatabaseDialect databaseDialect(JdbcTemplateProperties properties) {
        String databaseType = properties.getDatabaseType().toLowerCase();
        log.info("初始化数据库方言: {}", databaseType);

        switch (databaseType) {
            case "mysql":
                return new MySQLDialect();
            case "oracle":
                return new OracleDialect();
            case "postgresql":
            case "postgres":
                return new PostgreSQLDialect();
            default:
                log.warn("未识别的数据库类型: {}，使用 MySQL 方言", databaseType);
                return new MySQLDialect();
        }
    }

    /**
     * 配置命名 SQL 管理器
     *
     * @return 命名 SQL 管理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public NamedSqlManager namedSqlManager() {
        log.info("初始化命名 SQL 管理器");
        return new NamedSqlManager();
    }

    /**
     * 配置事务定义
     *
     * @return 事务定义实例
     */
    @Bean
    @ConditionalOnMissingBean(TransactionDefinition.class)
    public TransactionDefinition transactionDefinition() {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        // 设置事务传播行为为 REQUIRED（如果当前存在事务，则加入该事务；否则创建新事务）
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        // 设置隔离级别为默认隔离级别, 例如 MySQL 的 ISOLATION_READ_COMMITTED(读已提交)
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        return definition;
    }

    /**
     * 配置 JDBC 模板管理器
     *
     * @param namedParameterJdbcTemplate 命名参数 JDBC 模板
     * @param transactionManager         事务管理器（可选）
     * @param transactionDefinition      事务定义（可选）
     * @param properties                 配置属性
     * @param sensitiveDataMasker        敏感数据脱敏器
     * @param databaseDialect            数据库方言
     * @param namedSqlManager            命名 SQL 管理器
     * @return JDBC 模板管理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public JdbcTemplateManager jdbcTemplateManager(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            DataSourceTransactionManager transactionManager,
            TransactionDefinition transactionDefinition,
            JdbcTemplateProperties properties,
            SensitiveDataMasker sensitiveDataMasker,
            DatabaseDialect databaseDialect,
            NamedSqlManager namedSqlManager) {

        log.info("初始化 JDBC 模板管理器");

        JdbcTemplateManager manager = new JdbcTemplateManager(
                namedParameterJdbcTemplate,
                transactionManager,
                transactionDefinition
        );

        manager.setSensitiveDataMasker(sensitiveDataMasker);
        manager.setDatabaseDialect(databaseDialect);
        manager.setNamedSqlManager(namedSqlManager);
        manager.setLowerCaseColumnNames(properties.isLowerCaseColumnNames());
        manager.setSlowQueryThresholdMs(properties.getSlowQueryThresholdMs());
        manager.setEnableColumnConflictCheck(properties.isEnableColumnConflictCheck());
        manager.setLogFormat(properties.getLogFormat());

        log.info("JDBC 模板管理器初始化完成");
        return manager;
    }
}
