package cn.refinex.common.job.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * XXL-Job 配置属性类
 *
 * <p>管理 XXL-Job 分布式任务调度框架的所有配置参数，通过配置文件灵活控制调度器和执行器的行为。</p>
 *
 * <h3>配置示例：</h3>
 * <pre>{@code
 * xxl:
 *   job:
 *     enabled: true
 *     access-token: default_token
 *     admin:
 *       addresses: http://127.0.0.1:8080/xxl-job-admin
 *     executor:
 *       app-name: ${spring.application.name}
 *       ip:
 *       port: -1
 *       log-path: ./logs/xxl-job
 *       log-retention-days: 30
 * }</pre>
 *
 * <h3>配置说明：</h3>
 * <ul>
 *     <li><strong>enabled=false：</strong>可以在开发或测试环境关闭 XXL-Job</li>
 *     <li><strong>access-token：</strong>建议在生产环境配置强密码令牌</li>
 *     <li><strong>port=-1：</strong>自动分配可用端口，适合容器化部署</li>
 *     <li><strong>log-retention-days=-1：</strong>永久保留日志（不推荐）</li>
 * </ul>
 *
 * <h3>安全建议：</h3>
 * <ul>
 *     <li>生产环境必须配置 access-token，避免使用默认值</li>
 *     <li>调度中心地址应使用内网地址，不对外暴露</li>
 *     <li>定期检查和清理执行日志，避免磁盘占满</li>
 * </ul>
 *
 * @author Refinex
 * @see <a href="https://www.xuxueli.com/xxl-job/">XXL-Job 官方文档</a>
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "xxl.job")
public class RefinexXxlJobProperties {

    /**
     * 是否启用 XXL-Job，默认开启
     *
     * <p>设置为 false 可以在不删除配置的情况下临时关闭 XXL-Job，适用于开发、测试环境或应急场景。</p>
     *
     * <p><strong>注意：</strong>关闭后所有定时任务将不会执行。</p>
     */
    private Boolean enabled = true;

    /**
     * 访问令牌，用于调度中心与执行器之间的安全认证
     *
     * <p>调度中心和执行器必须配置相同的令牌才能正常通信。建议在生产环境配置复杂的令牌字符串，增强安全性。</p>
     *
     * <p><strong>配置示例：</strong></p>
     * <pre>{@code
     * # 开发环境
     * access-token: default_token
     *
     * # 生产环境（推荐）
     * access-token: ${XXL_JOB_ACCESS_TOKEN:your-strong-password-here}
     * }</pre>
     *
     * <p><strong>安全提示：</strong>令牌应定期更换，避免泄露。</p>
     */
    private String accessToken;

    /**
     * 调度中心配置
     *
     * <p>配置 XXL-Job 调度中心的连接信息，执行器通过该配置向调度中心注册。</p>
     */
    @NotNull(message = "调度中心配置不能为空，请配置 xxl.job.admin")
    @Valid
    private AdminProperties admin;

    /**
     * 执行器配置
     *
     * <p>配置当前应用作为 XXL-Job 执行器的运行参数。</p>
     */
    @NotNull(message = "执行器配置不能为空，请配置 xxl.job.executor")
    @Valid
    private ExecutorProperties executor;

    /**
     * XXL-Job 调度中心配置类
     *
     * <p>定义执行器与调度中心的连接配置。</p>
     */
    @Data
    @Valid
    public static class AdminProperties {

        /**
         * 调度中心地址列表
         *
         * <p>支持配置多个调度中心地址，使用逗号分隔，实现高可用部署。执行器会自动选择可用的调度中心进行注册和心跳。</p>
         *
         * <p><strong>配置格式：</strong></p>
         * <ul>
         *     <li><strong>单个地址：</strong>http://127.0.0.1:8080/xxl-job-admin</li>
         *     <li><strong>多个地址：</strong>http://host1:8080/xxl-job-admin,http://host2:8080/xxl-job-admin</li>
         * </ul>
         *
         * <p><strong>注意事项：</strong></p>
         * <ul>
         *     <li>地址必须包含完整的协议和路径（http:// 或 https://）</li>
         *     <li>多个地址之间不要有空格</li>
         *     <li>建议配置内网地址，提高安全性和性能</li>
         *     <li>生产环境建议配置至少两个地址实现高可用</li>
         * </ul>
         *
         * <p><strong>配置示例：</strong></p>
         * <pre>{@code
         * # 单节点部署
         * addresses: http://192.168.1.100:8080/xxl-job-admin
         *
         * # 集群部署（推荐）
         * addresses: http://192.168.1.100:8080/xxl-job-admin,http://192.168.1.101:8080/xxl-job-admin
         * }</pre>
         */
        @NotEmpty(message = "调度中心地址不能为空，请配置 xxl.job.admin.addresses")
        private String addresses;
    }

    /**
     * XXL-Job 执行器配置类
     *
     * <p>定义当前应用作为执行器的运行配置，包括注册信息、网络配置和日志配置。</p>
     */
    @Data
    @Valid
    public static class ExecutorProperties {

        /**
         * 默认端口值：-1 表示自动分配可用端口
         *
         * <p>自动端口分配适合容器化部署和多实例部署场景，避免端口冲突问题。</p>
         */
        private static final Integer PORT_DEFAULT = -1;

        /**
         * 默认日志保留天数：30 天
         *
         * <p>平衡磁盘占用和日志追溯需求的推荐值。可以根据实际情况调整，-1 表示永久保留（不推荐）。</p>
         */
        private static final Integer LOG_RETENTION_DAYS_DEFAULT = 30;

        /**
         * 执行器应用名称，用于在调度中心标识不同的执行器
         *
         * <p>执行器 AppName 是执行器在调度中心的唯一标识，调度中心通过 AppName 路由任务到对应的执行器集群。</p>
         *
         * <p><strong>命名规范：</strong></p>
         * <ul>
         *     <li>建议使用 ${spring.application.name} 自动获取</li>
         *     <li>同一应用的多个实例应使用相同的 AppName</li>
         *     <li>不同应用应使用不同的 AppName</li>
         *     <li>只能包含字母、数字、下划线和中划线</li>
         * </ul>
         *
         * <p><strong>配置示例：</strong></p>
         * <pre>{@code
         * # 推荐：自动获取应用名
         * app-name: ${spring.application.name}
         *
         * # 或者手动指定
         * app-name: refinex-order-service
         * }</pre>
         *
         * <p><strong>注意：</strong>AppName 必须在调度中心预先配置执行器信息。</p>
         */
        @NotEmpty(message = "执行器应用名不能为空，请配置 xxl.job.executor.app-name")
        private String appName;

        /**
         * 执行器 IP 地址
         *
         * <p>指定执行器注册到调度中心时使用的 IP 地址。如果不配置，XXL-Job 会自动获取本机 IP。</p>
         *
         * <p><strong>使用场景：</strong></p>
         * <ul>
         *     <li><strong>不配置（推荐）：</strong>自动获取本机 IP，适用于大多数场景</li>
         *     <li><strong>手动配置：</strong>多网卡环境，需要指定特定网卡的 IP</li>
         *     <li><strong>手动配置：</strong>容器环境，需要指定宿主机或 Pod IP</li>
         * </ul>
         *
         * <p><strong>配置示例：</strong></p>
         * <pre>{@code
         * # 不配置，自动获取（推荐）
         * ip:
         *
         * # 手动指定（特殊场景）
         * ip: 192.168.1.100
         * }</pre>
         */
        private String ip;

        /**
         * 执行器端口号
         *
         * <p>执行器用于接收调度中心任务调度请求的端口。</p>
         *
         * <p><strong>端口配置：</strong></p>
         * <ul>
         *     <li><strong>-1（默认）：</strong>自动分配可用端口，推荐容器化部署使用</li>
         *     <li><strong>固定端口：</strong>指定具体端口号，便于防火墙配置和运维管理</li>
         * </ul>
         *
         * <p><strong>配置示例：</strong></p>
         * <pre>{@code
         * # 自动分配（推荐容器环境）
         * port: -1
         *
         * # 固定端口（推荐传统部署）
         * port: 9999
         * }</pre>
         *
         * <p><strong>注意事项：</strong></p>
         * <ul>
         *     <li>使用固定端口时，需确保端口未被占用</li>
         *     <li>多实例部署时，建议使用自动分配避免冲突</li>
         *     <li>防火墙需放行该端口</li>
         * </ul>
         */
        @Min(value = -1, message = "执行器端口必须大于等于 -1，-1 表示自动分配")
        private Integer port = PORT_DEFAULT;

        /**
         * 执行日志存储路径
         *
         * <p>XXL-Job 任务执行日志的存储目录，用于记录任务执行详情、异常信息、执行结果等，便于问题排查和审计。</p>
         *
         * <p><strong>路径配置：</strong></p>
         * <ul>
         *     <li><strong>相对路径：</strong>相对于应用启动目录，如 ./logs/xxl-job</li>
         *     <li><strong>绝对路径：</strong>指定完整路径，如 /var/logs/xxl-job</li>
         * </ul>
         *
         * <p><strong>配置示例：</strong></p>
         * <pre>{@code
         * # 相对路径（开发环境）
         * log-path: ./logs/xxl-job
         *
         * # 绝对路径（生产环境推荐）
         * log-path: /data/logs/xxl-job/${spring.application.name}
         * }</pre>
         *
         * <p><strong>运维建议：</strong></p>
         * <ul>
         *     <li>生产环境使用绝对路径，便于统一日志管理</li>
         *     <li>日志目录需要有足够的磁盘空间</li>
         *     <li>建议将日志路径挂载到独立磁盘或存储卷</li>
         *     <li>定期备份和归档重要日志</li>
         *     <li>配置合理的日志保留天数，避免磁盘占满</li>
         * </ul>
         */
        @NotEmpty(message = "执行日志路径不能为空，请配置 xxl.job.executor.log-path")
        private String logPath;

        /**
         * 日志文件保留天数
         *
         * <p>自动清理超过指定天数的历史日志文件，避免磁盘空间耗尽。</p>
         *
         * <p><strong>配置说明：</strong></p>
         * <ul>
         *     <li><strong>正数（如 30）：</strong>保留最近 N 天的日志，自动清理过期日志</li>
         *     <li><strong>-1：</strong>永久保留所有日志（不推荐，会导致磁盘占满）</li>
         *     <li><strong>0：</strong>不保留日志（不推荐，无法排查问题）</li>
         * </ul>
         *
         * <p><strong>配置建议：</strong></p>
         * <ul>
         *     <li><strong>开发环境：</strong>7-15 天，节省磁盘空间</li>
         *     <li><strong>测试环境：</strong>15-30 天</li>
         *     <li><strong>生产环境：</strong>30-90 天，根据合规要求调整</li>
         *     <li><strong>审计要求：</strong>根据行业规范配置（如金融行业可能要求 180 天）</li>
         * </ul>
         *
         * <p><strong>配置示例：</strong></p>
         * <pre>{@code
         * # 保留 30 天（默认推荐）
         * log-retention-days: 30
         *
         * # 保留 90 天（审计要求）
         * log-retention-days: 90
         *
         * # 永久保留（不推荐）
         * log-retention-days: -1
         * }</pre>
         *
         * <p><strong>注意事项：</strong></p>
         * <ul>
         *     <li>日志清理在每天凌晨执行</li>
         *     <li>重要日志建议归档到独立存储系统</li>
         *     <li>配置前需评估磁盘空间是否充足</li>
         * </ul>
         */
        @Min(value = -1, message = "日志保留天数必须大于等于 -1，-1 表示永久保留")
        private Integer logRetentionDays = LOG_RETENTION_DAYS_DEFAULT;
    }
}
