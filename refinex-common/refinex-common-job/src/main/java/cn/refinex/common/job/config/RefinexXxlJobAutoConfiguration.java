package cn.refinex.common.job.config;

import cn.refinex.common.job.config.properties.RefinexXxlJobProperties;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * XXL-Job 自动配置类
 *
 * <p>该配置类负责自动初始化和配置 XXL-Job 分布式任务调度框架，将当前应用注册为 XXL-Job 执行器，接收调度中心下发的任务并执行。</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *     <li>自动创建和配置 XXL-Job 执行器</li>
 *     <li>向调度中心注册执行器信息</li>
 *     <li>接收并执行调度任务</li>
 *     <li>上报任务执行结果和日志</li>
 *     <li>集成 Spring 定时任务（@Scheduled）</li>
 * </ul>
 *
 * <h3>生效条件：</h3>
 * <ul>
 *     <li>类路径下存在 XxlJobSpringExecutor 类（引入 xxl-job-core 依赖）</li>
 *     <li>配置 xxl.job.enabled=true（默认开启）</li>
 *     <li>配置必需的属性（admin.addresses、executor.app-name 等）</li>
 * </ul>
 *
 * <h3>快速开始：</h3>
 *
 * <h4>1. 添加依赖</h4>
 * <pre>{@code
 * <dependency>
 *     <groupId>cn.refinex</groupId>
 *     <artifactId>refinex-common-job</artifactId>
 * </dependency>
 * }</pre>
 *
 * <h4>2. 配置文件</h4>
 * <pre>{@code
 * xxl:
 *   job:
 *     enabled: true
 *     access-token: default_token
 *     admin:
 *       addresses: http://127.0.0.1:8080/xxl-job-admin
 *     executor:
 *       app-name: ${spring.application.name}
 *       port: -1
 *       log-path: ./logs/xxl-job
 *       log-retention-days: 30
 * }</pre>
 *
 * <h4>3. 编写任务处理器</h4>
 * <pre>{@code
 * @Component
 * public class OrderJobHandler {
 *
 *     @XxlJob("orderProcessJob")
 *     public void processOrder() {
 *         // 处理订单任务
 *         log.info("执行订单处理任务");
 *     }
 * }
 * }</pre>
 *
 * <h4>4. 在调度中心配置任务</h4>
 * <ul>
 *     <li>登录 XXL-Job 调度中心</li>
 *     <li>配置执行器：AppName 与配置文件保持一致</li>
 *     <li>创建任务：JobHandler 填写 @XxlJob 注解的值</li>
 *     <li>配置调度策略：Cron 表达式、执行参数等</li>
 * </ul>
 *
 * <h3>工作原理：</h3>
 * <ol>
 *     <li><strong>启动阶段：</strong>执行器启动时向调度中心注册</li>
 *     <li><strong>心跳保持：</strong>定期向调度中心发送心跳，维持连接</li>
 *     <li><strong>任务调度：</strong>调度中心根据策略触发任务</li>
 *     <li><strong>任务执行：</strong>执行器接收任务并执行 JobHandler</li>
 *     <li><strong>结果上报：</strong>执行完成后上报结果和日志</li>
 *     <li><strong>日志查看：</strong>可在调度中心查看执行日志</li>
 * </ol>
 *
 * <h3>高可用部署：</h3>
 * <p>XXL-Job 执行器支持集群部署，实现任务的高可用和负载均衡：</p>
 * <ul>
 *     <li><strong>故障转移：</strong>某个执行器宕机，任务自动转移到其他执行器</li>
 *     <li><strong>负载均衡：</strong>支持轮询、随机、故障转移等路由策略</li>
 *     <li><strong>分片执行：</strong>大任务可以分片到多个执行器并行处理</li>
 * </ul>
 *
 * <h3>集群部署示例：</h3>
 * <pre>{@code
 * # 实例1
 * xxl.job.executor.ip: 192.168.1.101
 * xxl.job.executor.port: 9999
 *
 * # 实例2
 * xxl.job.executor.ip: 192.168.1.102
 * xxl.job.executor.port: 9999
 *
 * # 实例3
 * xxl.job.executor.ip: 192.168.1.103
 * xxl.job.executor.port: 9999
 * }</pre>
 *
 * <h3>分片任务示例：</h3>
 * <pre>{@code
 * @XxlJob("dataProcessJob")
 * public void processData() {
 *     // 获取分片参数
 *     int shardIndex = XxlJobHelper.getShardIndex();
 *     int shardTotal = XxlJobHelper.getShardTotal();
 *
 *     log.info("当前分片: {}/{}", shardIndex, shardTotal);
 *
 *     // 根据分片处理数据
 *     List<Data> dataList = dataService.getDataBySharding(shardIndex, shardTotal);
 *     dataList.forEach(this::processData);
 * }
 * }</pre>
 *
 * <h3>注意事项：</h3>
 * <ul>
 *     <li><strong>AppName 唯一性：</strong>同一应用的所有实例使用相同的 AppName</li>
 *     <li><strong>端口配置：</strong>集群部署时建议使用固定端口或自动分配</li>
 *     <li><strong>日志存储：</strong>确保日志目录有足够的磁盘空间</li>
 *     <li><strong>网络连通：</strong>执行器需要能访问调度中心，调度中心需要能访问执行器</li>
 *     <li><strong>时间同步：</strong>调度中心和执行器的时间需要保持同步</li>
 *     <li><strong>安全配置：</strong>生产环境务必配置 access-token</li>
 * </ul>
 *
 * <h3>监控和运维：</h3>
 * <ul>
 *     <li>在调度中心查看执行器在线状态</li>
 *     <li>查看任务执行历史和成功率</li>
 *     <li>查看任务执行日志和异常信息</li>
 *     <li>配置任务执行超时告警</li>
 *     <li>配置任务失败重试策略</li>
 * </ul>
 *
 * <h3>Spring 定时任务集成：</h3>
 * <p>该配置类同时启用了 Spring 的 @Scheduled 定时任务功能，可以在同一应用中同时使用 XXL-Job 和 Spring 定时任务。</p>
 *
 * @author Refinex
 * @see XxlJobExecutor
 * @see XxlJobSpringExecutor
 * @see RefinexXxlJobProperties
 * @see <a href="https://www.xuxueli.com/xxl-job/">XXL-Job 官方文档</a>
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(XxlJobSpringExecutor.class)
@ConditionalOnProperty(prefix = "xxl.job", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RefinexXxlJobProperties.class)
@EnableScheduling  // 启用 Spring 自带的 @Scheduled 定时任务
public class RefinexXxlJobAutoConfiguration {

    /**
     * 创建 XXL-Job 执行器 Bean
     *
     * <p>根据配置文件初始化 XXL-Job 执行器，并向调度中心注册。
     * 该方法只有在容器中不存在 XxlJobExecutor Bean 时才会执行，
     * 允许用户自定义执行器配置。</p>
     *
     * <h4>执行器配置说明：</h4>
     * <ul>
     *     <li><strong>adminAddresses：</strong>调度中心地址，支持多个地址（逗号分隔）</li>
     *     <li><strong>appname：</strong>执行器 AppName，用于在调度中心标识执行器</li>
     *     <li><strong>ip：</strong>执行器 IP，不配置则自动获取</li>
     *     <li><strong>port：</strong>执行器端口，-1 表示自动分配</li>
     *     <li><strong>accessToken：</strong>访问令牌，用于安全认证</li>
     *     <li><strong>logPath：</strong>执行日志存储路径</li>
     *     <li><strong>logRetentionDays：</strong>日志保留天数</li>
     * </ul>
     *
     * <h4>自动注册流程：</h4>
     * <ol>
     *     <li>执行器启动时，自动向调度中心发起注册请求</li>
     *     <li>调度中心验证 access-token 和 appname</li>
     *     <li>注册成功后，执行器进入在线状态</li>
     *     <li>定期发送心跳保持连接</li>
     * </ol>
     *
     * <h4>常见问题排查：</h4>
     * <ul>
     *     <li><strong>注册失败：</strong>检查网络连通性、access-token 是否正确</li>
     *     <li><strong>心跳失败：</strong>检查防火墙、执行器端口是否开放</li>
     *     <li><strong>任务不执行：</strong>检查 appname 是否匹配、JobHandler 是否注册</li>
     * </ul>
     *
     * @param properties XXL-Job 配置属性
     * @return XxlJobExecutor 执行器实例
     * @throws IllegalArgumentException 如果必需的配置项缺失或无效
     */
    @Bean
    @ConditionalOnMissingBean
    public XxlJobExecutor xxlJobExecutor(RefinexXxlJobProperties properties) {
        log.info("[XXL-Job 配置] 开始初始化 XXL-Job 执行器");

        try {
            // 获取配置
            RefinexXxlJobProperties.AdminProperties admin = properties.getAdmin();
            RefinexXxlJobProperties.ExecutorProperties executor = properties.getExecutor();

            // 打印配置信息（脱敏）
            log.info("[XXL-Job 配置] 调度中心地址: {}", admin.getAddresses());
            log.info("[XXL-Job 配置] 执行器配置 - AppName: {}, IP: {}, Port: {}",
                    executor.getAppName(),
                    executor.getIp() != null ? executor.getIp() : "自动获取",
                    executor.getPort());
            log.info("[XXL-Job 配置] 日志配置 - 存储路径: {}, 保留天数: {}",
                    executor.getLogPath(),
                    executor.getLogRetentionDays());

            // 创建执行器实例
            XxlJobExecutor xxlJobExecutor = new XxlJobSpringExecutor();

            // 配置执行器网络参数
            xxlJobExecutor.setIp(executor.getIp());
            xxlJobExecutor.setPort(executor.getPort());
            xxlJobExecutor.setAppname(executor.getAppName());

            // 配置日志参数
            xxlJobExecutor.setLogPath(executor.getLogPath());
            xxlJobExecutor.setLogRetentionDays(executor.getLogRetentionDays());

            // 配置调度中心连接参数
            xxlJobExecutor.setAdminAddresses(admin.getAddresses());
            xxlJobExecutor.setAccessToken(properties.getAccessToken());

            log.info("[XXL-Job 配置] XXL-Job 执行器初始化完成");

            // 打印使用提示
            printUsageTips(executor.getAppName());

            return xxlJobExecutor;

        } catch (Exception e) {
            log.error("[XXL-Job 配置] XXL-Job 执行器初始化失败", e);
            throw new IllegalStateException("XXL-Job 执行器初始化失败，请检查配置", e);
        }
    }

    /**
     * 打印使用提示信息
     *
     * @param appName 应用名称
     */
    private void printUsageTips(String appName) {
        log.info("========================================");
        log.info("[XXL-Job 使用提示]");
        log.info("1. 确保调度中心已配置执行器，AppName: {}", appName);
        log.info("2. 使用 @XxlJob(\"jobHandlerName\") 注解定义任务处理器");
        log.info("3. 在调度中心创建任务时，JobHandler 填写注解中的名称");
        log.info("4. 查看执行日志可登录调度中心或查看本地日志文件");
        log.info("========================================");
    }
}
