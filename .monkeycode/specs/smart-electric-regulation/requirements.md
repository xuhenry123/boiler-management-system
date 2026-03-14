# Requirements Document

## Introduction

本文档描述智能电动调节功能模块的需求规格。该模块通过模糊PID控制算法实现电动执行器的智能调节，根据管网实际运行参数自动调整阀门开度，解决传统PID控制响应慢、超调大的问题，实现精确调节。

## Glossary

- **电动执行器 (Electric Actuator)**: 接收控制信号驱动阀门开启或关闭的电动装置
- **模糊PID控制 (Fuzzy PID Control)**: 结合模糊逻辑与PID控制的智能控制算法
- **管网参数 (Network Parameters)**: 管网运行过程中的温度、压力、流量等实时数据
- **阀门开度 (Valve Opening)**: 阀门开启程度的百分比表示
- **超调量 (Overshoot)**: 系统响应超过目标值的最大偏差
- **响应时间 (Response Time)**: 系统从响应变化到达到稳定状态所需时间
- **稳态误差 (Steady-state Error)**: 系统稳定后与目标值的偏差
- **MQTT**: 轻量级物联网消息传输协议
- **Modbus**: 工业设备通信协议
- **二网平衡 (Secondary Network Balancing)**: 二次管网水力平衡调节
- **换热站自控 (Heat Station Auto-control)**: 换热站自动控制系统

## Requirements

### Requirement 1: 模糊PID控制器配置

**User Story:** AS 热力公司技术人员, I want 配置模糊PID控制器的参数, so that 根据管网特性实现最佳调节效果

#### Acceptance Criteria

1. WHEN 用户打开模糊PID配置面板, THE system SHALL 显示Kp、Ki、Kd三个PID参数的配置输入框
2. WHEN 用户打开模糊PID配置面板, THE system SHALL 显示模糊控制器参数配置项（模糊化因子、规则库、解模糊方法）
3. WHEN 用户保存控制器参数, THE system SHALL 验证参数值的合法性（Kp范围0.1-10，Ki范围0.01-1，Kd范围0.1-5）
4. IF 参数值超出合法范围, THE system SHALL 提示具体错误信息并阻止保存
5. WHEN 参数保存成功, THE system SHALL 将参数下发到控制执行层并返回成功提示

### Requirement 2: 管网参数实时采集

**User Story:** AS 系统, I want 实时采集管网的温度、压力、流量等参数, so that 为模糊PID控制算法提供准确的输入数据

#### Acceptance Criteria

1. WHEN 系统启动实时调节模式, THE system SHALL 通过物联网网关建立与传感器和执行器的通信连接
2. WHILE 实时调节模式运行中, THE system SHALL 每5秒采集一次管网温度、压力、流量参数
3. WHEN 采集到新数据, THE system SHALL 对数据进行有效性校验，过滤异常值
4. IF 数据采集失败, THE system SHALL 使用最近一次有效数据进行填充并在日志中记录
5. WHILE 数据采集中, THE system SHALL 在前端界面实时显示各参数的当前值和变化趋势

### Requirement 3: 模糊PID控制算法执行

**User Story:** AS 系统, I want 根据管网实际参数执行模糊PID控制算法, so that 自动计算最优阀门开度

#### Acceptance Criteria

1. WHEN 管网参数更新, THE system SHALL 在100ms内完成模糊PID控制计算
2. WHEN 控制计算完成, THE system SHALL 输出阀门开度调节量，范围为0-100%
3. WHEN 控制算法检测到参数变化剧烈, THE system SHALL 自动调整模糊规则权重以减少超调
4. IF 控制输出变化超过20%, THE system SHALL 分步执行调节，每次调节不超过20%防止冲击管网
5. WHILE 控制算法运行中, THE system SHALL 记录控制输入、输出、误差值用于后续分析

### Requirement 4: 阀门控制指令下发

**User Story:** AS 系统, I want 将计算得到的阀门开度指令下发到电动执行器, so that 实现对阀门的实际控制

#### Acceptance Criteria

1. WHEN 模糊PID控制计算完成, THE system SHALL 生成控制指令包含目标开度值和执行时间戳
2. WHEN 控制指令生成, THE system SHALL 通过MQTT协议将指令下发到物联网网关
3. IF 物联网网关为Modbus设备, THE system SHALL 将指令转换为Modbus写入命令
4. WHEN 指令下发成功, THE system SHALL 等待执行器响应确认
5. IF 执行器响应超时（超过10秒）, THE system SHALL 重发指令最多3次，若仍失败则告警

### Requirement 5: 前端阀门状态展示

**User Story:** AS 热力公司调度人员, I want 在前端界面查看阀门状态和控制面板, so that 直观监控和操作电动执行器

#### Acceptance Criteria

1. WHEN 用户进入智能调节页面, THE system SHALL 展示所有已配置阀门的列表，每行显示阀门名称、当前位置、目标开度、运行状态
2. WHILE 阀门运行中, THE system SHALL 使用不同颜色标识状态（绿色正常运行、黄色调节中、红色告警）
3. WHEN 用户点击单个阀门, THE system SHALL 打开阀门详情面板，显示实时参数曲线和调节历史
4. WHEN 用户手动调整阀门开度, THE system SHALL 接收输入值并立即下发控制指令

### Requirement 6: 与二网平衡模块集成

**User Story:** AS 系统, I want 与二网平衡模块进行数据交互, so that 接收优化计算结果并执行调节

#### Acceptance Criteria

1. WHEN 二网平衡模块完成优化计算, THE system SHALL 接收包含各阀门目标开度的调节方案
2. WHEN 接收到调节方案, THE system SHALL 解析方案中的阀门ID和目标开度
3. WHEN 解析完成, THE system SHALL 启动模糊PID控制器逐个执行阀门调节
4. WHILE 调节执行中, THE system SHALL 将调节进度和状态实时同步给二网平衡模块
5. IF 调节执行失败, THE system SHALL 将失败原因反馈给二网平衡模块用于重新计算

### Requirement 7: 与换热站自控模块集成

**User Story:** AS 系统, I want 与换热站自控模块进行数据交互, so that 协同完成换热站优化运行

#### Acceptance Criteria

1. WHEN 换热站自控模块请求调节支持, THE system SHALL 接收包含目标参数的调节请求
2. WHEN 接收到调节请求, THE system SHALL 根据请求参数计算对应的阀门开度方案
3. WHEN 方案计算完成, THE system SHALL 返回调节方案和预期效果给换热站自控模块
4. WHILE 协同调节中, THE system SHALL 与换热站自控模块保持数据同步确保协调一致

### Requirement 8: 调节效果评估

**User Story:** AS 热力公司调度人员, I want 查看模糊PID控制的调节效果, so that 评估控制算法的性能

#### Acceptance Criteria

1. WHEN 调节完成后, THE system SHALL 计算并显示响应时间（从指令下达到达到目标开度的时间）
2. WHEN 调节完成后, THE system SHALL 计算并显示超调量（实际开度超过目标值的最大偏差）
3. WHEN 调节完成后, THE system SHALL 计算并显示稳态误差（稳定后与目标开度的偏差百分比）
4. IF 响应时间超过30秒 OR 超调量超过15% OR 稳态误差超过5%, THE system SHALL 生成性能告警
5. WHILE 效果评估展示中, THE system SHALL 提供与传统PID控制的对比图表

### Requirement 9: 设备通信协议支持

**User Story:** AS 系统, I want 支持MQTT和Modbus两种物联网通信协议, so that 与不同类型的电动执行器设备通信

#### Acceptance Criteria

1. WHEN 系统配置设备连接, THE system SHALL 支持通过MQTT协议连接设备
2. WHEN 系统配置设备连接, THE system SHALL 支持通过Modbus RTU协议连接设备
3. WHEN 系统配置设备连接, THE system SHALL 支持通过Modbus TCP协议连接设备
4. IF 设备通信中断, THE system SHALL 自动尝试重连，重连间隔为5秒、10秒、30秒递增
5. WHILE 设备通信中, THE system SHALL 记录通信日志包括发送指令、接收响应、时间戳

### Requirement 10: 控制参数自学习

**User Story:** AS 系统, I want 根据历史调节数据自动优化控制参数, so that 适应管网运行变化

#### Acceptance Criteria

1. WHILE 系统运行中, THE system SHALL 记录每次调节的输入输出数据形成训练数据集
2. WHEN 训练数据积累超过1000条, THE system SHALL 启动参数自学习算法优化Kp、Ki、Kd
3. WHEN 自学习完成, THE system SHALL 比较新参数与当前参数的调节效果
4. IF 新参数效果优于当前参数超过10%, THE system SHALL 自动切换到新参数
5. IF 自学习过程出现异常, THE system SHALL 回退到原有参数并记录错误日志
