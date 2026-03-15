<template>
  <div class="secondary-temp-control">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><Temperature /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ supplyTemp }}°C</div>
              <div class="stat-label">当前供水温度</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><Temperature /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ returnTemp }}°C</div>
              <div class="stat-label">当前回水温度</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><Odometer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ controlDeviation }}°C</div>
              <div class="stat-label">控制偏差</div>
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
              <span>温度控制曲线</span>
              <el-select v-model="selectedStation" placeholder="选择换热站" size="small" style="width: 150px">
                <el-option v-for="item in stations" :key="item" :label="item" :value="item" />
              </el-select>
            </div>
          </template>
          <div ref="tempControlChartRef" style="height: 400px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>PID控制参数</span>
            </div>
          </template>
          <el-form label-width="80px">
            <el-form-item label="Kp">
              <el-input-number v-model="pidParams.kp" :step="0.1" />
            </el-form-item>
            <el-form-item label="Ki">
              <el-input-number v-model="pidParams.ki" :step="0.01" />
            </el-form-item>
            <el-form-item label="Kd">
              <el-input-number v-model="pidParams.kd" :step="0.01" />
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
            <el-form-item>
              <el-button type="primary" @click="saveParams">保存参数</el-button>
              <el-button @click="autoTuning">自整定</el-button>
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
              <span>各区域温度分布</span>
            </div>
          </template>
          <div ref="zoneTempChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>控制效果分析</span>
            </div>
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
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Temperature, Odometer } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const tempControlChartRef = ref(null)
const zoneTempChartRef = ref(null)

const selectedStation = ref('换热站1')
const stations = ref(['换热站1', '换热站2', '换热站3'])

const supplyTemp = ref(50.2)
const returnTemp = ref(40.5)
const controlDeviation = ref(0.2)

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
  { indicator: '响应时间', value: '5min', standard: '≤10min', status: '达标' }
])

const saveParams = () => {
  console.log('保存PID参数')
}

const autoTuning = () => {
  console.log('自整定')
}

const initTempControlChart = () => {
  const chart = echarts.init(tempControlChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['设定值', '实际值'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'] },
    yAxis: { type: 'value', name: '温度(°C)' },
    series: [
      { name: '设定值', type: 'line', data: [50, 50, 50, 50, 50, 50], lineStyle: { type: 'dashed' } },
      { name: '实际值', type: 'line', smooth: true, data: [49.8, 50.2, 50.1, 49.9, 50.3, 50.0] }
    ]
  }
  chart.setOption(option)
}

const initZoneTempChart = () => {
  const chart = echarts.init(zoneTempChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['1号楼', '2号楼', '3号楼', '4号楼', '5号楼'] },
    yAxis: { type: 'value', name: '温度(°C)' },
    series: [
      { name: '供水', type: 'bar', data: [50, 51, 49, 50, 52] },
      { name: '回水', type: 'bar', data: [40, 41, 39, 40, 42] }
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
