package cn.refinex.auth.utils.ip;

import cn.refinex.common.utils.ip.Ip2RegionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * IP 解析测试
 *
 * @author Refinex
 * @since 1.0.0
 */
@DisplayName("IP 解析测试")
class Ip2RegionUtilsTest {

    @Test
    @DisplayName("测试公网 IPv4 地址解析")
    void testPublicIp2Region() {
        String ip = "118.31.219.247";
        String region = Ip2RegionUtils.getRegion(ip);
        System.out.println("公网 IP: " + ip + " -> " + region);
        assertNotNull(region);
        assertEquals("中国|浙江省|杭州市|阿里云", region);
    }

    @Test
    @DisplayName("测试内网 IPv4 地址解析")
    void testInternalIp() {
        // 测试 C 类私有地址 192.168.x.x
        String ip1 = "192.168.1.100";
        String region1 = Ip2RegionUtils.getRegion(ip1);
        System.out.println("内网 IP: " + ip1 + " -> " + region1);
        assertNotNull(region1);
        assertEquals("内网IP|内网IP", region1);

        // 测试 A 类私有地址 10.x.x.x
        String ip2 = "10.0.0.1";
        String region2 = Ip2RegionUtils.getRegion(ip2);
        System.out.println("内网 IP: " + ip2 + " -> " + region2);
        assertNotNull(region2);
        assertEquals("内网IP|内网IP", region2);

        // 测试 B 类私有地址 172.16-31.x.x
        String ip3 = "172.16.0.1";
        String region3 = Ip2RegionUtils.getRegion(ip3);
        System.out.println("内网 IP: " + ip3 + " -> " + region3);
        assertNotNull(region3);
        assertEquals("内网IP|内网IP", region3);

        // 测试回环地址
        String ip4 = "127.0.0.1";
        String region4 = Ip2RegionUtils.getRegion(ip4);
        System.out.println("回环 IP: " + ip4 + " -> " + region4);
        assertNotNull(region4);
        assertEquals("内网IP|内网IP", region4);
    }

    @Test
    @DisplayName("测试多个公网 IP 地址解析")
    void testMultiplePublicIps() {
        // 测试 Google DNS
        String googleDns = "8.8.8.8";
        String googleRegion = Ip2RegionUtils.getRegion(googleDns);
        System.out.println("Google DNS: " + googleDns + " -> " + googleRegion);
        assertNotNull(googleRegion);

        // 测试 Cloudflare DNS
        String cloudflareDns = "1.1.1.1";
        String cloudflareRegion = Ip2RegionUtils.getRegion(cloudflareDns);
        System.out.println("Cloudflare DNS: " + cloudflareDns + " -> " + cloudflareRegion);
        assertNotNull(cloudflareRegion);
    }
}
