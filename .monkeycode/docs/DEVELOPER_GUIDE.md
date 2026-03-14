# 开发指南

## 环境要求

### 前端开发环境

| 工具 | 版本要求 | 说明 |
|------|----------|------|
| Node.js | >= 20.0.0 | 推荐使用 nvm 管理 |
| pnpm | >= 8.0.0 | 包管理器 |
| Vite | >= 5.0.0 | 构建工具 |
| Vue | >= 3.4.0 | 前端框架 |
| TypeScript | >= 5.0.0 | 类型系统 |
| IDE | VS Code | 推荐使用 WebStorm |

### 后端开发环境

| 工具 | 版本要求 | 说明 |
|------|----------|------|
| JDK | >= 17 | Java 17 LTS |
| Maven | >= 3.9.0 | 项目构建 |
| MySQL | >= 8.0.0 | 主数据库 |
| Redis | >= 7.0.0 | 缓存数据库 |
| IDE | IDEA | 推荐使用 IDEA |

### 物联网开发环境

| 工具 | 版本要求 | 说明 |
|------|----------|------|
| Node.js | >= 20.0.0 | 运行时 |
| PM2 | >= 5.0.0 | 进程管理 |
| MQTT Broker | EMQX/Mosquitto | 消息中间件 |

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd boiler-smart-system
```

### 2. 前端项目启动

```bash
cd frontend

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

前端服务默认运行在 `http://localhost:5173`

### 3. 后端项目启动

```bash
cd backend

# 配置数据库连接
# 修改 src/main/resources/application.yml

# 启动应用
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 4. 物联网网关启动

```bash
cd iot-gateway

# 安装依赖
npm install

# 启动服务
npm run dev
```

物联网服务默认运行在 `http://localhost:3000`

### 5. 配置 MQTT

```bash
# 使用 Docker 启动 EMQX
docker run -d --name emqx -p 1883:1883 -p 8083:8083 -p 8084:8084 -p 18083:18083 emqx/emqx:latest
```

## 项目结构说明

### 前端目录结构

```
frontend/src/
├── api/                    # API 接口
│   ├── index.ts           # Axios 配置
│   ├── heatUser.ts        # 热用户接口
│   ├── balance.ts         # 二网平衡接口
│   ├── station.ts         # 换热站接口
│   ├── heatSource.ts      # 热源接口
│   ├── simulation.ts      # 仿真接口
│   └── actuator.ts        # 执行器接口
│
├── components/             # 公共组件
│   ├── common/            # 通用组件
│   ├── chart/             # 图表组件
│   └── control/           # 控制组件
│
├── composables/           # 组合式 API
│   ├── useWebSocket.ts    # WebSocket 连接
│   └── usePermission.ts   # 权限控制
│
├── layouts/               # 布局组件
│   ├── MainLayout.vue     # 主布局
│   └── BlankLayout.vue    # 空布局
│
├── router/                # 路由配置
│   ├── index.ts           # 路由定义
│   └── guard.ts           # 路由守卫
│
├── stores/                # Pinia 状态管理
│   ├── user.ts            # 用户状态
│   ├── station.ts         # 换热站状态
│   └── realtime.ts       # 实时数据状态
│
├── styles/                # 全局样式
│   ├── variables.scss     # 样式变量
│   └── common.scss       # 通用样式
│
├── types/                 # TypeScript 类型
│   ├── api.d.ts          # API 类型
│   ├── entity.d.ts       # 实体类型
│   └── index.d.ts        # 公共类型
│
├── utils/                 # 工具函数
│   ├── date.ts           # 日期处理
│   ├── number.ts         # 数字格式化
│   └── validation.ts     # 数据校验
│
└── views/                 # 页面视图
    ├── dashboard/        # 监控大屏
    ├── heat-user/         # 热用户管理
    ├── balance/           # 二网平衡
    ├── station/          # 换热站
    ├── primary-network/  # 一网调度
    ├── heat-source/      # 热源调控
    ├── simulation/        # 水力仿真
    └── control/           # 智能调节
```

### 后端目录结构

```
backend/src/main/java/com/boiler/
├── common/                # 公共模块
│   ├── annotation/        # 自定义注解
│   │   └── RateLimiter.java
│   ├── config/            # 配置类
│   │   ├── RedisConfig.java
│   │   └── SecurityConfig.java
│   ├── constant/         # 常量定义
│   ├── enums/             # 枚举类
│   │   └── ErrorCode.java
│   ├── exception/         # 异常处理
│   │   └── GlobalException.java
│   └── utils/             # 工具类
│
├── controller/            # 控制器层
│   ├── HeatUserController.java
│   ├── BalanceController.java
│   ├── StationController.java
│   └── ...
│
├── service/               # 业务逻辑层
│   ├── impl/             # 实现类
│   └── IHeatUserService.java
│
├── mapper/                # 数据访问层
│   ├── HeatUserMapper.java
│   └── HeatUserMapper.xml
│
├── entity/                # 实体类
│   ├── HeatUser.java
│   ├── Station.java
│   └── ...
│
├── dto/                   # 数据传输对象
│   ├── HeatUserDTO.java
│   └── ...
│
└── vo/                    # 视图对象
    ├── Result.java
    └── PageVO.java
```

## 开发规范

### 前端规范

#### 命名规范

- **组件名**: 大驼峰 (PascalCase)，如 `TemperatureChart.vue`
- **文件名**: 小驼峰 (camelCase)，如 `useWebSocket.ts`
- **CSS 类名**: 短横线分隔 (kebab-case)，如 `.content-wrapper`
- **常量**: 全大写下划线，如 `MAX_TEMPERATURE`
- **接口/类型**: 大驼峰，如 `HeatUserInfo`

#### 代码风格

- 使用 TypeScript 进行类型定义
- 组件采用 `<script setup>` 语法
- 使用 Pinia 进行状态管理
- 样式使用 SCSS
- 使用 ESLint + Prettier 进行代码检查

#### 目录规范

```
# 页面组件放在 views 目录
views/heat-user/
├── HeatUserList.vue       # 列表页
├── HeatUserDetail.vue     # 详情页
└── index.ts               # 导出入口

# 业务组件放在 components 目录对应模块
components/heat-user/
├── TemperatureCard.vue
└── UserTable.vue
```

### 后端规范

#### 命名规范

- **类名**: 大驼峰，如 `HeatUserService`
- **方法名**: 小驼峰，如 `getUserById`
- **常量**: 全大写下划线，如 `MAX_RETRY_TIMES`
- **包名**: 全小写，如 `com.boiler.controller`

#### 分层规范

```
controller/     # 负责接收请求、参数校验、返回响应
  - 不应包含业务逻辑
  - 只调用 service 层方法

service/        # 负责业务逻辑处理
  - 处理业务规则
  - 事务管理
  - 调用 mapper 层

mapper/         # 负责数据访问
  - 数据库 CRUD 操作
  - 不包含业务逻辑

entity/         # 数据库实体
  - 与数据库表一一对应
```

#### 日志规范

```java
// 使用 SLF4J
private static final Logger log = LoggerFactory.getLogger(XXX.class);

// 日志级别
log.debug("调试信息");    // 开发调试
log.info("业务信息");     // 业务流程
log.warn("警告信息");     // 需要注意
log.error("错误信息", e); // 异常错误
```

### 数据库规范

#### 表名命名

- 使用小写字母
- 单词间用下划线分隔
- 加上模块前缀，如 `ht_` (heat user)

#### 字段命名

- 小驼峰命名
- 必须包含 `id`, `create_time`, `update_time` 字段
- 软删除使用 `deleted` 字段

#### 索引规范

- 主键索引: `pk_表名`
- 唯一索引: `uk_字段名`
- 普通索引: `idx_字段名`

### Git 提交规范

#### 提交类型

| 类型 | 说明 |
|------|------|
| feat | 新功能 |
| fix | Bug 修复 |
| docs | 文档更新 |
| style | 代码格式 |
| refactor | 代码重构 |
| test | 测试相关 |
| chore | 构建/工具链 |

#### 提交格式

```
feat(heat-user): 添加室内温度预测功能

- 集成 LSTM 温度预测模型
- 添加预测结果展示组件
- 优化历史数据查询性能

Closes #123
```

## 常见任务

### 添加新功能模块

1. **前端**
   - 在 `views/` 下创建页面目录
   - 在 `api/` 下创建接口文件
   - 在 `stores/` 下创建状态管理
   - 在 `router/` 下添加路由

2. **后端**
   - 在 `entity/` 下创建实体类
   - 在 `mapper/` 下创建 Mapper 接口
   - 在 `service/` 下创建 Service 接口和实现
   - 在 `controller/` 下创建 Controller

### 添加新设备支持

1. 在 `iot-gateway/src/devices/` 下创建设备驱动
2. 实现设备通信协议
3. 在后端添加设备管理接口
4. 在前端添加设备监控页面

### 添加新算法

1. 在 `backend/src/main/java/com/boiler/service/algorithm/` 下创建算法模块
2. 实现算法接口
3. 在对应 Service 中调用算法
4. 添加单元测试

## 构建与发布

### 前端构建

```bash
# 开发环境构建
pnpm build:dev

# 生产环境构建
pnpm build:prod

# 分析打包结果
pnpm build:analyze
```

### 后端构建

```bash
# 构建项目
mvn clean package

# 跳过测试
mvn clean package -DskipTests

# 运行测试
mvn test
```

### 物联网网关构建

```bash
# 构建项目
npm run build

# 使用 PM2 启动生产服务
pm2 start ecosystem.config.js
```

### Docker 部署

```bash
# 构建前端镜像
docker build -t boiler-frontend ./frontend

# 构建后端镜像
docker build -t boiler-backend ./backend

# 构建 IoT 镜像
docker build -t boiler-iot ./iot-gateway

# 启动所有服务
docker-compose up -d
```

## 注意事项

1. **敏感信息**: 配置文件中的密码、密钥等敏感信息使用环境变量或加密配置
2. **数据库迁移**: 使用 Flyway 进行数据库版本管理
3. **接口版本**: API 接口添加版本号，如 `/api/v1/`
4. **前端兼容**: 确保 IE11+ 兼容（如需要）
5. **日志审计**: 关键操作需要记录操作日志
