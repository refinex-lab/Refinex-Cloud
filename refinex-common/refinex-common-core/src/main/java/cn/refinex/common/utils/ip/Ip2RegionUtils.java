package cn.refinex.common.utils.ip;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.utils.net.NetUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * IP 地址离线定位工具类(基于 ip2region 3.1.1 (xdb) 实现)
 *
 * <p>支持 IPv4 和 IPv6 地址查询</p>
 * <p>使用线程安全的全文件缓存模式</p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Ip2RegionUtils {

    /**
     * IPv4 数据库文件路径（相对于 classpath）
     */
    private static final String IPV4_DB_PATH = "/ip2region/ip2region.xdb";

    /**
     * IPv6 数据库文件路径（相对于 classpath）
     * 注意：需要单独下载 IPv6 数据库文件
     */
    private static final String IPV6_DB_PATH = "/ip2region/ip2region_v6.xdb";

    /**
     * IPv4 查询器实例
     */
    private static Searcher ipv4Searcher;

    /**
     * IPv6 查询器实例
     */
    private static Searcher ipv6Searcher;

    /**
     * 读写锁，保证线程安全
     */
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    /**
     * 初始化标记
     */
    private static volatile boolean ipv4Initialized = false;
    private static volatile boolean ipv6Initialized = false;

    /**
     * 默认返回值（查询失败时）
     */
    private static final String DEFAULT_REGION = "未知|0|0|0|0";

    /**
     * 初始化 IPv4 查询器
     *
     * <p>采用延迟加载策略，首次调用时才加载数据库到内存</p>
     * <p>使用双重检查锁定（DCL）保证线程安全和性能</p>
     */
    private static void initIPv4Searcher() {
        if (!ipv4Initialized) {
            writeLock.lock();
            try {
                if (!ipv4Initialized) {
                    log.info("开始初始化 ip2region IPv4 数据库...");
                    long startTime = System.currentTimeMillis();

                    InputStream inputStream = Ip2RegionUtils.class.getResourceAsStream(IPV4_DB_PATH);
                    if (inputStream == null) {
                        log.error("无法找到 IPv4 数据库文件: {}, 尝试从类路径加载", IPV4_DB_PATH);
                        // 尝试使用不同的加载方式
                        inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ip2region/ip2region.xdb");
                    }
                    
                    if (inputStream == null) {
                        log.error("IPv4 数据库文件不存在: {}", IPV4_DB_PATH);
                        throw new SystemException("IPv4 数据库文件不存在: " + IPV4_DB_PATH);
                    }

                    try {
                        // 将整个 xdb 文件加载到内存
                        byte[] dbBuffer = IoUtil.readBytes(inputStream);
                        log.info("IPv4 数据库文件大小: {} KB", dbBuffer.length / 1024);

                        // 转换为 LongByteArray（ip2region 3.1.1 API 要求）
                        LongByteArray contentBuffer = new LongByteArray();
                        contentBuffer.append(dbBuffer);

                        // 创建基于内存的查询器（无磁盘 IO，性能最优）
                        // ip2region 3.1.1 使用 newWithBuffer(Version, LongByteArray) 方法
                        ipv4Searcher = Searcher.newWithBuffer(Version.IPv4, contentBuffer);

                        ipv4Initialized = true;
                        long costTime = System.currentTimeMillis() - startTime;
                        log.info("ip2region IPv4 数据库初始化完成，耗时: {} ms", costTime);
                        
                        // 测试查询以验证数据库是否正常工作
                        try {
                            String testIp = "8.8.8.8";
                            String testResult = ipv4Searcher.search(testIp);
                            log.info("IPv4 数据库测试查询成功，IP: {}, 结果: {}", testIp, testResult);
                        } catch (Exception e) {
                            log.warn("IPv4 数据库测试查询失败", e);
                        }

                    } catch (IOException e) {
                        log.error("加载 IPv4 数据库文件失败", e);
                        throw new SystemException("加载 IPv4 数据库文件失败", e);
                    } finally {
                        IoUtil.close(inputStream);
                    }
                }
            } finally {
                writeLock.unlock();
            }
        }
    }

    /**
     * 初始化 IPv6 查询器
     *
     * <p>IPv6 数据库需要单独下载，如果文件不存在会记录警告日志</p>
     */
    private static void initIPv6Searcher() {
        if (!ipv6Initialized) {
            writeLock.lock();
            try {
                if (!ipv6Initialized) {
                    log.info("开始初始化 ip2region IPv6 数据库...");
                    long startTime = System.currentTimeMillis();

                    InputStream inputStream = Ip2RegionUtils.class.getResourceAsStream(IPV6_DB_PATH);
                    if (inputStream == null) {
                        log.warn("无法找到 IPv6 数据库文件: {}, 尝试从类路径加载", IPV6_DB_PATH);
                        // 尝试使用不同的加载方式
                        inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ip2region/ip2region_v6.xdb");
                    }
                    
                    if (inputStream == null) {
                        log.warn("未找到 IPv6 数据库文件: {}, IPv6 查询功能将不可用", IPV6_DB_PATH);
                        // 标记为已初始化，避免重复尝试
                        ipv6Initialized = true;
                        return;
                    }

                    try {
                        byte[] dbBuffer = IoUtil.readBytes(inputStream);
                        log.info("IPv6 数据库文件大小: {} KB", dbBuffer.length / 1024);

                        // 转换为 LongByteArray（ip2region 3.1.1 API 要求）
                        LongByteArray contentBuffer = new LongByteArray();
                        contentBuffer.append(dbBuffer);

                        // ip2region 3.1.1 使用 newWithBuffer(Version, LongByteArray) 方法
                        ipv6Searcher = Searcher.newWithBuffer(Version.IPv6, contentBuffer);

                        ipv6Initialized = true;
                        long costTime = System.currentTimeMillis() - startTime;
                        log.info("ip2region IPv6 数据库初始化完成，耗时: {} ms", costTime);
                        
                        // 测试查询以验证数据库是否正常工作
                        try {
                            String testIp = "2001:4860:4860::8888";
                            String testResult = ipv6Searcher.search(testIp);
                            log.info("IPv6 数据库测试查询成功，IP: {}, 结果: {}", testIp, testResult);
                        } catch (Exception e) {
                            log.warn("IPv6 数据库测试查询失败", e);
                        }

                    } catch (IOException e) {
                        log.error("加载 IPv6 数据库文件失败", e);
                    } finally {
                        IoUtil.close(inputStream);
                    }
                }
            } finally {
                writeLock.unlock();
            }
        }
    }

    /**
     * 根据 IP 地址获取地域信息（自动识别 IPv4 或 IPv6）
     *
     * @param ip IP 地址字符串（支持 IPv4 和 IPv6）
     * @return 地域信息字符串，格式：国家|区域|省份|城市|ISP, 查询失败返回：未知|0|0|0|0
     * <p>
     * 注意：如果传入的是内网 IP（如 192.168.x.x、10.x.x.x、172.16-31.x.x 等），
     * 将直接返回 "内网IP|内网IP"，不会查询数据库。
     * <p>
     * 示例:
     * <pre>{@code
     * Ip2RegionUtils.getRegion("202.108.22.5");
     * // 返回: 中国|0|北京|北京市|联通
     *
     * Ip2RegionUtils.getRegion("8.8.8.8");
     * // 返回: 美国|0|0|0|谷歌
     *
     * Ip2RegionUtils.getRegion("192.168.1.100");
     * // 返回: 内网IP|内网IP
     * }</pre>
     */
    public static String getRegion(String ip) {
        if (StrUtil.isBlank(ip)) {
            log.warn("IP 地址为空");
            return DEFAULT_REGION;
        }

        // 检查是否为内网 IP
        if (NetUtils.isInnerIP(ip)) {
            log.debug("检测到内网 IP，返回默认地域信息: {}", ip);
            return "内网IP|内网IP";
        }

        // 自动识别 IP 类型
        if (NetUtils.isIPv4(ip)) {
            return searchIPv4(ip);
        } else if (NetUtils.isIPv6(ip)) {
            return searchIPv6(ip);
        } else {
            log.warn("无效的 IP 地址格式: {}", ip);
            return DEFAULT_REGION;
        }
    }

    /**
     * 查询 IPv4 地址的地域信息
     *
     * @param ip IPv4 地址
     * @return 地域信息字符串
     */
    public static String searchIPv4(String ip) {
        if (!NetUtils.isIPv4(ip)) {
            log.warn("不是有效的 IPv4 地址: {}", ip);
            return DEFAULT_REGION;
        }

        initIPv4Searcher();

        readLock.lock();
        try {
            if (ipv4Searcher != null) {
                String region = ipv4Searcher.search(ip);
                log.debug("IPv4 查询成功: IP={}, Region={}", ip, region);
                return StrUtil.isBlank(region) ? DEFAULT_REGION : region;
            } else {
                log.error("IPv4 查询器未初始化，无法查询 IP: {}", ip);
            }
        } catch (Exception e) {
            log.error("查询 IPv4 地址失败: {}", ip, e);
        } finally {
            readLock.unlock();
        }

        return DEFAULT_REGION;
    }

    /**
     * 查询 IPv6 地址的地域信息
     *
     * @param ip IPv6 地址
     * @return 地域信息字符串
     */
    public static String searchIPv6(String ip) {
        if (!NetUtils.isIPv6(ip)) {
            log.warn("不是有效的 IPv6 地址: {}", ip);
            return DEFAULT_REGION;
        }

        initIPv6Searcher();

        readLock.lock();
        try {
            if (ipv6Searcher != null) {
                String region = ipv6Searcher.search(ip);
                log.debug("IPv6 查询成功: IP={}, Region={}", ip, region);
                return StrUtil.isBlank(region) ? DEFAULT_REGION : region;
            } else {
                log.warn("IPv6 查询器未初始化，请确保已添加 IPv6 数据库文件");
            }
        } catch (Exception e) {
            log.error("查询 IPv6 地址失败: {}", ip, e);
        } finally {
            readLock.unlock();
        }

        return DEFAULT_REGION;
    }

    /**
     * 根据 IP 地址获取城市名称
     *
     * <p>简化的查询方法，只返回城市信息</p>
     *
     * @param ip IP 地址
     * @return 城市名称，如果没有城市信息则返回省份或国家
     *
     * <pre>{@code
     * Ip2RegionUtils.getCity("202.108.22.5");  // 返回: 北京市
     * Ip2RegionUtils.getCity("8.8.8.8");       // 返回: 美国
     * }</pre>
     */
    public static String getCity(String ip) {
        String region = getRegion(ip);
        return parseCity(region);
    }

    /**
     * 根据 IP 地址获取详细的地域信息对象
     *
     * @param ip IP 地址
     * @return 地域信息对象
     *
     * <pre>{@code
     * IpRegionInfo info = Ip2RegionUtils.getRegionInfo("183.247.152.98");
     * System.out.println(info.getCountry());   // 中国
     * System.out.println(info.getProvince());  // 浙江省
     * System.out.println(info.getCity());      // 杭州市
     * System.out.println(info.getIsp());       // 移动
     * }</pre>
     */
    public static IpRegionInfo getRegionInfo(String ip) {
        String region = getRegion(ip);
        return parseRegionInfo(region);
    }

    /**
     * 获取真实地址（格式化后的地址字符串）
     *
     * <p>返回便于阅读的地址格式，自动过滤 "0" 占位符</p>
     *
     * @param ip IP 地址
     * @return 格式化的地址字符串
     *
     * <pre>{@code
     * Ip2RegionUtils.getRealAddress("202.108.22.5");
     * // 返回: 中国 北京市 联通
     *
     * Ip2RegionUtils.getRealAddress("8.8.8.8");
     * // 返回: 美国 谷歌
     * }</pre>
     */
    public static String getRealAddress(String ip) {
        String region = getRegion(ip);
        return formatAddress(region);
    }

    /**
     * 从地域字符串中解析城市名称
     *
     * @param region 地域字符串（格式：国家|区域|省份|城市|ISP）
     * @return 城市名称
     */
    private static String parseCity(String region) {
        if (StrUtil.isBlank(region) || DEFAULT_REGION.equals(region)) {
            return "未知";
        }

        String[] parts = region.split("\\|");
        if (parts.length < 4) {
            return "未知";
        }

        // 优先返回城市，如果城市为 0 则返回省份，都为 0 则返回国家
        String city = parts[3];
        if (!"0".equals(city) && StrUtil.isNotBlank(city)) {
            return city;
        }

        String province = parts[2];
        if (!"0".equals(province) && StrUtil.isNotBlank(province)) {
            return province;
        }

        String country = parts[0];
        return StrUtil.isBlank(country) ? "未知" : country;
    }

    /**
     * 解析地域字符串为结构化对象
     *
     * @param region 地域字符串
     * @return 地域信息对象
     */
    private static IpRegionInfo parseRegionInfo(String region) {
        IpRegionInfo info = new IpRegionInfo();

        if (StrUtil.isBlank(region) || DEFAULT_REGION.equals(region)) {
            info.setCountry("未知");
            info.setRegion("0");
            info.setProvince("0");
            info.setCity("0");
            info.setIsp("0");
            return info;
        }

        String[] parts = region.split("\\|");
        if (parts.length >= 5) {
            info.setCountry(parts[0]);
            info.setRegion(parts[1]);
            info.setProvince(parts[2]);
            info.setCity(parts[3]);
            info.setIsp(parts[4]);
        }

        return info;
    }

    /**
     * 格式化地址信息
     *
     * @param region 地域字符串
     * @return 格式化后的地址
     */
    private static String formatAddress(String region) {
        if (StrUtil.isBlank(region) || DEFAULT_REGION.equals(region)) {
            return "未知";
        }

        String[] parts = region.split("\\|");
        StringBuilder address = new StringBuilder();

        for (String part : parts) {
            if (StrUtil.isNotBlank(part) && !"0".equals(part)) {
                if (!address.isEmpty()) {
                    address.append(" ");
                }
                address.append(part);
            }
        }

        return !address.isEmpty() ? address.toString() : "未知";
    }

    /**
     * 检查 IP 地址是否在数据库中
     *
     * @param ip IP 地址
     * @return 如果能查询到信息返回 true，否则返回 false
     */
    public static boolean isIPInDatabase(String ip) {
        String region = getRegion(ip);
        return !DEFAULT_REGION.equals(region);
    }

    /**
     * 获取数据库版本信息
     *
     * @return 版本信息字符串
     */
    public static String getDatabaseVersion() {
        initIPv4Searcher();
        return "ip2region xdb 2.x";
    }

    /**
     * 关闭查询器，释放资源
     *
     * <p>通常在应用关闭时调用，一般情况下无需手动调用</p>
     */
    public static void close() {
        writeLock.lock();
        try {
            if (ipv4Searcher != null) {
                try {
                    ipv4Searcher.close();
                    log.info("IPv4 查询器已关闭");
                } catch (IOException e) {
                    log.error("关闭 IPv4 查询器失败", e);
                }
                ipv4Searcher = null;
                ipv4Initialized = false;
            }

            if (ipv6Searcher != null) {
                try {
                    ipv6Searcher.close();
                    log.info("IPv6 查询器已关闭");
                } catch (IOException e) {
                    log.error("关闭 IPv6 查询器失败", e);
                }
                ipv6Searcher = null;
                ipv6Initialized = false;
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * IP 地域信息实体类
     */
    @Setter
    @Getter
    public static class IpRegionInfo {

        /**
         * 国家
         */
        private String country;

        /**
         * 区域（通常为 0）
         */
        private String region;

        /**
         * 省份
         */
        private String province;

        /**
         * 城市
         */
        private String city;

        /**
         * ISP 运营商
         */
        private String isp;

        public IpRegionInfo() {
        }

        /**
         * 获取完整的地址字符串（自动过滤 0）
         */
        public String getFullAddress() {
            StringBuilder sb = new StringBuilder();
            appendIfNotEmpty(sb, country);
            appendIfNotEmpty(sb, province);
            appendIfNotEmpty(sb, city);
            appendIfNotEmpty(sb, isp);
            return !sb.isEmpty() ? sb.toString() : "未知";
        }

        private void appendIfNotEmpty(StringBuilder sb, String value) {
            if (StrUtil.isNotBlank(value) && !"0".equals(value)) {
                if (!sb.isEmpty()) {
                    sb.append(" ");
                }
                sb.append(value);
            }
        }

        @Override
        public String toString() {
            return "IpRegionInfo{" +
                    "country='" + country + '\'' +
                    ", region='" + region + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", isp='" + isp + '\'' +
                    '}';
        }
    }
}
