<template>
  <div class="secondary-temp-control">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>二级网控温系统</span>
          <div>
            <el-select v-model="selectedStation" placeholder="选择换热站" size="small" style="width: 150px; margin-right: 10px" @change="onStationChange">
              <el-option v-for="item in stations" :key="item" :label="item" :value="item" />
            </el-select>
            <el-button type="primary" @click="handleRefresh">
              <el-icon><Refresh /></el-icon>
              刷新数据
            </el-button>
          </div>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <div class="stat-icon" style="background: #409eff">
                <el-icon>< Sunny /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ supplyTemp }}°C</div>
                <div class="stat-label">供水温度</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <div class="stat-icon" style="background: #67c23a">
                <el-icon><Sunny /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ returnTemp }}°C</div>
                <div class="stat-label">回水温度</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <div class="stat-icon" style="background: #e6a23c">
                <el-icon><Odometer /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ controlDeviation }}°C</div>
                <div class="stat-label">控制偏差</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <div class="stat-icon" style="background: #f56c6c">
                <el-icon><Operation /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ valvePosition }}%</div>
                <div class="stat-label">阀门开度</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="16">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>温度控制曲线</span>
                <div>
                  <el-radio-group v-model="timeRange" size="small" @change="loadChartData">
                    <el-radio-button label="day">日</el-radio-button>
                    <el-radio-button label="week">周</el-radio-button>
                  </el-radio-group>
                </div>
              </div>
            </template>
            <div ref="tempControlChartRef" style="height: 400px"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>PID控制参数</span>
              </div>
            </template>
            <el-form label-width="80px">
              <el-form-item label="Kp">
                <el-input-number v-model="pidParams.kp" :step="0.1" :precision="2" />
              </el-form-item>
              <el-form-item label="Ki">
                <el-input-number v-model="pidParams.ki" :step="0.01" :precision="3" />
              </el-form-item>
              <el-form-item label="Kd">
                <el-input-number v-model="pidParams.kd" :step="0.01" :precision="3" />
              </el-form-item>
              <el-form-item label="设定值">
                <el-input-number v-model="pidParams.setpoint" :min="30" :max="60" />
                <span style="margin-left: 10px">°C</span>
              </el-form-item>
              <el-form-item label="控制模式">
                <el-radio-group v-model="controlMode">
                  <el-radio label="auto">自动</el-radio>
                  <el-radio label="manual">手动</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="输出限制">
                <el-slider v-model="outputLimit" range :min="0" :max="100" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveParams">保存参数</el-button>
                <el-button @click="autoTuning">自整定</el-button>
                <el-button @click="resetParams">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>各区域温度分布</span>
                <el-select v-model="buildingFilter" placeholder="筛选区域" size="small" style="width: 120px" clearable>
                  <el-option label="全部" value="" />
                  <el-option label="1号楼" value="1" />
                  <el-option label="2号楼" value="2" />
                  <el-option label="3号楼" value="3" />
                </el-select>
              </div>
            </template>
            <div ref="zoneTempChartRef" style="height: 300px"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>控制效果分析</span>
            </template>
            <el-table :data="controlEffectData" style="width: 100%">
              <el-table-column prop="indicator" label="指标" />
              <el-table-column prop="value" label="数值" />
              <el-table-column prop="standard" label="标准" />
              <el-table-column prop="status" label="状态" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.status === '达标' ? 'success' : 'danger'">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
            <div style="margin-top: 15px">
              <el-button type="primary" size="small" @click="exportReport">导出报告</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>二级网控制历史记录</span>
                <div>
                  <el-date-picker v-model="historyDateRange" type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" size="small" style="width: 360px" />
                  <el-button type="primary" size="small" style="margin-left: 10px" @click="loadHistory">查询</el-button>
                </div>
              </div>
            </template>
            <el-table :data="historyData" style="width: 100%" max-height="250">
              <el-table-column prop="time" label="时间" width="180" />
              <el-table-column prop="station" label="换热站" width="120" />
              <el-table-column prop="setpoint" label="设定温度(°C)" width="100" />
              <el-table-column prop="actualTemp" label="实际温度(°C)" width="100" />
              <el-table-column prop="deviation" label="偏差(°C)" width="80" />
              <el-table-column prop="valvePosition" label="阀位(%)" width="80" />
              <el-table-column prop="controlMode" label="控制模式" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.controlMode === 'auto' ? 'success' : 'info'" size="small">
                    {{ row.controlMode === 'auto' ? '自动' : '手动' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="row.status === '正常' ? 'success' : row.status === '报警' ? 'warning' : 'danger'" size="small">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination style="margin-top: 15px; text-align: right" :current-page="historyPage" :page-size="pageSize" :total="historyTotal" layout="total, prev, pager, next" />
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { Refresh, Sunny, Odometer, Operation } from '@element-plus/icons-vue'

const tempControlChartRef = ref(null)
const zoneTempChartRef = ref(null)

const selectedStation = ref('换热站1')
const stations = ref(['换热站1', '换热站2', '换热站3'])
const timeRange = ref('day')
const buildingFilter = ref('')
const historyDateRange = ref([])
const historyPage = ref(1)
const pageSize = ref(10)
const historyTotal = ref(50)
const outputLimit = ref([20, 80])

const supplyTemp = ref(50.2)
const returnTemp = ref(40.5)
const controlDeviation = ref(0.2)
const valvePosition = ref(65)

const controlMode = ref('auto')
const pidParams = ref({
  kp: 2.5,
  ki: 0.15,
  kd: 0.8,
  setpoint: 50
})

const controlEffectData = ref([
  { indicator: '温度达标率', value: '98.5%', standard: '≥95%', status: '达标' },
  { indicator: '最大偏差', value: '1.2°C', standard: '≤2°C', status: '达标' },
  { indicator: '调节频率', value: '15次/h', standard: '≤20次/h', status: '达标' },
  { indicator: '响应时间', value: '5min', standard: '≤10min', status: '达标' },
  { indicator: '超调量', value: '0.8°C', standard: '≤1.5°C', status: '达标' }
])

const historyData = ref([
  { time: '2026-03-15 10:00:00', station: '换热站1', setpoint: 50, actualTemp: 50.2, deviation: 0.2, valvePosition: 65, controlMode: 'auto', status: '正常' },
  { time: '2026-03-15 09:50:00', station: '换热站1', setpoint: 50, actualTemp: 49.8, deviation: -0.2, valvePosition: 63, controlMode: 'auto', status: '正常' },
  { time: '2026-03-15 09:40:00', station: '换热站1', setpoint: 50, actualTemp: 50.5, deviation: 0.5, valvePosition: 68, controlMode: 'auto', status: '正常' },
  { time: '2026-03-15 09:30:00', station: '换热站2', setpoint: 48, actualTemp: 48.2, deviation: 0.2, valvePosition: 55, controlMode: 'auto', status: '正常' },
  { time: '2026-03-15 09:20:00', station: '换热站1', setpoint: 50, actualTemp: 51.5, deviation: 1.5, valvePosition: 72, controlMode: 'auto', status: '报警' }
])

const onStationChange = () => {
  ElMessage.info(`已切换到${selectedStation.value}`)
  loadChartData()
}

const handleRefresh = () => {
  ElMessage.success('数据已刷新')
  loadChartData()
}

const loadChartData = () => {
  initTempControlChart()
  initZoneTempChart()
}

const saveParams = () => {
  ElMessage.success('PID参数已保存')
}

const autoTuning = () => {
  ElMessage.info('正在自动整定PID参数...')
  setTimeout(() => {
    pidParams.value = { kp: 2.8, ki: 0.18, kd: 0.75, setpoint: 50 }
    ElMessage.success('PID参数自整定完成')
  }, 2000)
}

const resetParams = () => {
  pidParams.value = { kp: 2.5, ki: 0.15, kd: 0.8, setpoint: 50 }
  ElMessage.success('参数已重置为默认值')
}

const exportReport = () => {
  ElMessage.info('正在生成控制效果报告...')
}

const loadHistory = () => {
  ElMessage.success('历史记录已刷新')
}

const initTempControlChart = () => {
  const chart = echarts.init(tempControlChartRef.value)
  let xAxisData = []
  let setpointData = []
  let actualData = []
  
  if (timeRange.value === 'day') {
    xAxisData = ['00:00', '02:00', '04:00', '06:00', '08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00']
    setpointData = [50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50]
    actualData = [49.8, 50.1, 49.9, 50.2, 50.0, 50.2, 49.9, 50.1, 50.3, 49.8, 50.0, 50.2]
  } else {
    xAxisData = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    setpointData = [50, 50, 50, 50, 50, 48, 48]
    actualData = [49.8, 50.2, 49.9, 50.1, 50.0, 47.9, 48.1]
  }
  
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['设定值', '实际值', '偏差范围'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: xAxisData },
    yAxis: { type: 'value', name: '温度(°C)' },
    series: [
      { name: '设定值', type: 'line', data: setpointData, lineStyle: { type: 'dashed', color: '#909399' }, itemStyle: { color: '#909399' } },
      { name: '实际值', type: 'line', smooth: true, data: actualData, itemStyle: { color: '#409eff' } },
      { name: '偏差范围', type: 'line', data: setpointData.map(v => v + 1), lineStyle: { opacity: 0.3, type: 'dotted' }, showSymbol: false, areaStyle: { opacity: 0.1 } },
      { name: '偏差范围', type: 'line', data: setpointData.map(v => v - 1), lineStyle: { opacity: 0.3, type: 'dotted' }, showSymbol: false, areaStyle: { opacity: 0.1 } }
    ]
  }
  chart.setOption(option)
}

const initZoneTempChart = () => {
  const chart = echarts.init(zoneTempChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['供水', '回水', '室内'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['1号楼', '2号楼', '3号楼', '4号楼', '5号楼'] },
    yAxis: { type: 'value', name: '温度(°C)' },
    series: [
      { name: '供水', type: 'bar', data: [50, 51, 49, 50, 52], itemStyle: { color: '#409eff' } },
      { name: '回水', type: 'bar', data: [40, 41, 39, 40, 42], itemStyle: { color: '#67c23a' } },
      { name: '室内', type: 'line', data: [20, 21, 19, 20, 22], itemStyle: { color: '#e6a23c' }, yAxisIndex: 1 }
    ]
  }
  chart.setOption(option)
}

onMounted(() => {
  initTempControlChart()
  initZoneTempChart()
})
</script>

<style scoped>
.secondary-temp-control {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-card {
  border-radius: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
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
</style>
