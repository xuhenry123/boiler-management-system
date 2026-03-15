# 锅炉集中供热智慧管理系统算法详细设计文档

## 文档信息

| 属性 | 内容 |
|------|------|
| 版本号 | V1.0 |
| 更新日期 | 2026-03-15 |
| 项目名称 | 锅炉集中供热智慧管理系统 |

## 1 算法概述

本系统涉及的核心算法涵盖负荷预测、优化控制、异常检测、水力计算等多个领域。这些算法协同工作，实现供热系统的智能化运行。

| 序号 | 算法名称 | 应用场景 | 输入数据 | 输出结果 |
|------|----------|----------|----------|----------|
| 1 | LSTM神经网络 | 负荷预测、温度预测 | 历史负荷、气象数据、时间特征 | 未来负荷预测值及置信区间 |
| 2 | 遗传算法 | 二网平衡优化 | 管网拓扑、用户室温、阀门状态 | 最优阀门开度组合 |
| 3 | 粒子群优化 | 管网平衡优化 | 管网参数、约束条件 | 全局最优解 |
| 4 | PID控制 | 温度/流量控制 | 设定值、测量值 | 控制输出 |
| 5 | 孤立森林 | 异常检测 | 运行数据 | 异常分数、异常标签 |
| 6 | Newton-Raphson | 水力仿真 | 管网拓扑、边界条件 | 节点压力、管段流量 |
| 7 | 线性规划 | 多能耦合优化 | 能源价格、设备参数 | 最优调度方案 |
| 8 | 指数平滑 | 气象预测 | 历史气象数据 | 未来气象预测 |

## 2 LSTM神经网络算法

### 2.1 算法原理

长短期记忆网络（LSTM）是一种特殊的循环神经网络结构，通过引入门控机制解决了传统RNN在处理长序列数据时的梯度消失和梯度爆炸问题。LSTM网络特别适合处理时间序列预测问题，在供热负荷预测中表现出色。

### 2.2 算法步骤

**步骤一：数据采集与预处理**

数据来源包括历史负荷数据、气象数据（室外温度、湿度、风速）、时间特征数据（小时、星期、是否节假日）。

数据预处理包括：
- 缺失值填补：采用线性插值法填补缺失数据
- 异常值剔除：采用3σ准则识别并处理异常值
- 归一化处理：采用Min-Max归一化，将数据缩放到[0,1]区间

归一化公式：
$$x_{normalized} = \frac{x - x_{min}}{x_{max} - x_{min}}$$

反归一化公式：
$$x_{original} = x_{normalized} \times (x_{max} - x_{min}) + x_{min}$$

**步骤二：特征工程**

将时间序列数据转换为监督学习格式。采用滑动窗口方法，窗口大小为24个时间点（24小时）。

输入特征向量：
$$X = [L_{t-23}, L_{t-22}, ..., L_{t-1}, T_{out}, H, W, D_{hour}, D_{week}, F_{holiday}]$$

其中：
- $L_{t-i}$：历史负荷值
- $T_{out}$：室外温度
- $H$：相对湿度
- $W$：风速
- $D_{hour}$：小时特征
- $D_{week}$：星期特征
- $F_{holiday}$：节假日标志

**步骤三：模型构建**

LSTM网络结构：
- 输入层：26个神经元（24个历史负荷+2个气象特征）
- LSTM隐藏层1：128个神经元
- Dropout层：丢弃率0.2
- LSTM隐藏层2：64个神经元
- Dropout层：丢弃率0.2
- 全连接层：32个神经元
- 输出层：6个神经元（预测未来6小时）

**步骤四：模型训练**

训练参数：
- 优化器：Adam
- 学习率：0.001
- 批次大小：32
- 训练轮数：100
- 损失函数：MSE（均方误差）

损失函数：
$$MSE = \frac{1}{n} \sum_{i=1}^{n}(y_i - \hat{y}_i)^2$$

**步骤五：预测推理**

将当前时刻的数据输入模型，前向传播计算得到预测输出。预测未来6小时的负荷值。

### 2.3 算法公式

**LSTM单元核心公式：**

遗忘门：
$$f_t = \sigma(W_f \cdot [h_{t-1}, x_t] + b_f)$$

输入门：
$$i_t = \sigma(W_i \cdot [h_{t-1}, x_t] + b_i)$$

候选细胞状态：
$$\tilde{C}_t = \tanh(W_C \cdot [h_{t-1}, x_t] + b_C)$$

细胞状态更新：
$$C_t = f_t \cdot C_{t-1} + i_t \cdot \tilde{C}_t$$

输出门：
$$o_t = \sigma(W_o \cdot [h_{t-1}, x_t] + b_o)$$

隐藏状态输出：
$$h_t = o_t \cdot \tanh(C_t)$$

其中：
- $\sigma$：Sigmoid激活函数，$\sigma(x) = \frac{1}{1 + e^{-x}}$
- $\tanh$：双曲正切激活函数
- $W_f, W_i, W_C, W_o$：权重矩阵
- $b_f, b_i, b_C, b_o$：偏置向量

### 2.4 数据字段

| 字段名称 | 数据类型 | 取值范围 | 说明 |
|----------|----------|----------|------|
| historical_load | Float[] | 0-∞ | 历史负荷序列（24个值） |
| outdoor_temp | Float | -30~40 | 室外温度（℃） |
| humidity | Float | 0-100 | 相对湿度（%） |
| wind_speed | Float | 0-30 | 风速（m/s） |
| hour | Integer | 0-23 | 小时 |
| day_of_week | Integer | 0-6 | 星期（0=周一） |
| is_holiday | Boolean | 0-1 | 是否节假日 |
| predicted_load | Float | 0-∞ | 预测负荷（MW） |
| confidence_lower | Float | 0-∞ | 置信区间下限 |
| confidence_upper | Float | 0-∞ | 置信区间上限 |
| accuracy | Float | 0-100 | 预测准确率（%） |

## 3 遗传算法

### 3.1 算法原理

遗传算法（Genetic Algorithm，GA）是一种模拟自然选择和遗传机制的优化算法，通过模拟生物进化过程搜索最优解。在二网平衡优化中，遗传算法用于寻找使各用户室温偏差最小的阀门开度组合。

### 3.2 算法步骤

**步骤一：染色体编码**

采用实数编码方式，将各阀门开度编码为染色体中的一个基因。假设有n个调节阀，则染色体长度为n。

染色体结构：
$$Chromosome = [v_1, v_2, v_3, ..., v_n]$$

其中，$v_i$表示第i个阀门的开度，取值范围[0, 100]。

**步骤二：初始化种群**

随机生成初始种群，种群规模通常设为50-100。

```python
# 初始化种群
population = []
for i in range(population_size):
    individual = [random.uniform(0, 100) for _ in range(num_valves)]
    population.append(individual)
```

**步骤三：适应度计算**

适应度函数定义为室温偏差的倒数，偏差越小适应度越高。

目标函数（总偏差）：
$$J = \sum_{i=1}^{n} |T_i^{actual} - T_i^{target}|$$

适应度函数：
$$fitness = \frac{1}{J + \epsilon}$$

其中，$\epsilon$为很小的正数，防止除零。

约束条件检查：
- 阀门开度约束：$0 \le v_i \le 100$
- 流量平衡约束：$\sum Q_{in} = \sum Q_{out}$
- 压力约束：$P_{min} \le P_i \le P_{max}$

**步骤四：选择操作**

采用轮盘赌选择算法，根据适应度值选择父代个体。

选择概率：
$$P_i = \frac{fitness_i}{\sum_{j=1}^{N} fitness_j}$$

**步骤五：交叉操作**

采用单点交叉，以一定概率（通常0.7-0.9）交换父代个体的部分基因。

```python
# 单点交叉
crossover_point = random.randint(1, chromosome_length - 1)
child1[:crossover_point] = parent1[:crossover_point]
child1[crossover_point:] = parent2[crossover_point:]
child2[:crossover_point] = parent2[:crossover_point]
child2[crossover_point:] = parent1[crossover_point:]
```

**步骤六：变异操作**

以一定概率（通常0.01-0.05）对子代个体的基因进行随机变异。

```python
# 高斯变异
if random.random() < mutation_rate:
    gene += random.gauss(0, mutation_strength)
    gene = max(0, min(100, gene))
```

**步骤七：精英保留**

将父代中适应度最高的个体直接复制到子代，确保最优解不丢失。

**步骤八：终止判断**

满足以下任一条件时终止：
- 达到最大迭代次数（通常100-500）
- 适应度值不再改善（连续20代）
- 目标函数值小于阈值

### 3.3 算法公式

| 参数 | 符号 | 典型值 | 说明 |
|------|------|--------|------|
| 种群规模 | N | 50-100 | 染色体数量 |
| 迭代次数 | T | 100-500 | 最大进化代数 |
| 交叉率 | $P_c$ | 0.7-0.9 | 交叉操作概率 |
| 变异率 | $P_m$ | 0.01-0.05 | 变异操作概率 |
| 精英数量 | E | 2-5 | 保留的最优个体数 |

### 3.4 数据字段

| 字段名称 | 数据类型 | 取值范围 | 说明 |
|----------|----------|----------|------|
| population_size | Integer | 20-100 | 种群规模 |
| max_generations | Integer | 50-500 | 最大迭代次数 |
| crossover_rate | Float | 0.5-1.0 | 交叉概率 |
| mutation_rate | Float | 0.01-0.2 | 变异概率 |
| valve_openings | Float[] | 0-100 | 阀门开度数组 |
| deviation_before | Float | 0-∞ | 优化前偏差（%） |
| deviation_after | Float | 0-∞ | 优化后偏差（%） |
| energy_saving | Float | 0-100 | 节能率（%） |

## 4 粒子群优化算法

### 4.1 算法原理

粒子群优化（Particle Swarm Optimization，PSO）是一种基于群体智能的优化算法，通过模拟鸟群觅食行为搜索最优解。PSO算法参数少、易于实现，广泛应用于函数优化和工程实践。

### 4.2 算法步骤

**步骤一：初始化粒子群**

在解空间中随机初始化粒子群的位置和速度。

粒子i的位置：$X_i = (x_{i1}, x_{i2}, ..., x_{id})$
粒子i的速度：$V_i = (v_{i1}, v_{i2}, ..., v_{id})$

位置范围：$[x_{min}, x_{max}]$
速度范围：$[v_{min}, v_{max}]$

**步骤二：计算适应度**

对每个粒子计算适应度值（目标函数的倒数）。

**步骤三：更新个体最优**

将当前适应度与历史最优适应度比较，若更优则更新。

$$P_{best,i} = \begin{cases} X_i, & f(X_i) < f(P_{best,i}) \\ P_{best,i}, & \text{otherwise} \end{cases}$$

**步骤四：更新全局最优**

找出所有粒子中的最优位置。

$$G_{best} = \arg\min_{i} f(P_{best,i})$$

**步骤五：更新速度和位置**

$$v_{i,j}(t+1) = w \cdot v_{i,j}(t) + c_1 \cdot r_1 \cdot (P_{best,i,j} - x_{i,j}(t)) + c_2 \cdot r_2 \cdot (G_{best,j} - x_{i,j}(t))$$

$$x_{i,j}(t+1) = x_{i,j}(t) + v_{i,j}(t+1)$$

**步骤六：终止判断**

达到最大迭代次数或适应度值满足要求时停止。

### 4.3 算法公式

| 参数 | 符号 | 典型值 | 说明 |
|------|------|--------|------|
| 粒子数量 | M | 20-50 | 粒子总数 |
| 迭代次数 | T | 100-500 | 最大迭代次数 |
| 惯性权重 | w | 0.4-0.9 | 保留上一代速度的比例 |
| 学习因子1 | $c_1$ | 1.5-2.5 | 个体经验影响 |
| 学习因子2 | $c_2$ | 1.5-2.5 | 群体经验影响 |

速度限制：
$$v_j^{min} \le v_{i,j} \le v_j^{max}$$

位置限制：
$$x_j^{min} \le x_{i,j} \le x_j^{max}$$

## 5 PID控制算法

### 5.1 算法原理

PID（比例-积分-微分）控制是过程控制中应用最广泛的控制算法。通过对偏差进行比例、积分、微分运算得到控制输出，实现对被控对象的精确调节。

### 5.2 算法步骤

**步骤一：采样**

以固定采样周期T采集被控变量的当前值（测量值）。

**步骤二：计算偏差**

$$e(k) = SP - PV(k)$$

其中：
- $e(k)$：第k次采样的偏差
- $SP$：设定值（Set Point）
- $PV(k)$：第k次采样的测量值（Process Value）

**步骤三：比例运算**

$$P_{out} = K_p \cdot e(k)$$

比例环节根据偏差大小进行调节，偏差越大，调节作用越强。

**步骤四：积分运算**

$$I_{out} = K_i \cdot T \cdot \sum_{j=0}^{k} e(j)$$

积分环节消除静差，提高控制精度。

**步骤五：微分运算**

$$D_{out} = K_d \cdot \frac{e(k) - e(k-1)}{T}$$

微分环节预测偏差变化趋势，提前调节，减少超调。

**步骤六：控制输出**

$$u(k) = P_{out} + I_{out} + D_{out}$$

**步骤七：输出限幅**

$$u_{final} = \begin{cases} u_{min}, & u(k) < u_{min} \\ u(k), & u_{min} \le u(k) \le u_{max} \\ u_{max}, & u(k) > u_{max} \end{cases}$$

### 5.3 算法公式

| 参数 | 符号 | 典型值 | 作用 |
|------|------|--------|------|
| 比例系数 | $K_p$ | 0.1-10 | 快速响应偏差 |
| 积分系数 | $K_i$ | 0.01-1 | 消除静差 |
| 微分系数 | $K_d$ | 0.01-1 | 预测提前调节 |
| 采样周期 | T | 0.1-10s | 采样间隔 |

PID离散形式：
$$u(k) = K_p \cdot e(k) + K_i \cdot T \cdot \sum_{j=0}^{k} e(j) + K_d \cdot \frac{e(k) - e(k-1)}{T}$$

### 5.4 数据字段

| 字段名称 | 数据类型 | 取值范围 | 说明 |
|----------|----------|----------|------|
| setpoint | Float | 30-80 | 设定温度（℃） |
| measured_value | Float | 0-100 | 测量温度（℃） |
| deviation | Float | -∞~+∞ | 偏差（℃） |
| kp | Float | 0.1-10 | 比例系数 |
| ki | Float | 0.01-1 | 积分系数 |
| kd | Float | 0.01-1 | 微分系数 |
| control_output | Float | 0-100 | 控制输出（%） |
| output_min | Float | 0 | 输出下限 |
| output_max | Float | 100 | 输出上限 |

## 6 孤立森林算法

### 6.1 算法原理

孤立森林（Isolation Forest）是一种基于决策树的异常检测算法。其核心思想是：异常点在特征空间中更容易被"孤立"，即通过随机划分，异常点通常只需要更少的划分次数就能被分离出来。

### 6.2 算法步骤

**步骤一：构建孤立树**

从样本数据中随机选择一个特征和该特征的随机切分值，将样本空间递归划分为两个子空间。

递归终止条件：
- 树深度达到限制$h_{max}$
- 子空间样本数$\le 1$

```python
def build_tree(X, depth, max_depth):
    if depth >= max_depth or len(X) <= 1:
        return Node(is_leaf=True)
    
    # 随机选择特征和切分点
    feature = random.randint(0, n_features - 1)
    split_value = random.uniform(X[:, feature].min(), X[:, feature].max())
    
    # 划分数据
    left_idx = X[:, feature] < split_value
    right_idx = X[:, feature] >= split_value
    
    return Node(
        feature=feature,
        split_value=split_value,
        left=build_tree(X[left_idx], depth+1, max_depth),
        right=build_tree(X[right_idx], depth+1, max_depth)
    )
```

**步骤二：构建孤立森林**

独立构建T棵孤立树，形成一片森林。树的数量通常设为100-200。

**步骤三：计算异常分数**

对于新样本x，计算其在各棵树中的平均路径长度：

$$E(h(x)) = \frac{1}{T} \sum_{i=1}^{T} h_i(x)$$

路径长度归一化：
$$c(n) = \begin{cases} 2H(n-1) - \frac{2(n-1)}{n}, & n > 2 \\ 1, & n = 2 \\ 0, & n \le 1 \end{cases}$$

异常分数：
$$s(x, n) = 2^{-\frac{E(h(x))}{c(n)}}$$

**步骤四：判定异常**

设定阈值$\theta$（通常0.6-0.8），当$s(x, n) > \theta$时判定为异常。

### 6.3 算法公式

| 参数 | 符号 | 典型值 | 说明 |
|------|------|--------|------|
| 树的数量 | T | 100-200 | 孤立树棵数 |
| 子采样数 | $\psi$ | 256 | 每棵树使用的样本数 |
| 最大深度 | $h_{max}$ | $\lceil \log_2 \psi \rceil$ | 树的最大深度 |
| 异常阈值 | $\theta$ | 0.6-0.8 | 异常判定阈值 |

调和数近似：
$$H(k) = \ln(k) + 0.5772156649$$

### 6.4 数据字段

| 字段名称 | 数据类型 | 取值范围 | 说明 |
|----------|----------|----------|------|
| sample_count | Integer | 256 | 子采样数量 |
| tree_count | Integer | 100-200 | 树的数量 |
| anomaly_score | Float | 0-1 | 异常分数 |
| is_anomaly | Boolean | 0-1 | 是否异常 |
| threshold | Float | 0.6-0.8 | 异常阈值 |
| anomaly_type | String | temp/pressure/flow | 异常类型 |

## 7 Newton-Raphson算法

### 7.1 算法原理

Newton-Raphson法是求解非线性方程组最常用的迭代方法，具有二阶收敛速度。在管网水力计算中，用于求解节点压力方程组。

### 7.2 算法步骤

**步骤一：建立方程组**

根据质量守恒定律，节点i的流量平衡方程：
$$f_i(P) = \sum_{j \in S_i} Q_{ij} = 0$$

其中，$S_i$为与节点i相连的管段集合，$Q_{ij}$为从节点i流向节点j的流量。

**步骤二：线性化**

管段流量与压差的关系（平方阻力定律）：
$$Q_{ij} = K_{ij} \cdot \text{sgn}(P_i - P_j) \cdot \sqrt{|P_i - P_j|}$$

**步骤三：构建雅可比矩阵**

$$J_{ij} = \frac{\partial f_i}{\partial P_j}$$

对角元素：
$$J_{ii} = \sum_{j \in S_i} \frac{K_{ij}}{2\sqrt{|P_i - P_j|}}$$

非对角元素：
$$J_{ij} = -\frac{K_{ij}}{2\sqrt{|P_i - P_j|}}, \quad j \in S_i$$

**步骤四：迭代求解**

$$P^{k+1} = P^k - J^{-1}(P^k) \cdot F(P^k)$$

**步骤五：收敛判断**

$$||F(P^k)||_2 < \varepsilon$$

或

$$||P^{k+1} - P^k||_2 < \varepsilon$$

其中，$\varepsilon$为收敛精度，通常取$10^{-4}$~$10^{-6}$。

### 7.3 算法公式

| 参数 | 符号 | 典型值 | 说明 |
|------|------|--------|------|
| 收敛精度 | $\varepsilon$ | $10^{-4}$~$10^{-6}$ | 迭代终止条件 |
| 最大迭代 | $N_{max}$ | 50-100 | 最大迭代次数 |
| 压力初值 | $P^0$ | 设计压力 | 迭代初始值 |

### 7.4 数据字段

| 字段名称 | 数据类型 | 取值范围 | 说明 |
|----------|----------|----------|------|
| node_count | Integer | - | 节点数量 |
| node_pressure | Float[] | 0-1.0 | 节点压力（MPa） |
| segment_flow | Float[] | 0-1000 | 管段流量（t/h） |
| tolerance | Float | $10^{-4}$~$10^{-6}$ | 收敛精度 |
| iterations | Integer | - | 实际迭代次数 |

## 8 线性规划算法

### 8.1 算法原理

线性规划（Linear Programming，LP）是数学规划中发展最成熟、应用最广泛的分支。在多能耦合优化中，用于求解在给定约束条件下的最优调度方案。

### 8.2 算法步骤

**步骤一：建立优化模型**

决策变量：
- $x_i$：设备i的出力（或启停状态）
- $i = 1, 2, ..., n$（设备数量）

目标函数（最小化总成本）：
$$\min Z = \sum_{i=1}^{n} (c_i^{fuel} \cdot x_i + c_i^{start} \cdot y_i + c_i^{om})$$

其中：
- $c_i^{fuel}$：设备i的燃料成本系数
- $c_i^{start}$：设备i的启停成本系数
- $c_i^{om}$：设备i的运维成本

**步骤二：建立约束条件**

热平衡约束：
$$\sum_{i=1}^{n} Q_i = Q^{demand} + Q^{loss}$$

设备出力约束：
$$Q_i^{min} \cdot y_i \le x_i \le Q_i^{max} \cdot y_i$$

爬坡速率约束：
$$|x_i(t) - x_i(t-1)| \le R_i^{ramp}$$

最小运行/停机时间约束：
$$y_i(t) - y_i(t-1) \le \sum_{k=1}^{T^{on}} y_i(t+k)$$

**步骤三：求解**

采用单纯形法或内点法求解线性规划问题。

### 8.3 算法公式

标准形式：
$$\min c^T x$$
$$s.t. \ Ax \ge b$$
$$x \ge 0$$

### 8.4 数据字段

| 字段名称 | 数据类型 | 取值范围 | 说明 |
|----------|----------|----------|------|
| device_count | Integer | - | 设备数量 |
| device_power | Float[] | 0-∞ | 设备出力（MW） |
| device_cost | Float[] | 0-∞ | 设备成本系数 |
| heat_demand | Float | 0-∞ | 热负荷需求（MW） |
| optimization_result | Object | - | 优化结果 |
| total_cost | Float | 0-∞ | 总成本（元） |
| total_emissions | Float | 0-∞ | 碳排放（t） |
| reliability | Float | 0-100 | 可靠性（%） |

## 9 算法性能指标

### 9.1 性能对比

| 算法 | 预测精度/优化效果 | 计算耗时 | 适用场景 |
|------|------------------|----------|----------|
| LSTM负荷预测 | MAPE≤5% | <1s | 短期负荷预测 |
| 遗传算法 | 偏差降低80% | 100-500ms | 阀门开度优化 |
| 粒子群优化 | 偏差降低75% | 50-200ms | 管网平衡优化 |
| PID控制 | 偏差≤±1℃ | 实时 | 温度闭环控制 |
| 孤立森林 | 准确率≥90% | <100ms | 实时异常检测 |
| Newton-Raphson | 精度10⁻⁴ | 10-50ms | 水力仿真计算 |
| 线性规划 | 最优解 | 1-10s | 多能耦合优化 |

### 9.2 评估指标

| 指标名称 | 计算公式 | 达标要求 |
|----------|-----------|----------|
| 负荷预测准确率 | $1 - MAPE$ | ≥95% |
| 温度控制偏差 | $|T_{actual} - T_{setpoint}|$ | ≤±1℃ |
| 管网平衡度 | $1 - \frac{\max(Q) - \min(Q)}{Q_{design}}$ | ≥90% |
| 异常检测准确率 | $\frac{TP + TN}{TP + TN + FP + FN}$ | ≥90% |
| 优化计算时间 | - | ≤1s |

## 10 算法实现示例

### 10.1 Python实现：LSTM负荷预测

```python
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout

class LoadPredictor:
    def __init__(self, input_steps=24, output_steps=6):
        self.input_steps = input_steps
        self.output_steps = output_steps
        self.model = self._build_model()
        
    def _build_model(self):
        model = Sequential([
            LSTM(128, return_sequences=True, input_shape=(self.input_steps, 26)),
            Dropout(0.2),
            LSTM(64),
            Dropout(0.2),
            Dense(32, activation='relu'),
            Dense(self.output_steps)
        ])
        model.compile(optimizer='adam', loss='mse')
        return model
    
    def train(self, X_train, y_train, epochs=100, batch_size=32):
        self.model.fit(X_train, y_train, epochs=epochs, batch_size=batch_size, validation_split=0.2)
    
    def predict(self, X):
        return self.model.predict(X)
```

### 10.2 Python实现：遗传算法

```python
import numpy as np
import random

class GeneticOptimizer:
    def __init__(self, num_valves, pop_size=50, max_gen=100):
        self.num_valves = num_valves
        self.pop_size = pop_size
        self.max_gen = max_gen
        self.crossover_rate = 0.8
        self.mutation_rate = 0.05
        
    def optimize(self, fitness_func):
        population = self._init_population()
        
        for gen in range(self.max_gen):
            # 计算适应度
            fitness = [fitness_func(ind) for ind in population]
            
            # 选择
            parents = self._selection(population, fitness)
            
            # 交叉
            children = self._crossover(parents)
            
            # 变异
            population = self._mutation(children)
            
            # 精英保留
            best_idx = np.argmax(fitness)
            population[0] = self.best_individual
            
        return self.best_individual
    
    def _init_population(self):
        return [[random.uniform(0, 100) for _ in range(self.num_valves)] 
                   for _ in range(self.pop_size)]
```

### 10.3 Python实现：PID控制器

```python
class PIDController:
    def __init__(self, kp=1.0, ki=0.1, kd=0.01, setpoint=50):
        self.kp = kp
        self.ki = ki
        self.kd = kd
        self.setpoint = setpoint
        self.prev_error = 0
        self.integral = 0
        self.output_min = 0
        self.output_max = 100
        
    def compute(self, measured_value, dt=1):
        error = self.setpoint - measured_value
        
        # 比例项
        P = self.kp * error
        
        # 积分项
        self.integral += error * dt
        I = self.ki * self.integral
        
        # 微分项
        derivative = (error - self.prev_error) / dt
        D = self.kd * derivative
        
        # 总输出
        output = P + I + D
        
        # 限幅
        output = max(self.output_min, min(self.output_max, output))
        
        self.prev_error = error
        return output
```

## 11 算法部署说明

### 11.1 模型训练周期

| 算法 | 训练周期 | 更新方式 |
|------|----------|----------|
| LSTM负荷预测 | 每周 |增量训练 |
| 遗传算法 | 每次优化 | 即时计算 |
| PID控制 | 首次配置 | 自整定更新 |
| 孤立森林 | 每月 | 增量训练 |
| 水力仿真 | 每次计算 | 即时计算 |
| 线性规划 | 每次优化 | 即时计算 |

### 11.2 硬件要求

| 算法 | CPU | 内存 | GPU |
|------|-----|------|-----|
| LSTM | 4核 | 8GB | 可选GTX1060+ |
| 遗传算法 | 2核 | 4GB | - |
| PID控制 | 1核 | 1GB | - |
| 孤立森林 | 2核 | 4GB | - |
| Newton-Raphson | 4核 | 8GB | - |
| 线性规划 | 2核 | 4GB | - |

### 11.3 实时性要求

| 算法 | 响应时间 | 采样周期 |
|------|----------|----------|
| 负荷预测 | <1s | 5分钟 |
| 平衡优化 | <500ms | 10分钟 |
| PID控制 | <10ms | 1秒 |
| 异常检测 | <100ms | 1分钟 |
| 水力仿真 | <1s | 按需 |
| 多能优化 | <10s | 1小时 |
