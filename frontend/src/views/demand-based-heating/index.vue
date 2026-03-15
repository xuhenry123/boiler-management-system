<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>热用户按需供热管理</span>
          <div>
            <el-select v-model="heatingStrategy" placeholder="供热策略" style="width: 140px; margin-right: 10px">
              <el-option label="按需供热" value="demand" />
              <el-option label="定时供热" value="scheduled" />
              <el-option label"恒温供热" value="constant" />
            </el-select>
            <el-button type="primary" @click="handleRefresh">
              <el-icon><Refresh /></el-icon>
              刷新数据
            </el-button>
          </div>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>用户温度分布</span>
            </template>
            <div ref="tempDistChartRef" style="height: 280px"></div>
          </el-card>
        </el-col>
        <el-col :span="16">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>实时温度监控</span>
                <el-input v-model="searchKeyword" placeholder="搜索用户" style="width: 150px" size="small" clearable />
              </div>
            </template>
            <el-table :data="filteredTemperatureData" style="width: 100%" max-height="280">
              <el-table-column prop="userCode" label="用户编码" width="100" />
              <el-table-column prop="userName" label="用户姓名" width="100" />
              <el-table-column prop="buildingName" label="建筑物" />
              <el-table-column prop="temperature" label="当前温度(℃)" width="120">
                <template #default="{ row }">
                  <span :style="{ color: getTempColor(row.temperature, row.targetTemp) }">
                    {{ row.temperature }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="targetTemp" label="目标温度(℃)" width="120">
                <template #default="{ row }">
                  <el-input-number v-model="row.targetTemp" :min="16" :max="26" :step="0.5" size="small" controls-position="right" @change="updateTargetTemp(row)" />
                </template>
              </el-table-column>
              <el-table-column prop="valveStatus" label="阀门状态" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.valveStatus === 'on' ? 'success' : 'info'" size="small">
                    {{ row.valveStatus === 'on' ? '开启' : '关闭' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="collectTime" label="采集时间" width="160" />
              <el-table-column label="操作" width="180">
                <template #default="{ row }">
                  <el-button size="small" @click="viewPrediction(row)">预测</el-button>
                  <el-button size="small" :type="row.valveStatus === 'on' ? 'danger' : 'success'" @click="toggleValve(row)">
                    {{ row.valveStatus === 'on' ? '关闭' : '开启' }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>温度趋势预测</span>
                <div>
                  <el-select v-model="selectedUser" placeholder="选择用户" style="width: 150px; margin-right: 10px" @change="loadPrediction">
                    <el-option v-for="item in temperatureData" :key="item.userCode" :label="item.userName" :value="item.userCode" />
                  </el-select>
                  <el-select v-model="predictHours" style="width: 120px" @change="loadPrediction">
                    <el-option :value="2" label="预测2小时" />
                    <el-option :value="4" label="预测4小时" />
                    <el-option :value="6" label="预测6小时" />
                  </el-select>
                </div>
              </div>
            </template>
            <div ref="predictChartRef" style="height: 350px"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>供热需求统计</span>
            </template>
            <div ref="demandChartRef" style="height: 300px"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>按需供热控制面板</span>
            </template>
            <el-form label-width="120px">
              <el-form-item label="控制模式">
                <el-radio-group v-model="controlMode">
                  <el-radio label="auto">自动控制</el-radio>
                  <el-radio label="manual">手动控制</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="温度允许偏差">
                <el-slider v-model="tempDeviation" :min="0.5" :max="3" :step="0.5" :marks="tempDeviationMarks" />
              </el-form-item>
              <el-form-item label="预热提前时间">
                <el-input-number v-model="preheatTime" :min="0" :max="120" :step="10" />
                <span style="margin-left: 10px">分钟</span>
              </el-form-item>
              <el-form-item label="节能模式">
                <el-switch v-model="energySavingMode" active-text="开启" inactive-text="关闭" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveControlConfig">保存配置</el-button>
                <el-button @click="applyToAll">应用到全部</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
    
    <el-dialog v-model="predictionDialogVisible" title="温度预测详情" width="700px">
      <el-table :data="predictionData" style="width: 100%">
        <el-table-column prop="predictTime" label="预测时间" />
        <el-table-column prop="predictedTemp" label="预测温度(℃)" />
        <el-table-column prop="confidenceLower" label="置信区间下限" />
        <el-table-column prop="confidenceUpper" label="置信区间上限" />
        <el-table-column prop="confidenceLevel" label="置信度" />
        <el-table-column prop="recommendation" label="调控建议">
          <template #default="{ row }">
            <el-tag v-if="row.recommendation === 'increase'" type="danger">增加供热</el-tag>
            <el-tag v-else-if="row.recommendation === 'decrease'" type="success">减少供热</el-tag>
            <el-tag v-else type="info">保持当前</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="predictionDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="applyPrediction">应用调控</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

const tempDistChartRef = ref(null)
const predictChartRef = ref(null)
const demandChartRef = ref(null)

const predictHours = ref(2)
const selectedUser = ref('')
const predictionDialogVisible = ref(false)
const predictionData = ref([])
const searchKeyword = ref('')
const heatingStrategy = ref('demand')
const controlMode = ref('auto')
const tempDeviation = ref(1.5)
const preheatTime = ref(30)
const energySavingMode = ref(true)

const tempDeviationMarks = {
  0.5: '0.5℃',
  1: '1℃',
  2: '2℃',
  3: '3℃'
}

const temperatureData = ref([
  { userCode: 'U001', userName: '张三', buildingName: '阳光花园1号楼', temperature: 19.5, targetTemp: 20.0, valveStatus: 'on', collectTime: '2026-03-15 10:00:00' },
  { userCode: 'U002', userName: '李四', buildingName: '阳光花园1号楼', temperature: 20.5, targetTemp: 21.0, valveStatus: 'on', collectTime: '2026-03-15 10:00:00' },
  { userCode: 'U003', userName: '王五', buildingName: '阳光花园1号楼', temperature: 19.0, targetTemp: 20.0, valveStatus: 'on', collectTime: '2026-03-15 10:00:00' },
  { userCode: 'U004', userName: '赵六', buildingName: '阳光花园2号楼', temperature: 21.5, targetTemp: 22.0, valveStatus: 'on', collectTime: '2026-03-15 10:00:00' },
  { userCode: 'U005', userName: '钱七', buildingName: '商业大厦A座', temperature: 20.0, targetTemp: 20.0, valveStatus: 'off', collectTime: '2026-03-15 10:00:00' },
  { userCode: 'U006', userName: '孙八', buildingName: '商业大厦A座', temperature: 20.2, targetTemp: 20.0, valveStatus: 'on', collectTime: '2026-03-15 10:00:00' },
  { userCode: 'U007', userName: '周九', buildingName: '阳光花园2号楼', temperature: 18.5, targetTemp: 20.0, valveStatus: 'on', collectTime: '2026-03-15 10:00:00' },
  { userCode: 'U008', userName: '吴十', buildingName: '阳光花园3号楼', temperature: 22.0, targetTemp: 21.0, valveStatus: 'on', collectTime: '2026-03-15 10:00:00' }
])

const filteredTemperatureData = computed(() => {
  if (!searchKeyword.value) return temperatureData.value
  return temperatureData.value.filter(item => 
    item.userName.includes(searchKeyword.value) || 
    item.userCode.includes(searchKeyword.value) ||
    item.buildingName.includes(searchKeyword.value)
  )
})

const getTempColor = (current, target) => {
  const diff = current - target
  if (Math.abs(diff) <= 1) return '#67c23a'
  if (diff < -1) return '#409eff'
  return '#f56c6c'
}

const initTempDistChart = () => {
  const chart = echarts.init(tempDistChartRef.value)
  const normal = temperatureData.value.filter(t => Math.abs(t.temperature - t.targetTemp) <= 1).length
  const low = temperatureData.value.filter(t => t.temperature < t.targetTemp - 1).length
  const high = temperatureData.value.filter(t => t.temperature > t.targetTemp + 1).length
  
  const option = {
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [{
      type: 'pie',
      radius: ['30%', '70%'],
      center: ['50%', '45%'],
      data: [
        { value: normal, name: '正常(±1℃)', itemStyle: { color: '#67c23a' } },
        { value: low, name: '偏低(>1℃)', itemStyle: { color: '#409eff' } },
        { value: high, name: '偏高(>1℃)', itemStyle: { color: '#f56c6c' } }
      ]
    }]
  }
  chart.setOption(option)
}

const initPredictChart = () => {
  const chart = echarts.init(predictChartRef.value)
  const actualData = [19.5, 19.8, 20.0, 20.2, 20.3, 20.5, 20.6]
  const predictData = [null, null, null, 20.2, 20.3, 20.5, 20.6]
  const confidenceUpper = [null, null, null, 20.5, 20.7, 20.9, 21.0]
  const confidenceLower = [null, null, null, 19.9, 19.9, 20.1, 20.2]
  
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['实际温度', '预测温度', '置信区间'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00']
    },
    yAxis: { type: 'value', name: '温度(℃)', min: 18, max: 23 },
    series: [
      { name: '实际温度', type: 'line', data: actualData, smooth: true, itemStyle: { color: '#409eff' } },
      { name: '预测温度', type: 'line', data: predictData, smooth: true, lineStyle: { type: 'dashed', color: '#e6a23c' } },
      { name: '置信区间', type: 'line', data: confidenceUpper, smooth: true, lineStyle: { opacity: 0 }, areaStyle: { opacity: 0.1, color: '#e6a23c' }, showSymbol: false },
      { name: '置信区间', type: 'line', data: confidenceLower, smooth: true, lineStyle: { opacity: 0 }, areaStyle: { opacity: 0.1, color: '#e6a23c' }, showSymbol: false }
    ]
  }
  chart.setOption(option)
}

const initDemandChart = () => {
  const chart = echarts.init(demandChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['热需求', '实际供热'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['1号楼', '2号楼', '3号楼', '商业大厦']
    },
    yAxis: { type: 'value', name: '功率(kW)' },
    series: [
      { name: '热需求', type: 'bar', data: [1200, 1500, 2000, 1800], itemStyle: { color: '#409eff' } },
      { name: '实际供热', type: 'bar', data: [1150, 1480, 1950, 1750], itemStyle: { color: '#67c23a' } }
    ]
  }
  chart.setOption(option)
}

const handleRefresh = () => {
  ElMessage.success('数据已刷新')
  initTempDistChart()
  initPredictChart()
  initDemandChart()
}

const viewPrediction = (row) => {
  selectedUser.value = row.userCode
  predictionData.value = [
    { predictTime: '2026-03-15 11:00', predictedTemp: 19.8, confidenceLower: 19.3, confidenceUpper: 20.3, confidenceLevel: '85%', recommendation: 'increase' },
    { predictTime: '2026-03-15 12:00', predictedTemp: 20.0, confidenceLower: 19.4, confidenceUpper: 20.6, confidenceLevel: '82%', recommendation: 'maintain' },
    { predictTime: '2026-03-15 13:00', predictedTemp: 20.2, confidenceLower: 19.5, confidenceUpper: 20.9, confidenceLevel: '80%', recommendation: 'maintain' },
    { predictTime: '2026-03-15 14:00', predictedTemp: 20.3, confidenceLower: 19.5, confidenceUpper: 21.1, confidenceLevel: '78%', recommendation: 'decrease' }
  ]
  predictionDialogVisible.value = true
}

const updateTargetTemp = (row) => {
  ElMessage.success(`用户 ${row.userName} 目标温度已更新为 ${row.targetTemp}℃`)
}

const toggleValve = (row) => {
  row.valveStatus = row.valveStatus === 'on' ? 'off' : 'on'
  ElMessage.success(`用户 ${row.userName} 阀门已${row.valveStatus === 'on' ? '开启' : '关闭'}`)
}

const loadPrediction = () => {
  initPredictChart()
}

const saveControlConfig = () => {
  ElMessage.success('控制配置已保存')
}

const applyToAll = () => {
  ElMessage.success('配置已应用到全部用户')
}

const applyPrediction = () => {
  ElMessage.success('预测调控方案已应用')
  predictionDialogVisible.value = false
}

onMounted(() => {
  initTempDistChart()
  initPredictChart()
  initDemandChart()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
