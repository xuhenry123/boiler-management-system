# Requirements Document

## Introduction

本文档描述水力仿真功能模块的需求规格。该模块基于图论算法和Newton-Raphson迭代进行管网水力计算，模拟管网运行状态，预测水力工况，为二网平衡和阀门调节提供数据支撑。

## Glossary

- **管网拓扑 (Network Topology)**: 供热管网中节点和管段的连接关系结构
- **节点 (Node)**: 管网中的关键点，包括热源、换热站、阀门、热用户等
- **管段 (Edge)**: 连接两个节点之间的管道
- **阻力系数 (Resistance Coefficient)**: 管道对流体流动阻碍程度的量化指标
- **Newton-Raphson迭代**: 求解非线性方程组的数值迭代方法
- **水力工况 (Hydraulic Condition)**: 管网中各点的压力、流量、流速等水力参数
- **静态仿真 (Static Simulation)**: 基于固定边界条件的水力计算
- **动态仿真 (Dynamic Simulation)**: 考虑时间变化因素的水力仿真
- **Jacobian矩阵**: Newton-Raphson算法中用于求解方程组的偏导数矩阵
- **收敛判断 (Convergence Check)**: 迭代计算终止条件的判定
- **二网平衡 (Secondary Network Balancing)**: 二次管网的水力平衡调节

## Requirements

### Requirement 1: 管网拓扑数据管理

**User Story:** AS 热力公司技术人员, I want 管理管网拓扑数据, so that 为水力仿真提供准确的管网结构输入

#### Acceptance Criteria

1. WHEN 用户进入管网拓扑管理页面, THE system SHALL 显示管网节点列表，包括节点ID、名称、类型、位置坐标
2. WHEN 用户进入管网拓扑管理页面, THE system SHALL 显示管网边（管段）列表，包括管段ID、起点、终点、长度、口径、粗糙度
3. WHEN 用户新增节点, THE system SHALL 验证节点类型（热源/换热站/阀门/用户）和位置坐标的合法性
4. WHEN 用户新增管段, THE system SHALL 验证管段连接的两个节点存在且不形成孤岛
5. IF 节点或管段数据不完整, THE system SHALL 提示具体缺失字段并阻止保存

### Requirement 2: 静态水力仿真计算

**User Story:** AS 热力公司调度人员, I want 进行静态水力仿真计算, so that 获取管网在固定工况下的压力分布和流量分配

#### Acceptance Criteria

1. WHEN 用户选择静态仿真模式并设置边界条件, THE system SHALL 建立管网水力方程组
2. WHEN 方程组建立完成, THE system SHALL 执行Newton-Raphson迭代求解
3. WHILE 迭代计算进行中, THE system SHALL 显示当前迭代次数和残差收敛曲线
4. WHEN 迭代收敛（残差小于1e-6）, THE system SHALL 返回各节点压力和各管段流量
5. WHEN 迭代收敛完成, THE system SHALL 计算并显示各管段流速和单位阻力损失
6. IF 迭代次数超过最大限制（100次）仍未收敛, THE system SHALL 提示用户检查管网参数并提供诊断信息
7. IF 迭代过程中出现负压, THE system SHALL 警告用户并标注异常节点位置

### Requirement 3: 动态水力仿真

**User Story:** AS 热力公司调度人员, I want 进行动态水力仿真, so that 模拟管网在不同时间段内的水力工况变化

#### Acceptance Criteria

1. WHEN 用户选择动态仿真模式并配置时间参数, THE system SHALL 设定仿真时间范围和时间步长
2. WHEN 动态仿真启动, THE system SHALL 按时间步长逐步执行静态仿真计算
3. WHILE 动态仿真进行中, THE system SHALL 每完成一个时间步更新一次仿真进度
4. WHEN 动态仿真完成, THE system SHALL 生成时间序列仿真结果，包含各节点压力变化曲线和各管段流量变化曲线
5. IF 动态仿真过程中某时间步收敛失败, THE system SHALL 记录失败时间点并跳过该时间步继续计算
6. IF 用户中断动态仿真, THE system SHALL 保存已完成的仿真结果供用户查看

### Requirement 4: 仿真结果可视化展示

**User Story:** AS 热力公司调度人员, I want 查看仿真结果的可视化展示, so that 直观理解管网水力工况分布

#### Acceptance Criteria

1. WHEN 仿真计算完成, THE system SHALL 在拓扑图上使用颜色热力图展示节点压力分布
2. WHEN 仿真计算完成, THE system SHALL 使用颜色标识管段流量状态（正常/过载/欠载）
3. WHEN 用户点击拓扑图上的节点, THE system SHALL 显示该节点的详细水力参数（压力、流量需求）
4. WHEN 用户点击拓扑图上的管段, THE system SHALL 显示该管段的详细参数（流量、流速、阻力损失）
5. WHEN 动态仿真完成, THE system SHALL 提供时间轴播放控件，支持回放仿真过程

### Requirement 5: 与二网平衡模块集成

**User Story:** AS 系统开发人员, I want 水力仿真模块为二网平衡提供管网参数, so that 支撑优化算法的准确计算

#### Acceptance Criteria

1. WHEN 二网平衡模块请求管网拓扑, THE system SHALL 通过内部接口返回完整的拓扑结构数据
2. WHEN 二网平衡模块请求管段阻力系数, THE system SHALL 返回各管段的计算阻力系数
3. WHEN 二网平衡模块请求节点设计流量, THE system SHALL 返回各热用户的设计流量需求
4. WHEN 二网平衡模块请求水力计算, THE system SHALL 执行仿真并返回各节点压力和各管段流量
5. IF 二网平衡模块传入阀门开度变化, THE system SHALL 在仿真中应用该变化并计算新的水力工况
6. IF 仿真计算失败, THE system SHALL 返回错误码和错误信息供二网平衡模块处理

### Requirement 6: 仿真参数配置

**User Story:** AS 热力公司技术人员, I want 配置水力仿真的计算参数, so that 根据管网特点调整仿真精度和收敛速度

#### Acceptance Criteria

1. WHEN 用户打开仿真参数配置面板, THE system SHALL 显示Newton-Raphson算法参数（最大迭代次数、收敛精度、残差类型）
2. WHEN 用户打开仿真参数配置面板, THE system SHALL 显示物理参数（流体密度、动力粘度、重力加速度）
3. WHEN 用户保存参数配置, THE system SHALL 验证参数值的合法性（迭代次数1-500，精度1e-10到1e-3）
4. IF 参数值超出合法范围, THE system SHALL 提示具体错误信息并阻止保存
5. WHEN 用户重置参数, THE system SHALL 恢复系统默认参数值

### Requirement 7: 仿真结果导出

**User Story:** AS 热力公司管理人员, I want 导出仿真结果数据, so that 进行离线分析和报告编制

#### Acceptance Criteria

1. WHEN 用户点击导出按钮, THE system SHALL 支持导出格式选择（Excel、CSV、JSON）
2. WHEN 用户选择导出范围, THE system SHALL 支持按节点/管段/时间范围筛选导出数据
3. WHEN 导出操作执行, THE system SHALL 生成包含节点压力、流量、流速等完整数据的文件
4. IF 导出数据量较大, THE system SHALL 显示导出进度条
5. IF 导出过程中出现IO错误, THE system SHALL 提示用户重试并记录错误日志

### Requirement 8: 仿真任务管理

**User Story:** AS 热力公司调度人员, I want 管理水力仿真任务, so that 查看历史仿真记录并进行对比分析

#### Acceptance Criteria

1. WHEN 用户进入仿真任务管理页面, THE system SHALL 显示仿真任务列表，包含任务ID、管网名称、仿真类型、创建时间、状态
2. WHEN 用户点击查看历史任务详情, THE system SHALL 展示该次仿真的完整输入参数和计算结果
3. WHEN 用户对比两个历史仿真结果, THE system SHALL 在同一图表中展示两个结果的压力分布和流量分配对比
4. IF 历史任务数据超过90天, THE system SHALL 提示用户可选择归档或清理

### Requirement 9: 边界条件设置

**User Story:** AS 热力公司调度人员, I want 设置仿真边界条件, so that 定义管网的水力输入参数

#### Acceptance Criteria

1. WHEN 用户设置热源边界条件, THE system SHALL 支持设置热源出口压力或流量
2. WHEN 用户设置用户边界条件, THE system SHALL 支持按设计流量或按建筑面积设定用户需求
3. WHEN 用户设置阀门边界条件, THE system SHALL 支持设定阀门开度百分比
4. IF 边界条件存在冲突（导致不可行解）, THE system SHALL 在仿真前提示用户并建议调整方案

### Requirement 10: 仿真诊断与报告

**User Story:** AS 热力公司技术人员, I want 获取仿真诊断信息和报告, so that 识别管网存在的问题和优化空间

#### Acceptance Criteria

1. WHEN 仿真计算完成, THE system SHALL 自动生成诊断报告，包含各节点压力是否在合理范围内
2. WHEN 诊断发现压力异常节点, THE system SHALL 高亮显示异常节点并提供可能原因分析
3. WHEN 诊断发现管段流量异常, THE system SHALL 标注过载或欠载管段并建议调节方案
4. IF 用户请求生成完整报告, THE system SHALL 生成包含输入参数、计算结果、诊断建议的PDF或HTML报告
