<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="设备监控" name="monitor">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>蒸汽锅炉设备列表</span>
              <el-button type="primary" @click="loadDevices">刷新</el-button>
            </div>
          </template>
          
          <el-row :gutter="20">
            <el-col :span="6" v-for="boiler in boilers" :key="boiler.id">
              <el-card shadow="hover" class="boiler-card">
                <div class="boiler-header">
                  <span class="boiler-name">{{ boiler.name }}</span>
                  <el-tag :type="boiler.status === 'online' ? 'success' : 'danger'">
                    {{ boiler.status === 'online' ? '在线' : '离线' }}
                  </el-tag>
                </div>
                <div class="boiler-info">
                  <div class="info-item">
                    <span class="label">企业</span>
                    <span class="value">{{ boiler.enterpriseName }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">额定蒸发量</span>
                    <span class="value">{{ boiler.capacity }} 吨/小时</span>
                  </div>
                  <div class="info-item">
                    <span class="label">型号</span>
                    <span class="value">{{ boiler.model }}</span>
                  </div>
                </div>
                <div class="boiler-actions">
                  <el-button size="small" type="primary" @click="viewDetail(boiler)">查看详情</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
        
        <el-card style="margin-top: 20px">
          <template #header>
            <span>实时数据监控</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <div ref="realtimeChartRef" style="height: 320px"></div>
            </el-col>
            <el-col :span="12">
              <div ref="efficiencyChartRef" style="height: 320px"></div>
            </el-col>
          </el-row>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="用户画像" name="profile">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>用户行为画像</span>
              <el-select v-model="selectedEnterprise" placeholder="选择企业" @change="loadProfile">
                <el-option v-for="ent in enterprises" :key="ent.id" :label="ent.name" :value="ent.id" />
              </el-select>
            </div>
          </template>
          
          <el-row :gutter="20" v-if="currentProfile">
            <el-col :span="12">
              <el-card shadow="hover">
                <template #header>
                  <span>生产规律分析</span>
                </template>
                <div class="profile-section">
                  <div class="profile-item">
                    <span class="label">生产时段</span>
                    <span class="value">{{ currentProfile.workHours || '未分析' }}</span>
                  </div>
                  <div class="profile-item">
                    <span class="label">峰值时段</span>
                    <span class="value">{{ currentProfile.peakHours || '未分析' }}</span>
                  </div>
                  <div class="profile-item">
                    <span class="label">平均蒸汽需求</span>
                    <span class="value">{{ currentProfile.avgSteamDemand || 0 }} 吨/小时</span>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="hover">
                <template #header>
                  <span>能耗特征</span>
                </template>
                <div class="profile-section">
                  <div class="profile-item">
                    <span class="label">画像评分</span>
                    <el-progress :percentage="currentProfile.profileScore || 0" :color="getScoreColor(currentProfile.profileScore)" />
                  </div>
                  <div class="profile-item">
                    <span class="label">更新日期</span>
                    <span class="value">{{ currentProfile.lastUpdated || '从未更新' }}</span>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          
          <el-empty v-else description="请选择企业查看画像" />
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="运行策略" name="strategy">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>锅炉运行策略</span>
              <el-button type="primary" @click="showStrategyDialog = true">新建策略</el-button>
            </div>
          </template>
          
          <el-table :data="strategies" stripe>
            <el-table-column prop="boilerId" label="锅炉" />
            <el-table-column prop="strategyType" label="策略类型">
              <template #default="{ row }">
                <el-tag v-if="row.strategyType === 'power'">功率策略</el-tag>
                <el-tag v-else-if="row.strategyType === 'schedule'">调度策略</el-tag>
                <el-tag v-else type="success">优化策略</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="targetPower" label="目标功率" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag v-if="row.status === 'active'" type="success">生效中</el-tag>
                <el-tag v-else-if="row.status === 'suspended'" type="warning">已暂停</el-tag>
                <el-tag v-else>草稿</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="activateStrategy(row)" v-if="row.status !== 'active'">激活</el-button>
                <el-button size="small" type="danger" @click="suspendStrategy(row)" v-if="row.status === 'active'">暂停</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="能耗监控" name="energy">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>能耗监控报表</span>
              <el-radio-group v-model="reportType" @change="loadReport">
                <el-radio label="daily">日报</el-radio>
                <el-radio label="monthly">月报</el-radio>
              </el-radio-group>
            </div>
          </template>
          
          <el-row :gutter="20">
            <el-col :span="6">
              <el-statistic title="蒸汽用量">
                <template #default>{{ totalSteam.toFixed(2) }} 吨</template>
              </el-statistic>
            </el-col>
            <el-col :span="6">
              <el-statistic title="用水量">
                <template #default>{{ totalWater.toFixed(2) }} 吨</template>
              </el-statistic>
            </el-col>
            <el-col :span="6">
              <el-statistic title="用电量">
                <template #default>{{ totalElectricity.toFixed(2) }} kWh</template>
              </el-statistic>
            </el-col>
            <el-col :span="6">
              <el-statistic title="标煤">
                <template #default>{{ totalStandardCoal.toFixed(2) }} 吨</template>
              </el-statistic>
            </el-col>
          </el-row>
          
          <el-row :gutter="20" style="margin-top: 20px">
            <el-col :span="24">
              <div ref="energyChartRef" style="height: 320px"></div>
            </el-col>
          </el-row>
          
          <el-table :data="energyData" stripe style="margin-top: 20px">
            <el-table-column prop="enterpriseName" label="企业" />
            <el-table-column prop="steamAmount" label="蒸汽(吨)" />
            <el-table-column prop="waterAmount" label="用水(吨)" />
            <el-table-column prop="electricityAmount" label="用电(kWh)" />
            <el-table-column prop="standardCoal" label="标煤(吨)" />
            <el-table-column prop="recordDate" label="日期" />
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const activeTab = ref('monitor')
const boilers = ref([])
const enterprises = ref([
  { id: 'ent001', name: '某制造企业' },
  { id: 'ent002', name: '某食品企业' }
])
const selectedEnterprise = ref('')
const currentProfile = ref(null)
const strategies = ref([])
const energyData = ref([])
const reportType = ref('daily')

const realtimeChartRef = ref(null)
const efficiencyChartRef = ref(null)
const energyChartRef = ref(null)

const totalSteam = ref(0)
const totalWater = ref(0)
const totalElectricity = ref(0)
const totalStandardCoal = ref(0)

const showStrategyDialog = ref(false)

const loadDevices = async () => {
  boilers.value = [
    { id: '1', name: '1号蒸汽锅炉', enterpriseName: '某制造企业', capacity: 10, model: 'LSS10-1.0', status: 'online' },
    { id: '2', name: '2号蒸汽锅炉', enterpriseName: '某制造企业', capacity: 15, model: 'LSS15-1.0', status: 'online' },
    { id: '3', name: '3号蒸汽锅炉', enterpriseName: '某食品企业', capacity: 8, model: 'LSS8-1.0', status: 'offline' }
  ]
  initRealtimeChart()
  initEfficiencyChart()
}

const loadProfile = async () => {
  currentProfile.value = {
    workHours: '08:00-20:00',
    peakHours: '10:00-14:00',
    avgSteamDemand: 8.5,
    profileScore: 85,
    lastUpdated: '2026-03-15'
  }
}

const loadStrategies = async () => {
  strategies.value = [
    { id: '1', boilerId: '1号蒸汽锅炉', strategyType: 'power', targetPower: '80%', status: 'active', createTime: '2026-03-10' },
    { id: '2', boilerId: '2号蒸汽锅炉', strategyType: 'schedule', targetPower: '60%', status: 'draft', createTime: '2026-03-12' }
  ]
}

const loadReport = async () => {
  energyData.value = [
    { enterpriseName: '某制造企业', steamAmount: 120, waterAmount: 150, electricityAmount: 3000, standardCoal: 18.5, recordDate: '2026-03-15' },
    { enterpriseName: '某食品企业', steamAmount: 80, waterAmount: 100, electricityAmount: 2000, standardCoal: 12.3, recordDate: '2026-03-15' }
  ]
  calculateTotals()
  nextTick(() => initEnergyChart())
}

const calculateTotals = () => {
  totalSteam.value = energyData.value.reduce((sum, item) => sum + (item.steamAmount || 0), 0)
  totalWater.value = energyData.value.reduce((sum, item) => sum + (item.waterAmount || 0), 0)
  totalElectricity.value = energyData.value.reduce((sum, item) => sum + (item.electricityAmount || 0), 0)
  totalStandardCoal.value = energyData.value.reduce((sum, item) => sum + (item.standardCoal || 0), 0)
}

const activateStrategy = async (strategy) => {
  ElMessage.success('策略已激活')
  loadStrategies()
}

const suspendStrategy = async (strategy) => {
  ElMessage.warning('策略已暂停')
  loadStrategies()
}

const viewDetail = (boiler) => {
  ElMessage.info(`查看 ${boiler.name} 详情`)
}

const initRealtimeChart = () => {
  if (!realtimeChartRef.value) return
  const chart = echarts.init(realtimeChartRef.value)
  const option = {
    title: { text: '蒸汽压力/温度实时曲线' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['压力', '温度'] },
    xAxis: { type: 'category', data: ['10:00', '10:05', '10:10', '10:15', '10:20', '10:25', '10:30'] },
    yAxis: [
      { type: 'value', name: '压力(MPa)' },
      { type: 'value', name: '温度(°C)' }
    ],
    series: [
      { name: '压力', type: 'line', data: [0.85, 0.87, 0.86, 0.88, 0.87, 0.89, 0.88] },
      { name: '温度', type: 'line', yAxisIndex: 1, data: [175, 178, 176, 180, 178, 182, 180] }
    ]
  }
  chart.setOption(option)
}

const initEfficiencyChart = () => {
  if (!efficiencyChartRef.value) return
  const chart = echarts.init(efficiencyChartRef.value)
  const option = {
    title: { text: '锅炉效率对比' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['1号锅炉', '2号锅炉', '3号锅炉'] },
    yAxis: { type: 'value', name: '效率(%)' },
    series: [{ name: '效率', type: 'bar', data: [92, 89, 85] }]
  }
  chart.setOption(option)
}

const initEnergyChart = () => {
  if (!energyChartRef.value) return
  const chart = echarts.init(energyChartRef.value)
  const option = {
    title: { text: '能耗趋势' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['蒸汽', '用水', '用电'] },
    xAxis: { type: 'category', data: ['03-11', '03-12', '03-13', '03-14', '03-15'] },
    yAxis: { type: 'value' },
    series: [
      { name: '蒸汽', type: 'line', data: [180, 195, 188, 200, 200] },
      { name: '用水', type: 'line', data: [220, 240, 230, 250, 250] },
      { name: '用电', type: 'line', data: [4500, 4800, 4600, 5000, 5000] }
    ]
  }
  chart.setOption(option)
}

const getScoreColor = (score) => {
  if (score >= 80) return '#67C23A'
  if (score >= 60) return '#E6A23C'
  return '#F56C6C'
}

const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#67C23A'
  if (percentage >= 60) return '#E6A23C'
  return '#F56C6C'
}

onMounted(() => {
  loadDevices()
  loadStrategies()
  loadReport()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.boiler-card {
  margin-bottom: 20px;
}

.boiler-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.boiler-name {
  font-size: 16px;
  font-weight: bold;
}

.boiler-info {
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.info-item .label {
  color: #909399;
}

.info-item .value {
  font-weight: 500;
}

.boiler-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.profile-section {
  padding: 10px;
}

.profile-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.profile-item .label {
  color: #909399;
}

.profile-item .value {
  font-weight: 500;
}
</style>
