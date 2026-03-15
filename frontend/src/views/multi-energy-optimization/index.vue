<template>
  <div class="multi-energy-optimization">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><Coin /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ energyStats.todayCost }}元</div>
              <div class="stat-label">今日运行成本</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ energyStats.savingsRate }}%</div>
              <div class="stat-label">节能率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><Leaf /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ energyStats.emissions }}t</div>
              <div class="stat-label">今日碳排放</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="32"><Cpu /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ energyStats.reliability }}%</div>
              <div class="stat-label">供能可靠性</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>多能耦合系统架构</span>
            </div>
          </template>
          <div ref="energyArchChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>优化目标配置</span>
            </div>
          </template>
          <el-form label-width="100px">
            <el-form-item label="优化目标">
              <el-radio-group v-model="optimizeConfig.target">
                <el-radio label="economy">经济优先</el-radio>
                <el-radio label="environment">环保优先</el-radio>
                <el-radio label="balance">综合平衡</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="经济权重">
              <el-slider v-model="optimizeConfig.economyWeight" :min="0" :max="100" show-input />
            </el-form-item>
            <el-form-item label="环保权重">
              <el-slider v-model="optimizeConfig.environmentWeight" :min="0" :max="100" show-input />
            </el-form-item>
            <el-form-item label="可靠性权重">
              <el-slider v-model="optimizeConfig.reliabilityWeight" :min="0" :max="100" show-input />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="runOptimization">执行优化</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>能源结构分布</span>
            </div>
          </template>
          <div ref="energyDistChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>优化方案推荐</span>
            </div>
          </template>
          <el-table :data="optimizePlans" style="width: 100%">
            <el-table-column prop="name" label="方案名称" />
            <el-table-column prop="cost" label="成本(元)" width="100" />
            <el-table-column prop="emissions" label="碳排放(t)" width="100" />
            <el-table-column prop="reliability" label="可靠性(%)" width="100" />
            <el-table-column prop="score" label="综合评分" width="80">
              <template #default="{ row }">
                <el-tag>{{ row.score }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default>
                <el-button type="primary" size="small">采用</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>优化调度计划</span>
              <el-button type="primary" size="small">生成计划</el-button>
            </div>
          </template>
          <el-table :data="schedulePlan" style="width: 100%">
            <el-table-column prop="time" label="时段" width="120" />
            <el-table-column prop="heatLoad" label="热负荷(MW)" width="120" />
            <el-table-column prop="boiler" label="锅炉方案" width="150" />
            <el-table-column prop="heatPump" label="热泵方案" width="150" />
            <el-table-column prop="cost" label="成本(元)" width="100" />
            <el-table-column prop="emissions" label="碳排放(kg)" width="100" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === '已执行' ? 'success' : row.status === '执行中' ? 'warning' : 'info'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Coin, TrendCharts, Leaf, Cpu } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const energyArchChartRef = ref(null)
const energyDistChartRef = ref(null)

const energyStats = ref({
  todayCost: 28500,
  savingsRate: 15.8,
  emissions: 42.5,
  reliability: 99.2
})

const optimizeConfig = ref({
  target: 'balance',
  economyWeight: 40,
  environmentWeight: 30,
  reliabilityWeight: 30
})

const optimizePlans = ref([
  { name: '经济最优方案', cost: 26500, emissions: 48.2, reliability: 98.5, score: 92 },
  { name: '环保最优方案', cost: 29800, emissions: 35.6, reliability: 99.0, score: 88 },
  { name: '综合平衡方案', cost: 27500, emissions: 42.5, reliability: 99.2, score: 95 }
])

const schedulePlan = ref([
  { time: '00:00-04:00', heatLoad: 35, boiler: '1号锅炉50%', heatPump: '关闭', cost: 3500, emissions: 5200, status: '已执行' },
  { time: '04:00-08:00', heatLoad: 45, boiler: '1号锅炉70%', heatPump: '开启1台', cost: 4200, emissions: 6100, status: '执行中' },
  { time: '08:00-12:00', heatLoad: 55, boiler: '1+2号锅炉', heatPump: '开启2台', cost: 5800, emissions: 8200, status: '待执行' },
  { time: '12:00-17:00', heatLoad: 50, boiler: '1号锅炉80%', heatPump: '开启2台', cost: 5100, emissions: 7400, status: '待执行' }
])

const runOptimization = () => {
  console.log('执行优化')
}

const initEnergyArchChart = () => {
  const chart = echarts.init(energyArchChartRef.value)
  const option = {
    tooltip: {},
    series: [{
      type: 'graph',
      layout: 'force',
      nodes: [
        { name: '燃气锅炉', x: 100, y: 100, symbolSize: 60, itemStyle: { color: '#409eff' } },
        { name: '热泵', x: 200, y: 50, symbolSize: 50, itemStyle: { color: '#67c23a' } },
        { name: '光伏', x: 250, y: 100, symbolSize: 40, itemStyle: { color: '#e6a23c' } },
        { name: '蓄热水箱', x: 150, y: 200, symbolSize: 50, itemStyle: { color: '#909399' } },
        { name: '换热站', x: 100, y: 300, symbolSize: 60, itemStyle: { color: '#f56c6c' } }
      ],
      links: [
        { source: '燃气锅炉', target: '换热站' },
        { source: '热泵', target: '换热站' },
        { source: '光伏', target: '热泵' },
        { source: '蓄热水箱', target: '换热站' },
        { source: '燃气锅炉', target: '蓄热水箱' }
      ],
      roam: true,
      label: { show: true }
    }]
  }
  chart.setOption(option)
}

const initEnergyDistChart = () => {
  const chart = echarts.init(energyDistChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 55, name: '燃气', itemStyle: { color: '#409eff' } },
        { value: 30, name: '电力', itemStyle: { color: '#e6a23c' } },
        { value: 10, name: '光伏', itemStyle: { color: '#67c23a' } },
        { value: 5, name: '余热', itemStyle: { color: '#909399' } }
      ]
    }]
  }
  chart.setOption(option)
}

onMounted(() => {
  initEnergyArchChart()
  initEnergyDistChart()
})
</script>

<style scoped>
.multi-energy-optimization {
  padding: 0;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.chart-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>
