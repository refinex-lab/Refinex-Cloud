# IP 工具类

> 官方仓库：[https://gitee.com/lionsoul/ip2region](https://gitee.com/lionsoul/ip2region)

---

## 使用示例

### 1. NetUtils - 网络工具类

#### 1.1 判断 IP 地址类型

```java
// 判断是否为 IPv4
boolean isV4 = NetUtils.isIPv4("192.168.1.1");
// 返回: true

// 判断是否为 IPv6
boolean isV6 = NetUtils.isIPv6("2001:db8::1");
// 返回: true

// 获取 IP 版本
String version = NetUtils.getIPVersion("fe80::1");
// 返回: "IPv6"
```

#### 1.2 判断内网地址

```java
// IPv4 内网判断
boolean isInner = NetUtils.isIPv4InnerIP("192.168.1.1");
// 返回: true

boolean isInner2 = NetUtils.isIPv4InnerIP("8.8.8.8");
// 返回: false

// IPv6 内网判断
boolean isV6Inner = NetUtils.isIPv6InnerIP("fe80::1");
// 返回: true (链路本地地址)

boolean isV6Inner2 = NetUtils.isIPv6InnerIP("fc00::1");
// 返回: true (唯一本地地址)

boolean isV6Inner3 = NetUtils.isIPv6InnerIP("2001:db8::1");
// 返回: false (公网地址)

// 通用方法（自动识别）
boolean isInner3 = NetUtils.isInnerIP("10.0.0.1");
// 返回: true
```

#### 1.3 地址验证和规范化

```java
// 验证 IP 地址是否有效
boolean isValid = NetUtils.isValidIP("192.168.1.1");
// 返回: true

// 判断是否为回环地址
boolean isLoopback = NetUtils.isLoopbackAddress("127.0.0.1");
// 返回: true

boolean isLoopback2 = NetUtils.isLoopbackAddress("::1");
// 返回: true

// 规范化 IPv6 地址
String normalized = NetUtils.normalizeIPv6("::1");
// 返回: "0:0:0:0:0:0:0:1"
```

### 2. Ip2RegionUtils - IP 地址定位工具类

#### 2.1 基本查询

```java
// 查询地域信息（自动识别 IPv4/IPv6）
String region = Ip2RegionUtils.getRegion("202.108.22.5");
// 返回: "中国|0|北京|北京市|联通"

String region2 = Ip2RegionUtils.getRegion("8.8.8.8");
// 返回: "美国|0|0|0|谷歌"

// 仅查询城市
String city = Ip2RegionUtils.getCity("183.247.152.98");
// 返回: "杭州市"

// 获取格式化的真实地址
String address = Ip2RegionUtils.getRealAddress("202.108.22.5");
// 返回: "中国 北京市 联通"
```

#### 2.2 获取详细信息对象

```java
IpRegionInfo info = Ip2RegionUtils.getRegionInfo("183.247.152.98");

System.out.println(info.getCountry());   // 中国
System.out.println(info.getProvince());  // 浙江省
System.out.println(info.getCity());      // 杭州市
System.out.println(info.getIsp());       // 移动
System.out.println(info.getFullAddress()); // 中国 浙江省 杭州市 移动
```

#### 2.3 IPv6 查询

```java
// 查询 IPv6 地址
String ipv6Region = Ip2RegionUtils.searchIPv6("2001:250:1006:dff0::1");
// 返回对应的地域信息

// 或使用通用方法（自动识别）
String region = Ip2RegionUtils.getRegion("2001:250:1006:dff0::1");
```

#### 2.4 其他实用方法

```java
// 检查 IP 是否在数据库中
boolean exists = Ip2RegionUtils.isIPInDatabase("8.8.8.8");
// 返回: true

// 获取数据库版本
String version = Ip2RegionUtils.getDatabaseVersion();
// 返回: "ip2region xdb 2.x"
```