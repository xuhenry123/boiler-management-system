# Requirements Document

## Introduction

本文档描述二网平衡功能模块的需求规格。该模块通过智能优化算法实现二次管网水力平衡，自动计算最优阀门开度，解决供热管网水力失调问题，提高供热质量。

## Glossary

- **二次管网 (Secondary Network)**: 供热系统中从换热站到热用户之间的管网
- **水力失调 (Hydraulic Imbalance)**: 管网中各用户回路流量与设计流量不一致的现象
- **遗传算法 (Genetic Algorithm)**: 模拟自然选择和遗传机制的全局优化算法
- **粒子群优化 (Particle Swarm Optimization)**: 模拟鸟群觅食行为的群体智能优化算法
- **阀门开度 (Valve Opening)**: 阀门开启程度的百分比表示
- **管网拓扑 (Network Topology)**: 管网中节点和管段的连接关系
- **实时调节 (Real-time Control)**: 根据实时数据动态调整阀门开度
- **离线计算 (Offline Calculation)**: 基于历史数据和仿真模型进行预先计算

## Requirements

### Requirement 1: 管网拓扑可视化展示

**User Story:** AS 热力公司调度人员, I want 查看二次管网的拓扑结构和阀门状态, so that 直观了解管网运行状况

#### Acceptance Criteria

1. WHEN 用户进入二网平衡页面, THE system SHALL 展示二次管网的拓扑图形，包括换热站、管道和阀门节点
2. WHILE 拓扑图展示中, THE system SHALL 在每个阀门节点显示当前开度百分比和阀门状态
3. WHILE 拓扑图展示中, THE system SHALL 使用不同颜色标识管段的流量状态（正常/过高/过低）
4. IF 管网数据加载成功, THE system SHALL 显示完整拓扑图
5. IF 管网数据加载失败, THE system SHALL 显示错误提示信息并提供重试按钮

### Requirement 2: 离线优化计算

**User Story:** AS 热力公司调度人员, I want 基于历史数据和仿真模型进行水力平衡优化计算, so that 获取最优阀门开度方案

#### Acceptance Criteria

1. WHEN 用户选择离线计算模式并点击开始优化, THE system SHALL 调用水力仿真模块获取当前管网参数
2. WHILE 优化计算进行中, THE system SHALL 显示计算进度百分比和当前迭代次数
3. WHEN 遗传算法优化完成, THE system SHALL 返回最优阀门开度方案和各支路流量预测值
4. WHEN 粒子群优化完成, THE system SHALL 返回最优阀门开度方案和收敛曲线数据
5. IF 优化计算超时（超过300秒）, THE system SHALL 中断计算并提示用户调整参数后重试
6. IF 优化计算无法收敛, THE system SHALL 提示用户检查管网参数并提供建议

### Requirement 3: 实时调节控制

**User Story:** AS 热力公司调度人员, I want 根据实时采集的管网数据动态调整阀门开度, so that 快速响应管网运行变化

#### Acceptance Criteria

1. WHEN 用户选择实时调节模式并配置控制参数, THE system SHALL 建立与物联网网关的数据通道
2. WHILE 实时调节模式运行中, THE system SHALL 每60秒接收一次管网实时数据并更新显示
3. WHEN 实时数据表明水力失调超过阈值, THE system SHALL 自动计算并下发调节指令到对应阀门
4. WHILE 调节指令执行中, THE system SHALL 显示阀门开度变化趋势和调节效果评估
5. IF 实时数据接收中断, THE system SHALL 显示连接断开告警并尝试自动重连
6. IF 阀门执行器响应超时（超过30秒）, THE system SHALL 记录失败日志并告警提示人工干预

### Requirement 4: 优化算法参数配置

**User Story:** AS 热力公司技术人员, I want 配置遗传算法和粒子群优化算法的参数, so that 根据管网特点调整优化策略

#### Acceptance Criteria

1. WHEN 用户打开参数配置面板, THE system SHALL 显示遗传算法参数配置项（种群大小、迭代次数、交叉概率、变异概率）
2. WHEN 用户打开参数配置面板, THE system SHALL 显示粒子群优化参数配置项（粒子数量、迭代次数、学习因子、惯性权重）
3. WHEN 用户保存参数配置, THE system SHALL 验证参数值的合法性（种群大小10-200，迭代次数50-1000，概率0-1）
4. IF 参数值超出合法范围, THE system SHALL 提示具体错误信息并阻止保存

### Requirement 5: 调节历史记录查询

**User Story:** AS 热力公司管理人员, I want 查询历史调节记录和效果评估, so that 分析二网平衡优化效果

#### Acceptance Criteria

1. WHEN 用户进入历史记录页面, THE system SHALL 默认展示最近30天的调节记录列表
2. WHEN 用户设置查询条件并点击搜索, THE system SHALL 根据管网ID、时间范围、调节模式过滤记录
3. WHEN 记录列表展示, THE system SHALL 每条记录显示调节时间、涉及阀门数量、调节前后偏差改善率
4. WHEN 用户点击单条记录查看详情, THE system SHALL 展示该次调节的完整参数、调节前后对比、效果评估曲线

### Requirement 6: 调节效果评估

**User Story:** AS 热力公司调度人员, I want 查看每次调节后的水力平衡改善效果, so that 评估优化算法的实际效果

#### Acceptance Criteria

1. WHEN 调节指令执行完成, THE system SHALL 自动计算并显示各支路流量偏差改善率
2. WHEN 改善率计算完成, THE system SHALL 生成效果评估报告，包含平均偏差降低百分比、达标支路数量、总调节时间
3. IF 调节后仍有支路流量偏差超过15%, THE system SHALL 高亮显示超标支路并建议二次调节
4. WHILE 效果评估展示中, THE system SHALL 提供与历史最佳方案的对比图表

### Requirement 7: 与水力仿真模块集成

**User Story:** AS 系统开发人员, I want 二网平衡模块能够调用水力仿真模块获取管网参数, so that 为优化算法提供准确的输入数据

#### Acceptance Criteria

1. WHEN 优化计算启动, THE system SHALL 通过内部接口调用水力仿真模块获取管网拓扑结构
2. WHEN 优化计算启动, THE system SHALL 通过内部接口调用水力仿真模块获取各管段阻力系数
3. WHEN 优化计算启动, THE system SHALL 通过内部接口调用水力仿真模块获取各节点设计流量
4. IF 水力仿真模块返回数据不完整, THE system SHALL 使用默认值填充缺失字段并在日志中记录警告

### Requirement 8: 算法选择与切换

**User Story:** AS 热力公司技术人员, I want 在遗传算法和粒子群优化算法之间切换选择, so that 根据实际效果选择最优算法

#### Acceptance Criteria

1. WHEN 用户进入优化配置页面, THE system SHALL 显示算法选择下拉框，包含遗传算法和粒子群优化两个选项
2. WHEN 用户切换算法, THE system SHALL 自动加载对应算法的默认参数配置
3. WHILE 算法运行中, THE system SHALL 不允许切换算法，需等待当前计算完成

### Requirement 9: 告警与通知

**User Story:** AS 热力公司调度人员, I want 在管网运行异常或调节失败时收到告警通知, so that 及时处理问题

#### Acceptance Criteria

1. WHEN 管网任意支路流量偏差超过20%, THE system SHALL 生成告警事件并通知相关人员
2. WHEN 阀门执行器连续3次调节失败, THE system SHALL 生成严重告警并标记需要人工干预
3. WHILE 实时调节运行中, THE system SHALL 在界面顶部显示当前告警数量和最新告警摘要
4. WHEN 用户点击告警摘要, THE system SHALL 打开告警详情面板展示完整告警列表
