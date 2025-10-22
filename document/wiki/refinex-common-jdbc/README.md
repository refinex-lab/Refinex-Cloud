# Refinex Common JDBC

`refinex-common-jdbc` 是 `Refinex` 项目的 JDBC 模块，提供了基于 `NamedParameterJdbcTemplate` 的 JDBC 相关的工具类和回调函数。提供简洁、高效、可靠的数据库访问能力。

> 💡 之所以不使用 MyBatis、MyBatis Plus 等 ORM 框架，一方面是因为本人对 MyBatis Plus 熟悉度并不是很高，实际工作中使用较少。
> 另一方面，我更喜欢明确的看到每个 SQL 细节而不是隐藏在 ORM 框架中的黑盒。这一点仁者见仁，智者见智。如果朋友们更加善于使用 ORM 框架，
> 也可以考虑使用 MyBatis、MyBatis Plus 等 ORM 框架。只需要维护自己的 `refinex-common-mybatis` 模块即可。

如果需要封装 MyBatis Plus ORM 框架，下面推荐两个大佬的优秀的开源项目中封装的开源模块:

- [艿芋的 yudao-spring-boot-starter-mybatis](https://github.com/YunaiV/yudao-cloud/tree/master/yudao-framework/yudao-spring-boot-starter-mybatis)
- [疯狂的狮子Li 的 ruoyi-common-mybatis](https://gitee.com/dromara/RuoYi-Cloud-Plus/tree/2.X/ruoyi-common/ruoyi-common-mybatis)

## 功能特性

### 核心功能
- ✅ 基础 CRUD 操作封装
- ✅ 分页查询支持
- ✅ 事务管理（编程式）
- ✅ 批量操作优化
- ✅ 存储过程调用
- ✅ 命名 SQL 管理

### 高级级特性
- ✅ SQL 日志记录（支持文本和 JSON 格式）
- ✅ 慢查询监控
- ✅ 敏感数据脱敏
- ✅ 列名自动转小写
- ✅ 列名冲突检测
- ✅ 资源自动管理
- ✅ 异常统一处理
- ✅ 多数据库方言支持（MySQL、Oracle、PostgreSQL）

## 快速开始

### 1. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependency>
    <groupId>cn.refinex</groupId>
    <artifactId>refinex-common-jdbc</artifactId>
</dependency>
```

### 2. 配置数据源

在 `application.yml` 文件中配置数据源：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

refinex:
  jdbc:
    # 是否启用 SQL 日志
    enable-sql-log: true
    # 慢查询阈值（毫秒）
    slow-query-threshold-ms: 1000
    # 敏感关键字（会被脱敏）
    sensitive-keys:
      - password
      - pwd
      - secret
      - token
    # 脱敏掩码
    mask-value: "******"
    # 是否启用列名转小写
    lower-case-column-names: true
    # 数据库类型
    database-type: mysql
    # 日志格式（text 或 json）
    log-format: text
```

### 3. 基本使用

使用示例:

```java
@Service
public class UserService {
    
    // 自动注入 JdbcTemplateManager
    private JdbcTemplateManager jdbcManager;
    
    @Autowired
    public UserService(JdbcTemplateManager jdbcManager) {
        this.jdbcManager = jdbcManager;
    }
    
    // 查询单个对象
    public User getUserById(Long id) {
        String sql = "SELECT * FROM user WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return jdbcManager.queryObject(sql, params, User.class);
    }
    
    // 查询列表
    public List<User> listUsers() {
        String sql = "SELECT * FROM user";
        return jdbcManager.queryList(sql, Collections.emptyMap(), User.class);
    }
    
    // 分页查询
    public PageResult<User> pageUsers(int pageNum, int pageSize) {
        String sql = "SELECT * FROM user";
        PageRequest pageRequest = new PageRequest(pageNum, pageSize);
        return jdbcManager.queryPage(sql, Collections.emptyMap(), pageRequest, User.class);
    }
    
    // 新增
    public long createUser(User user) {
        String sql = "INSERT INTO user (name, email, age) VALUES (:name, :email, :age)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", user.getName());
        params.addValue("email", user.getEmail());
        params.addValue("age", user.getAge());
        return jdbcManager.insertAndGetKey(sql, params);
    }
    
    // 更新
    public int updateUser(User user) {
        String sql = "UPDATE user SET name = :name, email = :email WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", user.getId());
        params.put("name", user.getName());
        params.put("email", user.getEmail());
        return jdbcManager.update(sql, params);
    }
    
    // 删除
    public int deleteUser(Long id) {
        String sql = "DELETE FROM user WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return jdbcManager.delete(sql, params);
    }
}
```

## 高级功能

### 事务管理

```java
@Service
public class OrderService {
    
    private JdbcTemplateManager jdbcManager;
    
    @Autowired
    public OrderService(JdbcTemplateManager jdbcManager) {
        this.jdbcManager = jdbcManager;
    }
    
    // 使用事务模板方法（推荐）
    public Long createOrder(Order order, List<OrderItem> items) {
        return jdbcManager.executeInTransaction(manager -> {
            // 插入订单
            String orderSql = "INSERT INTO orders (user_id, total_amount) VALUES (:userId, :totalAmount)";
            MapSqlParameterSource orderParams = new MapSqlParameterSource();
            orderParams.addValue("userId", order.getUserId());
            orderParams.addValue("totalAmount", order.getTotalAmount());
            long orderId = manager.insertAndGetKey(orderSql, orderParams);
            
            // 批量插入订单项
            String itemSql = "INSERT INTO order_item (order_id, product_id, quantity, price) " +
                           "VALUES (:orderId, :productId, :quantity, :price)";
            SqlParameterSource[] batchParams = items.stream()
                .map(item -> {
                    MapSqlParameterSource params = new MapSqlParameterSource();
                    params.addValue("orderId", orderId);
                    params.addValue("productId", item.getProductId());
                    params.addValue("quantity", item.getQuantity());
                    params.addValue("price", item.getPrice());
                    return params;
                })
                .toArray(SqlParameterSource[]::new);
            manager.batchUpdate(itemSql, batchParams);
            
            return orderId;
        });
    }
    
    // 手动管理事务
    public void manualTransaction() {
        TransactionStatus status = jdbcManager.getTransactionStatus();
        try {
            // 执行数据库操作
            jdbcManager.update("UPDATE ...", params);
            jdbcManager.insert("INSERT ...", params);
            
            // 提交事务
            jdbcManager.commit(status);
        } catch (Exception e) {
            // 回滚事务
            jdbcManager.rollback(status);
            throw e;
        }
    }
}
```

### 批量操作

```java
// 批量插入
public void batchInsertUsers(List<User> users) {
    String sql = "INSERT INTO user (name, email, age) VALUES (:name, :email, :age)";
    
    SqlParameterSource[] batchParams = users.stream()
        .map(user -> {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("name", user.getName());
            params.addValue("email", user.getEmail());
            params.addValue("age", user.getAge());
            return params;
        })
        .toArray(SqlParameterSource[]::new);
    
    jdbcManager.batchUpdate(sql, batchParams);
}
```

### 处理 InputStream（推荐方式）

```java
// 查询 BLOB 并自动管理资源
public byte[] downloadFile(Long fileId) {
    String sql = "SELECT content_data FROM file_content WHERE id = :id";
    Map<String, Object> params = Collections.singletonMap("id", fileId);
    
    return jdbcManager.queryInputStreamWithCallback(sql, params, inputStream -> {
        // 在回调中处理流，框架会自动关闭
        return IOUtils.toByteArray(inputStream);
    });
}
```

### 命名 SQL

```java
@Configuration
public class SqlConfiguration {
    
    private NamedSqlManager namedSqlManager;
    
    @Autowired
    public SqlConfiguration(NamedSqlManager namedSqlManager) {
        this.namedSqlManager = namedSqlManager;
    }
    
    @PostConstruct // 初始化时注册 SQL
    public void registerSqls() {
        // 注册命名 SQL
        namedSqlManager.register(new SqlDefinition(
            "user.findById",
            "SELECT * FROM user WHERE id = :id",
            "根据 ID 查询用户"
        ));
        
        namedSqlManager.register(new SqlDefinition(
            "user.findByEmail",
            "SELECT * FROM user WHERE email = :email",
            "根据邮箱查询用户"
        ));
    }
}

@Service
public class UserService {
    
    private JdbcTemplateManager jdbcManager;
    
    @Autowired
    public UserService(JdbcTemplateManager jdbcManager) {
        this.jdbcManager = jdbcManager;
    }
    
    public User findById(Long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        // 使用命名 SQL 查询
        return jdbcManager.queryObjectByName("user.findById", params, User.class);
    }
}
```

### 存储过程调用

```java
public Map<String, Object> callUserStatistics(Long userId) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    return jdbcManager.callProcedure("sp_user_statistics", params);
}
```

### 单列查询

```java
// 查询所有用户 ID
public List<Long> getAllUserIds() {
    String sql = "SELECT id FROM user";
    return jdbcManager.queryColumn(sql, Collections.emptyMap(), Long.class);
}

// 查询所有用户名
public List<String> getAllUserNames() {
    String sql = "SELECT name FROM user WHERE status = :status";
    Map<String, Object> params = Collections.singletonMap("status", 1);
    return jdbcManager.queryColumn(sql, params, String.class);
}
```

## 与 MyBatis Plus 多数据源集成

### 方案一：使用 dynamic-datasource-spring-boot-starter

#### 1.  添加依赖

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.14</version>
</dependency>
```

#### 2. 配置多数据源

```yaml
spring:
  datasource:
    dynamic:
      primary: master
      strict: false
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/master_db
          username: root
          password: password
          driver-class-name: com.mysql.cj.jdbc.Driver
        slave:
          url: jdbc:mysql://localhost:3306/slave_db
          username: root
          password: password
          driver-class-name: com.mysql.cj.jdbc.Driver
```

#### 3. 配置多个 JdbcTemplateManager

```java
@Configuration
public class MultiDataSourceJdbcConfiguration {
    
    @Bean
    @Primary
    public JdbcTemplateManager masterJdbcTemplateManager(@Qualifier("masterJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new JdbcTemplateManager(new NamedParameterJdbcTemplate(jdbcTemplate));
    }
    
    @Bean
    public JdbcTemplateManager slaveJdbcTemplateManager(@Qualifier("slaveJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new JdbcTemplateManager(new NamedParameterJdbcTemplate(jdbcTemplate));
    }
    
    @Bean
    @Primary
    public JdbcTemplate masterJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    @Bean
    public JdbcTemplate slaveJdbcTemplate(@Qualifier("slaveDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
```

#### 4. 使用 @DS 注解切换数据源

```java
@Service
public class UserService {
    
    @Autowired
    @Qualifier("masterJdbcTemplateManager")
    private JdbcTemplateManager masterManager;
    
    @Autowired
    @Qualifier("slaveJdbcTemplateManager")
    private JdbcTemplateManager slaveManager;
    
    // 或者使用 @DS 注解
    @DS("master")
    public void writeToMaster() {
        // 操作主库
        masterManager.insert(sql, params);
    }
    
    @DS("slave")
    public List<User> readFromSlave() {
        // 操作从库
        return slaveManager.queryList(sql, params, User.class);
    }
}
```

### 方案二：直接使用注入的不同 Manager

```java
@Service
public class ReportService {
    
    @Resource(name = "masterJdbcTemplateManager")
    private JdbcTemplateManager masterManager;
    
    @Resource(name = "slaveJdbcTemplateManager")
    private JdbcTemplateManager slaveManager;
    
    public void generateReport() {
        // 从从库读取数据（减轻主库压力）
        List<Map<String, Object>> data = slaveManager.queryList(
            "SELECT * FROM orders WHERE create_time > :startTime",
            params
        );
        
        // 写入主库
        masterManager.insert(
            "INSERT INTO reports (name, data) VALUES (:name, :data)",
            reportParams
        );
    }
}
```