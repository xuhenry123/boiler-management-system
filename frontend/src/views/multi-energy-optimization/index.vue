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
              <el-icon :size="32"><CircleCheck /></el-icon>
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
              <el-button type="primary" size="small" @click="refreshArchitecture">刷新</el-button>
            </div>
          </template>
          <div ref="energyArchChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span>优化目标配置</span>
          </template>
          <el-form label-width="100px">
            <el-form-item label="优化目标">
              <el-radio-group v-model="optimizeConfig.target" @change="onTargetChange">
                <el-radio label="economy">经济优先</el-radio>
                <el-radio label="environment">环保优先</el-radio>
                <el-radio label="balance">综合平衡</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="经济权重">
              <el-slider v-model="optimizeConfig.economyWeight" :min="0" :max="100" show-input :disabled="optimizeConfig.target !== 'custom'" />
            </el-form-item>
            <el-form-item label="环保权重">
              <el-slider v-model="optimizeConfig.environmentWeight" :min="0" :max="100" show-input :disabled="optimizeConfig.target !== 'custom'" />
            </el-form-item>
            <el-form-item label="可靠性权重">
              <el-slider v-model="optimizeConfig.reliabilityWeight" :min="0" :max="100" show-input :disabled="optimizeConfig.target !== 'custom'" />
            </el-form-item>
            <el-form-item label="约束条件">
              <el-checkbox-group v-model="optimizeConfig.constraints">
                <el-checkbox label="minReliability">最低可靠性≥95%</el-checkbox>
                <el-checkbox label="maxEmissions">碳排放上限</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="runOptimization">执行优化</el-button>
              <el-button @click="saveConfig">保存配置</el-button>
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
              <el-select v-model="energyViewType" size="small" style="width: 100px">
                <el-option label="功率" value="power" />
                <el-option label="能耗" value="energy" />
                <el-option label="成本" value="cost" />
              </el-select>
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
              <el-button type="primary" size="small" @click="refreshPlans">刷新方案</el-button>
            </div>
          </template>
          <el-table :data="optimizePlans" style="width: 100%">
            <el-table-column prop="name" label="方案名称" />
            <el-table-column prop="cost" label="成本(元)" width="100" />
            <el-table-column prop="emissions" label="碳排放(t)" width="100" />
            <el-table-column prop="reliability" label="可靠性(%)" width="100" />
            <el-table-column prop="score" label="综合评分" width="80">
              <template #default="{ row }">
                <el-tag :type="row.score >= 90 ? 'success' : row.score >= 80 ? 'warning' : 'info'">{{ row.score }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="adoptPlan(row)">采用</el-button>
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
              <div>
                <el-date-picker v-model="scheduleDate" type="date" placeholder="选择日期" size="small" style="width: 150px; margin-right: 10px" />
                <el-button type="primary" size="small" @click="generatePlan">生成计划</el-button>
                <el-button size="small" @click="exportPlan">导出</el-button>
              </div>
            </div>
          </template>
          <el-table :data="schedulePlan" style="width: 100%">
            <el-table-column prop="time" label="时段" width="120" />
            <el-table-column prop="heatLoad" label="热负荷(MW)" width="120" />
            <el-table-column prop="boiler" label="锅炉方案" width="150">
              <template #default="{ row }">
                <el-tag size="small">{{ row.boiler }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="heatPump" label="热泵方案" width="150">
              <template #default="{ row }">
                <el-tag size="small" type="success">{{ row.heatPump }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="photovoltaic" label="光伏" width="100">
              <template #default="{ row }">
                <span>{{ row.photovoltaic }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="cost" label="成本(元)" width="100" />
            <el-table-column prop="emissions" label="碳排放(kg)" width="100" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === '已执行' ? 'success' : row.status === '执行中' ? 'warning' : 'info'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default>
                <el-button type="primary" link size="small">调整</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header">
            <span>设备运行状态</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="6" v-for="device in devices" :key="device.name">
              <el-card shadow="hover" class="device-card">
                <div class="device-header">
                  <span class="device-name">{{ device.name }}</span>
                  <el-tag :type="device.status === '运行' ? 'success' : 'info'" size="small">{{ device.status }}</el-tag>
                </div>
                <div class="device-info">
                  <div class="info-row">
                    <span>功率:</span>
                    <span>{{ device.power }}MW</span>
                  </div>
                  <div class="info-row">
                    <span>效率:</span>
                    <span>{{ device.efficiency }}%</span>
                  </div>
                  <div class="info-row">
                    <span>运行时间:</span>
                    <span>{{ device.runTime }}h</span>
                  </div>
                </div>
                <el-progress :percentage="device.loadRate" :stroke-width="8" :color="getLoadColor(device.loadRate)" style="margin-top: 10px" />
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Coin, TrendCharts, CircleCheck, Cpu } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

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
  reliabilityWeight: 30,
  constraints: ['minReliability']
})

const energyViewType = ref('power')
const scheduleDate = ref(new Date())

const optimizePlans = ref([
  { name: '经济最优方案', cost: 26500, emissions: 48.2, reliability: 98.5, score: 92 },
  { name: '环保最优方案', cost: 29800, emissions: 35.6, reliability: 99.0, score: 88 },
  { name: '综合平衡方案', cost: 27500, emissions: 42.5, reliability: 99.2, score: 95 }
])

const schedulePlan = ref([
  { time: '00:00-04:00', heatLoad: 35, boiler: '1号锅炉50%', heatPump: '关闭', photovoltaic: '0kW', cost: 3500, emissions: 5200, status: '已执行' },
  { time: '04:00-08:00', heatLoad: 45, boiler: '1号锅炉70%', heatPump: '开启1台', photovoltaic: '50kW', cost: 4200, emissions: 6100, status: '执行中' },
  { time: '08:00-12:00', heatLoad: 55, boiler: '1+2号锅炉', heatPump: '开启2台', photovoltaic: '120kW', cost: 5800, emissions: 8200, status: '待执行' },
  { time: '12:00-17:00', heatLoad: 50, boiler: '1号锅炉80%', heatPump: '开启2台', photovoltaic: '180kW', cost: 5100, emissions: 7400, status: '待执行' },
  { time: '17:00-21:00', heatLoad: 58, boiler: '1+2号锅炉', heatPump: '开启2台', photovoltaic: '80kW', cost: 6200, emissions: 8800, status: '待执行' },
  { time: '21:00-24:00', heatLoad: 42, boiler: '1号锅炉60%', heatPump: '开启1台', photovoltaic: '0kW', cost: 4000, emissions: 5800, status: '待执行' }
])

const devices = ref([
  { name: '1号燃气锅炉', status: '运行', power: 8.5, efficiency: 95, loadRate: 85, runTime: 120 },
  { name: '2号燃气锅炉', status: '运行', power: 6.0, efficiency: 93, loadRate: 60, runTime: 80 },
  { name: '空气源热泵1', status: '运行', power: 3.2, efficiency: 280, loadRate: 80, runTime: 200 },
  { name: '空气源热泵2', status: '停止', power: 0, efficiency: 0, loadRate: 0, runTime: 0 }
])

const onTargetChange = () => {
  if (optimizeConfig.value.target === 'economy') {
    optimizeConfig.value.economyWeight = 70
    optimizeConfig.value.environmentWeight = 15
    optimizeConfig.value.reliabilityWeight = 15
  } else if (optimizeConfig.value.target === 'environment') {
    optimizeConfig.value.economyWeight = 15
    optimizeConfig.value.environmentWeight = 70
    optimizeConfig.value.reliabilityWeight = 15
  } else {
    optimizeConfig.value.economyWeight = 40
    optimizeConfig.value.environmentWeight = 30
    optimizeConfig.value.reliabilityWeight = 30
  }
}

const refreshArchitecture = () => {
  initEnergyArchChart()
  ElMessage.success('系统架构已刷新')
}

const runOptimization = () => {
  ElMessage.info('正在执行多能耦合优化计算...')
  setTimeout(() => {
    ElMessage.success('优化计算完成')
    refreshPlans()
  }, 2000)
}

const saveConfig = () => {
  ElMessage.success('优化配置已保存')
}

const refreshPlans = () => {
  initEnergyDistChart()
}

const adoptPlan = (plan) => {
  ElMessage.success(`已采用方案: ${plan.name}`)
  generatePlan()
}

const generatePlan = () => {
  ElMessage.success('调度计划已生成')
}

const exportPlan = () => {
  ElMessage.info('正在导出调度计划...')
}

const getLoadColor = (rate) => {
  if (rate >= 80) return '#f56c6c'
  if (rate >= 50) return '#e6a23c'
  return '#67c23a'
}

const initEnergyArchChart = () => {
  const chart = echarts.init(energyArchChartRef.value)
  const option = {
    tooltip: {},
    series: [{
      type: 'graph',
      layout: 'force',
      force: { repulsion: 300 },
      nodes: [
        { name: '燃气锅炉', x: 100, y: 100, symbolSize: 70, itemStyle: { color: '#409eff' }, category: 0 },
        { name: '热泵', x: 200, y: 50, symbolSize: 60, itemStyle: { color: '#67c23a' }, category: 1 },
        { name: '光伏', x: 280, y: 100, symbolSize: 50, itemStyle: { color: '#e6a23c' }, category: 2 },
        { name: '蓄热水箱', x: 150, y: 200, symbolSize: 60, itemStyle: { color: '#909399' }, category: 3 },
        { name: '换热站', x: 100, y: 300, symbolSize: 70, itemStyle: { color: '#f56c6c' }, category: 4 },
        { name: '电网', x: 300, y: 250, symbolSize: 40, itemStyle: { color: '#b37feb' }, category: 5 }
      ],
      links: [
        { source: '燃气锅炉', target: '换热站' },
        { source: '燃气锅炉', target: '蓄热水箱' },
        { source: '热泵', target: '换热站' },
        { source: '光伏', target: '热泵' },
        { source: '光伏', target: '电网' },
        { source: '蓄热水箱', target: '换热站' },
        { source: '电网', target: '热泵' }
      ],
      roam: true,
      label: { show: true },
      edgeSymbol: ['circle', 'arrow'],
      edgeSymbolSize: [6, 12],
      categories: [
        { name: '热源' },
        { name: '热泵' },
        { name: '光伏' },
        { name: '储能' },
        { name: '用户' },
        { name: '电网' }
      ]
    }]
  }
  chart.setOption(option)
}

const initEnergyDistChart = () => {
  const chart = echarts.init(energyDistChartRef.value)
  
  let data = []
  if (energyViewType.value === 'power') {
    data = [
      { value: 14.5, name: '燃气', itemStyle: { color: '#409eff' } },
      { value: 6.4, name: '电力', itemStyle: { color: '#e6a23c' } },
      { value: 0.3, name: '光伏', itemStyle: { color: '#67c23a' } },
      { value: 0.1, name: '余热', itemStyle: { color: '#909399' } }
    ]
  } else if (energyViewType.value === 'energy') {
    data = [
      { value: 55, name: '燃气', itemStyle: { color: '#409eff' } },
      { value: 30, name: '电力', itemStyle: { color: '#e6a23c' } },
      { value: 10, name: '光伏', itemStyle: { color: '#67c23a' } },
      { value: 5, name: '余热', itemStyle: { color: '#909399' } }
    ]
  } else {
    data = [
      { value: 16000, name: '燃气', itemStyle: { color: '#409eff' } },
      { value: 8500, name: '电力', itemStyle: { color: '#e6a23c' } },
      { value: 500, name: '光伏', itemStyle: { color: '#67c23a' } },
      { value: 300, name: '余热', itemStyle: { color: '#909399' } }
    ]
  }
  
  const option = {
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: data,
      label: { show: true, formatter: '{b}: {d}%' }
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

.device-card {
  margin-bottom: 10px;
}

.device-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.device-name {
  font-weight: bold;
  font-size: 14px;
}

.device-info {
  font-size: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.info-row span:first-child {
  color: #909399;
}
</style>
