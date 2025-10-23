package cn.refinex.common.utils.net;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * 网络工具类 - 扩展 Hutool NetUtil 功能
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NetUtils {

    /**
     * IPv4 地址正则表达式
     * 匹配格式：0.0.0.0 到 255.255.255.255
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$"
    );

    /**
     * IPv6 地址正则表达式（标准格式和压缩格式）
     * 支持完整格式、零压缩格式、IPv4映射格式等
     */
    private static final Pattern IPV6_STANDARD_PATTERN = Pattern.compile(
            "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$"
    );

    private static final Pattern IPV6_COMPRESSED_PATTERN = Pattern.compile(
            "^(([0-9a-fA-F]{1,4}:){0,7}[0-9a-fA-F]{0,4})?::([0-9a-fA-F]{1,4}:){0,7}[0-9a-fA-F]{0,4}$"
    );

    /**
     * IPv6 私有地址前缀
     * - fe80::/10  链路本地地址
     * - fc00::/7   唯一本地地址 (ULA)
     * - fd00::/8   唯一本地地址 (ULA)
     * - ::1        环回地址
     */
    private static final String[] IPV6_PRIVATE_PREFIXES = {
            "fe80:", "fe90:", "fea0:", "feb0:",    // fe80::/10
            "fc", "fd"                             // fc00::/7
    };

    /**
     * 判断给定的字符串是否为有效的 IPv4 地址
     *
     * <p>验证 IP 地址格式是否符合 IPv4 标准（点分十进制）</p>
     *
     * @param ip 待验证的 IP 地址字符串
     * @return 如果是有效的 IPv4 地址返回 true，否则返回 false
     *
     * <pre>{@code
     * NetUtils.isIPv4("192.168.1.1")     = true
     * NetUtils.isIPv4("2001:db8::1")     = false
     * NetUtils.isIPv4("256.1.1.1")       = false
     * NetUtils.isIPv4("")                = false
     * }</pre>
     */
    public static boolean isIPv4(String ip) {
        if (StrUtil.isBlank(ip)) {
            return false;
        }
        return IPV4_PATTERN.matcher(ip.trim()).matches();
    }

    /**
     * 判断给定的字符串是否为有效的 IPv6 地址
     *
     * <p>支持标准格式、压缩格式（::）以及 IPv4 映射格式</p>
     *
     * @param ip 待验证的 IP 地址字符串
     * @return 如果是有效的 IPv6 地址返回 true，否则返回 false
     *
     * <pre>{@code
     * NetUtils.isIPv6("2001:db8::1")                       = true
     * NetUtils.isIPv6("fe80::1")                           = true
     * NetUtils.isIPv6("::1")                               = true
     * NetUtils.isIPv6("2001:0db8:0000:0000:0000:ff00:0042:8329") = true
     * NetUtils.isIPv6("192.168.1.1")                       = false
     * }</pre>
     */
    public static boolean isIPv6(String ip) {
        if (StrUtil.isBlank(ip)) {
            return false;
        }

        String trimmedIp = ip.trim();

        // 处理带有方括号的 IPv6 地址（例如 [::1]）
        if (trimmedIp.startsWith("[") && trimmedIp.endsWith("]")) {
            trimmedIp = trimmedIp.substring(1, trimmedIp.length() - 1);
        }

        // 处理带有端口号的情况（例如 ::1%eth0）
        int percentIndex = trimmedIp.indexOf('%');
        if (percentIndex != -1) {
            trimmedIp = trimmedIp.substring(0, percentIndex);
        }

        // 检查是否包含 "::"
        if (trimmedIp.contains("::")) {
            // 验证压缩格式
            // "::" 只能出现一次
            if (trimmedIp.indexOf("::") != trimmedIp.lastIndexOf("::")) {
                return false;
            }
            return IPV6_COMPRESSED_PATTERN.matcher(trimmedIp).matches();
        }

        // 验证标准格式
        return IPV6_STANDARD_PATTERN.matcher(trimmedIp).matches();
    }

    /**
     * 判断 IPv6 地址是否为内网（私有）地址
     *
     * <p>包括以下类型：</p>
     * <ul>
     *   <li>链路本地地址（Link-Local）: fe80::/10</li>
     *   <li>唯一本地地址（ULA）: fc00::/7 和 fd00::/8</li>
     *   <li>环回地址（Loopback）: ::1</li>
     * </ul>
     *
     * @param ip IPv6 地址字符串
     * @return 如果是内网地址返回 true，否则返回 false
     *
     * <pre>{@code
     * NetUtils.isIPv6InnerIP("fe80::1")           = true  (链路本地)
     * NetUtils.isIPv6InnerIP("fc00::1")           = true  (唯一本地)
     * NetUtils.isIPv6InnerIP("fd12:3456::1")      = true  (唯一本地)
     * NetUtils.isIPv6InnerIP("::1")               = true  (环回)
     * NetUtils.isIPv6InnerIP("2001:db8::1")       = false (公网地址)
     * }</pre>
     */
    public static boolean isIPv6InnerIP(String ip) {
        if (!isIPv6(ip)) {
            return false;
        }

        try {
            InetAddress address = InetAddress.getByName(ip);

            if (!(address instanceof Inet6Address)) {
                return false;
            }

            // 使用 Java 原生方法判断
            if (address.isLoopbackAddress() || address.isLinkLocalAddress() || address.isSiteLocalAddress()) {
                return true;
            }

            // 额外检查唯一本地地址 (ULA: fc00::/7)
            String normalizedIp = address.getHostAddress().toLowerCase();

            // 移除可能存在的接口标识符（例如 %eth0）
            int percentIndex = normalizedIp.indexOf('%');
            if (percentIndex != -1) {
                normalizedIp = normalizedIp.substring(0, percentIndex);
            }

            // 检查私有前缀
            for (String prefix : IPV6_PRIVATE_PREFIXES) {
                if (normalizedIp.startsWith(prefix)) {
                    return true;
                }
            }

            return false;

        } catch (UnknownHostException e) {
            // 无法解析的地址视为非内网地址
            return false;
        }
    }

    /**
     * 判断 IPv4 地址是否为内网（私有）地址
     *
     * <p>增强版本，除了 Hutool 的 isInnerIP 功能外，添加了更详细的验证</p>
     *
     * <p>包括以下地址段：</p>
     * <ul>
     *   <li>A 类私有地址: 10.0.0.0/8</li>
     *   <li>B 类私有地址: 172.16.0.0/12</li>
     *   <li>C 类私有地址: 192.168.0.0/16</li>
     *   <li>环回地址: 127.0.0.0/8</li>
     *   <li>链路本地地址: 169.254.0.0/16</li>
     * </ul>
     *
     * @param ip IPv4 地址字符串
     * @return 如果是内网地址返回 true，否则返回 false
     *
     * <pre>{@code
     * NetUtils.isIPv4InnerIP("192.168.1.1")   = true
     * NetUtils.isIPv4InnerIP("10.0.0.1")      = true
     * NetUtils.isIPv4InnerIP("172.16.0.1")    = true
     * NetUtils.isIPv4InnerIP("127.0.0.1")     = true
     * NetUtils.isIPv4InnerIP("8.8.8.8")       = false
     * }</pre>
     */
    public static boolean isIPv4InnerIP(String ip) {
        if (!isIPv4(ip)) {
            return false;
        }

        // 优先使用 Hutool 的方法
        if (NetUtil.isInnerIP(ip)) {
            return true;
        }

        try {
            InetAddress address = InetAddress.getByName(ip);

            if (!(address instanceof Inet4Address)) {
                return false;
            }

            // 使用 Java 原生方法进行额外验证
            return address.isSiteLocalAddress() ||
                    address.isLoopbackAddress() ||
                    address.isLinkLocalAddress();

        } catch (UnknownHostException e) {
            return false;
        }
    }

    /**
     * 通用方法：判断 IP 地址是否为内网地址
     *
     * <p>自动识别 IPv4 或 IPv6 并进行相应判断</p>
     *
     * @param ip IP 地址字符串（IPv4 或 IPv6）
     * @return 如果是内网地址返回 true，否则返回 false
     *
     * <pre>{@code
     * NetUtils.isInnerIP("192.168.1.1")   = true
     * NetUtils.isInnerIP("fe80::1")       = true
     * NetUtils.isInnerIP("8.8.8.8")       = false
     * NetUtils.isInnerIP("2001:db8::1")   = false
     * }</pre>
     */
    public static boolean isInnerIP(String ip) {
        if (StrUtil.isBlank(ip)) {
            return false;
        }

        if (isIPv4(ip)) {
            return isIPv4InnerIP(ip);
        } else if (isIPv6(ip)) {
            return isIPv6InnerIP(ip);
        }

        return false;
    }

    /**
     * 获取 IP 地址的版本类型
     *
     * @param ip IP 地址字符串
     * @return "IPv4" 或 "IPv6" 或 "UNKNOWN"
     *
     * <pre>{@code
     * NetUtils.getIPVersion("192.168.1.1")  = "IPv4"
     * NetUtils.getIPVersion("2001:db8::1")  = "IPv6"
     * NetUtils.getIPVersion("invalid")      = "UNKNOWN"
     * }</pre>
     */
    public static String getIPVersion(String ip) {
        if (isIPv4(ip)) {
            return "IPv4";
        } else if (isIPv6(ip)) {
            return "IPv6";
        }
        return "UNKNOWN";
    }

    /**
     * 验证 IP 地址格式是否有效（支持 IPv4 和 IPv6）
     *
     * @param ip IP 地址字符串
     * @return 如果是有效的 IP 地址返回 true，否则返回 false
     *
     * <pre>{@code
     * NetUtils.isValidIP("192.168.1.1")     = true
     * NetUtils.isValidIP("2001:db8::1")     = true
     * NetUtils.isValidIP("invalid")         = false
     * }</pre>
     */
    public static boolean isValidIP(String ip) {
        return isIPv4(ip) || isIPv6(ip);
    }

    /**
     * 规范化 IPv6 地址（展开压缩格式）
     *
     * @param ip IPv6 地址字符串
     * @return 规范化后的 IPv6 地址，如果无法解析则返回原字符串
     *
     * <pre>{@code
     * NetUtils.normalizeIPv6("::1")            = "0:0:0:0:0:0:0:1"
     * NetUtils.normalizeIPv6("2001:db8::1")    = "2001:db8:0:0:0:0:0:1"
     * }</pre>
     */
    public static String normalizeIPv6(String ip) {
        if (!isIPv6(ip)) {
            return ip;
        }

        try {
            InetAddress address = InetAddress.getByName(ip);
            if (address instanceof Inet6Address) {
                return address.getHostAddress();
            }
        } catch (UnknownHostException e) {
            // 解析失败，返回原字符串
        }

        return ip;
    }

    /**
     * 判断是否为本地回环地址（IPv4 或 IPv6）
     *
     * @param ip IP 地址字符串
     * @return 如果是回环地址返回 true，否则返回 false
     *
     * <pre>{@code
     * NetUtils.isLoopbackAddress("127.0.0.1")  = true
     * NetUtils.isLoopbackAddress("::1")        = true
     * NetUtils.isLoopbackAddress("192.168.1.1") = false
     * }</pre>
     */
    public static boolean isLoopbackAddress(String ip) {
        if (!isValidIP(ip)) {
            return false;
        }

        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isLoopbackAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
