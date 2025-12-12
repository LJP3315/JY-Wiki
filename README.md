# 金庸小说人物信息管理系统
主要功能，对数据库中的小说人物进行查找以及将人物添加到收藏夹中

提供按照姓名和武功的模糊查找

查找的结果会单独显示一个界面

人物详情界面会显示人物的详细评价、武功列表、选中的武功描述、人物关系列表、选中人物的关系详情


## 运行方式

### 方法1：使用批处理文件（推荐）
直接双击项目根目录下的 `run.bat` 文件即可启动程序。

### 方法2：命令行运行
```bash
java -cp "out;lib/mysql-connector-j-9.5.0.jar" com.jycms.AppMain
```

### 方法3：使用IDE
在IntelliJ IDEA或Eclipse中导入项目，右键运行 `AppMain.java`。

## 默认登录信息
- 用户名：`admin`
- 密码：`123456`

---

## 项目改进建议

### 🔒 安全性改进（重要）

#### 1. 数据库密码安全
**当前问题**：数据库密码明文硬编码在 `DBUtil.java` 中
```java
private static final String PASS = "LiGaB3315";  // ❌ 不安全
```

**改进方案**：
- 使用配置文件（如 `config.properties`）存储敏感信息
- 使用环境变量
- 添加 `.gitignore` 防止配置文件上传到Git

**示例代码**：
```java
// 从配置文件读取
Properties props = new Properties();
props.load(new FileInputStream("config.properties"));
String password = props.getProperty("db.password");
```

#### 2. 用户密码加密
**当前问题**：用户密码明文存储在数据库中
```sql
INSERT INTO `User` (`username`, `password`) VALUES ('admin', '123456');  -- ❌ 明文密码
```

**改进方案**：
- 使用BCrypt、SHA-256等加密算法
- 添加盐值（salt）增强安全性

**示例代码**：
```java
import org.mindrot.jbcrypt.BCrypt;

// 注册时加密
String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

// 登录时验证
boolean isValid = BCrypt.checkpw(inputPassword, hashedPassword);
```

#### 3. SQL注入防护
**当前状态**：✅ 已使用PreparedStatement，防护良好
- 继续保持使用参数化查询
- 避免字符串拼接SQL

### 🎨 用户体验改进

#### 4. 界面美化
**改进建议**：
- 使用现代化UI库（如FlatLaf、Substance）替代默认Swing外观
- 添加图标和Logo
- 优化颜色搭配和字体

**示例**：
```java
// 使用FlatLaf主题
FlatLightLaf.setup();
```

#### 5. 错误提示优化
**当前问题**：数据库连接失败时用户不知道原因

**改进方案**：
- 添加友好的错误提示
- 提供数据库连接测试功能
- 添加日志记录系统

#### 6. 搜索功能增强
**改进建议**：
- 添加高级搜索（多条件组合）
- 支持拼音搜索
- 添加搜索历史记录
- 实现实时搜索提示

### 📊 功能扩展

#### 7. 数据导入导出
**新增功能**：
- 导出收藏列表为Excel/PDF
- 批量导入人物数据
- 数据备份与恢复

#### 8. 用户权限管理
**改进方案**：
- 区分管理员和普通用户
- 管理员可以添加/编辑/删除人物信息
- 普通用户只能查看和收藏

#### 9. 统计分析功能
**新增功能**：
- 最受欢迎人物排行榜
- 武功统计分析
- 人物关系图谱可视化

### 🏗️ 架构优化

#### 10. 数据库连接池
**当前问题**：每次操作都创建新连接，效率低

**改进方案**：
- 使用HikariCP或Druid连接池
- 提高并发性能
- 减少资源消耗

**示例**：
```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl(URL);
config.setUsername(USER);
config.setPassword(PASS);
HikariDataSource dataSource = new HikariDataSource(config);
```

#### 11. 异常处理优化
**当前问题**：异常处理简单，只打印堆栈

**改进方案**：
- 使用日志框架（Log4j、SLF4J）
- 分级记录日志（INFO、WARN、ERROR）
- 添加异常恢复机制

#### 12. 配置文件管理
**改进方案**：
- 将数据库配置、界面配置分离
- 支持多环境配置（开发/测试/生产）
- 使用YAML或Properties文件

### 🧪 测试与文档

#### 13. 单元测试
**改进建议**：
- 为DAO层添加JUnit测试
- 测试边界条件和异常情况
- 使用Mock对象测试

#### 14. 代码文档
**改进建议**：
- 添加JavaDoc注释
- 编写开发文档
- 添加数据库设计文档（ER图）

#### 15. 版本控制优化
**改进建议**：
- 完善 `.gitignore`（排除编译文件、配置文件）
- 使用Git分支管理功能开发
- 编写清晰的commit信息

### 🚀 性能优化

#### 16. 图片加载优化
**改进方案**：
- 使用缓存机制避免重复加载
- 支持图片懒加载
- 压缩图片大小

#### 17. 数据分页
**当前问题**：一次性加载所有数据

**改进方案**：
- 实现分页查询
- 添加"加载更多"功能
- 提高大数据量下的性能

### 💡 创新功能

#### 18. 智能推荐
- 根据收藏历史推荐相似人物
- 基于武功推荐相关人物

#### 19. 社交功能
- 用户评论和评分
- 分享收藏列表
- 好友系统

#### 20. 多语言支持
- 支持中英文切换
- 国际化（i18n）

---

## 优先级建议

### 🔴 高优先级（安全相关）
1. 数据库密码配置化
2. 用户密码加密
3. 完善异常处理

### 🟡 中优先级（用户体验）
4. 界面美化
5. 错误提示优化
6. 搜索功能增强

### 🟢 低优先级（功能扩展）
7. 数据导入导出
8. 统计分析功能
9. 智能推荐

---

## 技术栈升级建议

### 当前技术栈
- Java Swing（桌面GUI）
- JDBC（数据库访问）
- MySQL（数据库）

### 可选升级方向

#### 方向1：现代化桌面应用
- JavaFX（替代Swing，更现代的UI）
- Maven/Gradle（项目管理）
- MyBatis（替代JDBC，更优雅的ORM）

#### 方向2：Web应用
- Spring Boot（后端框架）
- Vue.js/React（前端框架）
- RESTful API（前后端分离）

#### 方向3：移动应用
- Android原生开发
- Flutter跨平台开发

---

## 学习资源

- **设计模式**：《Head First设计模式》
- **数据库优化**：《高性能MySQL》
- **Java进阶**：《Effective Java》
- **Web开发**：Spring Boot官方文档

---

## 贡献指南

欢迎提交Issue和Pull Request来改进这个项目！

## 许可证

本项目仅供学习交流使用。
