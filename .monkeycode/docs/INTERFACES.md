# 接口定义

## 概述

本系统采用 RESTful API 风格进行前后端数据交互。所有接口均返回 JSON 格式数据，使用 HTTP 状态码表示请求结果。接口分为前端调用接口和物联网设备接口两类。

## 通用规范

### 请求格式

- Content-Type: `application/json`
- 字符编码: `UTF-8`
- 认证方式: Bearer Token (JWT)

### 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1704067200000
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 认证接口

### 登录

```http
POST /api/auth/login
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "admin",
    "roles": ["ADMIN"]
  }
}
```

### 登出

```http
POST /api/auth/logout
```

## 热用户接口

### 获取热用户列表

```http
GET /api/heat-users
```

**查询参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 20 |
| building | string | 否 | 楼栋名称 |
| unit | string | 否 | 单元号 |

### 获取热用户详情

```http
GET /api/heat-users/{id}
```

### 获取室内温度趋势

```http
GET /api/heat-users/{id}/temperature-trend
```

**查询参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startTime | long | 是 | 开始时间戳 |
| endTime | long | 是 | 结束时间戳 |
| interval | string | 否 | 数据间隔 (5m/15m/1h/1d) |

### 获取温度预测数据

```http
GET /api/heat-users/{id}/temperature-prediction
```

**查询参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| hours | int | 否 | 预测小时数，默认 24 |

## 二网平衡接口

### 获取管网拓扑

```http
GET /api/balance/topology
```

### 获取平衡优化方案

```http
GET /api/balance/optimization
```

**查询参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| networkId | string | 是 | 管网ID |

### 执行平衡调节

```http
POST /api/balance/regulate
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| networkId | string | 是 | 管网ID |
| valvePositions | array | 是 | 阀门位置列表 |

**请求示例**

```json
{
  "networkId": "NW001",
  "valvePositions": [
    {"valveId": "V001", "position": 75},
    {"valveId": "V002", "position": 60}
  ]
}
```

### 获取调节历史

```http
GET /api/balance/history
```

## 换热站接口

### 获取换热站列表

```http
GET /api/stations
```

### 获取换热站详情

```http
GET /api/stations/{id}
```

### 获取换热站实时数据

```http
GET /api/stations/{id}/realtime
```

**响应示例**

```json
{
  "code": 200,
  "data": {
    "stationId": "ST001",
    "primaryTemp": 85.5,
    "primaryPressure": 0.6,
    "primaryFlow": 120.5,
    "secondaryTemp": 55.2,
    "secondaryPressure": 0.4,
    "secondaryFlow": 100.3,
    "pumpSpeed": 75,
    "valveOpening": 60,
    "updateTime": 1704067200000
  }
}
```

### 设置换热站控制模式

```http
PUT /api/stations/{id}/control-mode
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| mode | string | 是 | 控制模式 (AUTO/MANUAL) |
| targetTemp | float | 条件 | 目标温度(AUTO模式必填) |
| targetPressure | float | 条件 | 目标压力(AUTO模式必填) |

### 获取控制参数

```http
GET /api/stations/{id}/control-params
```

### 更新控制参数

```http
PUT /api/stations/{id}/control-params
```

## 一网调度接口

### 获取一次网拓扑

```http
GET /api/primary-network/topology
```

### 获取调度方案

```http
GET /api/primary-network/schedule
```

**查询参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| date | string | 是 | 日期 (yyyy-MM-dd) |

### 下发调度指令

```http
POST /api/primary-network/dispatch
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| heatSourceId | string | 是 | 热源ID |
| targetOutput | float | 是 | 目标出力 (MW) |
| scheduleTime | long | 是 | 计划时间 |

## 热源调控接口

### 获取热源列表

```http
GET /api/heat-sources
```

### 获取热源详情

```http
GET /api/heat-sources/{id}
```

### 获取热源实时状态

```http
GET /api/heat-sources/{id}/status
```

**响应示例**

```json
{
  "code": 200,
  "data": {
    "sourceId": "HS001",
    "boilers": [
      {
        "boilerId": "B001",
        "status": "RUNNING",
        "load": 75,
        "efficiency": 92.5,
        "fuelConsumption": 120.5,
        "exhaustTemp": 180.2
      }
    ],
    "totalOutput": 45.5,
    "totalEfficiency": 91.8,
    "updateTime": 1704067200000
  }
}
```

### 获取优化建议

```http
GET /api/heat-sources/{id}/optimization
```

**查询参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| targetOutput | float | 是 | 目标出力 (MW) |

### 设置热源出力

```http
PUT /api/heat-sources/{id}/output
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| targetOutput | float | 是 | 目标出力 (MW) |
| boilerIds | array | 是 | 锅炉ID列表 |

## 水力仿真接口

### 创建仿真任务

```http
POST /api/simulation/hydraulic
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| networkId | string | 是 | 管网ID |
| params | object | 是 | 仿真参数 |

**仿真参数**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| nodeDemands | array | 节点流量需求 |
| pumpCurves | array | 泵特性曲线 |
| pipeRoughness | float | 管道粗糙度 |

### 获取仿真结果

```http
GET /api/simulation/hydraulic/{taskId}
```

### 获取仿真历史

```http
GET /api/simulation/history
```

## 智能调节接口

### 获取执行器列表

```http
GET /api/actuators
```

### 获取执行器状态

```http
GET /api/actuators/{id}/status
```

### 设置目标位置

```http
PUT /api/actuators/{id}/target-position
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| position | float | 是 | 目标位置 (%) |
| mode | string | 是 | 控制模式 (AUTO/MANUAL) |

### 获取控制参数

```http
GET /api/actuators/{id}/pid-params
```

### 更新PID参数

```http
PUT /api/actuators/{id}/pid-params
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| kp | float | 是 | 比例系数 |
| ki | float | 是 | 积分系数 |
| kd | float | 是 | 微分系数 |
| fuzzyRules | object | 否 | 模糊规则 |

### 获取调节日志

```http
GET /api/actuators/{id}/logs
```

**查询参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startTime | long | 是 | 开始时间戳 |
| endTime | long | 是 | 结束时间戳 |

## 实时数据接口

### 订阅实时数据

```http
WebSocket /ws/realtime
```

**消息格式**

```json
{
  "type": "TEMPERATURE",
  "deviceId": "T001",
  "value": 22.5,
  "timestamp": 1704067200000
}
```

**数据类型**

| 类型 | 说明 |
|------|------|
| TEMPERATURE | 温度数据 |
| PRESSURE | 压力数据 |
| FLOW | 流量数据 |
| VALVE | 阀门状态 |
| PUMP | 泵运行状态 |
| ALARM | 报警数据 |

## 报警接口

### 获取报警列表

```http
GET /api/alarms
```

**查询参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| level | string | 否 | 报警级别 (INFO/WARNING/ERROR) |
| status | string | 否 | 状态 (ACTIVE/ACKNOWLEDGED/RESOLVED) |
| startTime | long | 否 | 开始时间 |
| endTime | long | 否 | 结束时间 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |

### 确认报警

```http
PUT /api/alarms/{id}/acknowledge
```

### 解决报警

```http
PUT /api/alarms/{id}/resolve
```

## 物联网设备接口

### 设备注册

```http
POST /api/devices/register
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| deviceType | string | 是 | 设备类型 |
| deviceCode | string | 是 | 设备编码 |
| protocol | string | 是 | 通信协议 (MQTT/MODBUS) |
| config | object | 是 | 设备配置 |

### 设备数据上报

```http
POST /api/devices/{code}/data
```

### 下发控制指令

```http
POST /api/devices/{code}/command
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| command | string | 是 | 指令类型 |
| params | object | 是 | 指令参数 |
| priority | int | 否 | 优先级，默认 5 |

### 获取设备日志

```http
GET /api/devices/{code}/logs
```
