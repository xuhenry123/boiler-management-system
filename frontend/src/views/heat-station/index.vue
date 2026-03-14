<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>换热站自动控制系统</span>
          <el-select v-model="selectedStation" style="width: 200px">
            <el-option v-for="s in stations" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card shadow="hover">
            <template #header>
              <span>实时工艺图</span>
            </template>
            <div ref="processChartRef" style="height: 400px"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>运行参数</span>
            </template>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="一次侧供水温度">{{ stationData.primarySupplyTemp }}℃</el-descriptions-item>
              <el-descriptions-item label="一次侧回水温度">{{ stationData.primaryReturnTemp }}℃</el-descriptions-item>
              <el-descriptions-item label="二次侧供水温度">{{ stationData.secondarySupplyTemp }}℃</el-descriptions-item>
              <el-descriptions-item label="二次侧回水温度">{{ stationData.secondaryReturnTemp }}℃</el-descriptions-item>
              <el-descriptions-item label="一次侧流量">{{ stationData.primaryFlow }} t/h</el-descriptions-item>
              <el-descriptions-item label="二次侧流量">{{ stationData.secondaryFlow }} t/h</el-descriptions-item>
              <el-descriptions-item label="循环泵转速">{{ stationData.pumpSpeed * 100 }}%</el-descriptions-item>
              <el-descriptions-item label="运行状态">
                <el-tag type="success">运行中</el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>PID控制参数</span>
                <el-button size="small" type="primary" @click="showPIDDialog = true">配置</el-button>
              </div>
            </template>
            <el-form label-width="100px">
              <el-form-item label="设定温度">
                <el-input-number v-model="pidConfig.setpoint" :min="30" :max="80" />
              </el-form-item>
              <el-form-item label="Kp">
                <el-input-number v-model="pidConfig.kp" :step="0.1" />
              </el-form-item>
              <el-form-item label="Ki">
                <el-input-number v-model="pidConfig.ki" :step="0.01" />
              </el-form-item>
              <el-form-item label="Kd">
                <el-input-number v-model="pidConfig.kd" :step="0.01" />
              </el-form-item>
              <el-form-item label="控制模式">
                <el-radio-group v-model="pidConfig.mode">
                  <el-radio label="auto">自动</el-radio>
                  <el-radio label="manual">手动</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>PID调节曲线</span>
            </template>
            <div ref="pidChartRef" style="height: 280px"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <span>历史曲线</span>
            </template>
            <div ref="historyChartRef" style="height: 300px"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'

const processChartRef = ref(null)
const pidChartRef = ref(null)
const historyChartRef = ref(null)

const selectedStation = ref(1)
const showPIDDialog = ref(false)

const stations = ref([
  { id: 1, name: '东城区换热站' },
  { id: 2, name: '西城区换热站' },
  { id: 3, name: '朝阳区换热站' }
])

const stationData = reactive({
  primarySupplyTemp: 118.5,
  primaryReturnTemp: 68.2,
  secondarySupplyTemp: 50.3,
  secondaryReturnTemp: 40.1,
  primaryFlow: 520,
  secondaryFlow: 480,
  pumpSpeed: 0.75
})

const pidConfig = reactive({
  setpoint: 50,
  kp: 1.2,
  ki: 0.3,
  kd: 0.1,
  mode: 'auto'
})

const initProcessChart = () => {
  const chart = echarts.init(processChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['一次侧温度', '二次侧温度', '设定值'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'] },
    yAxis: { type: 'value', name: '温度(℃)' },
    series: [
      { name: '一次侧温度', type: 'line', data: [118, 120, 122, 121, 119, 120], smooth: true },
      { name: '二次侧温度', type: 'line', data: [48, 50, 51, 50, 49, 50], smooth: true },
      { name: '设定值', type: 'line', data: [50, 50, 50, 50, 50, 50], lineStyle: { type: 'dashed' } }
    ]
  }
  chart.setOption(option)
}

const initPIDChart = () => {
  const chart = echarts.init(pidChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['测量值', '输出值'] },
    xAxis: { type: 'category', data: Array.from({ length: 20 }, (_, i) => `${i}s`) },
    yAxis: { type: 'value' },
    series: [
      { name: '测量值', type: 'line', data: [45, 46, 47, 48, 49, 49.5, 49.8, 50, 50.1, 50, 49.9, 50, 50, 50.1, 50, 49.9, 50, 50, 50, 50], smooth: true },
      { name: '输出值', type: 'line', data: [60, 58, 55, 52, 51, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50], smooth: true }
    ]
  }
  chart.setOption(option)
}

const initHistoryChart = () => {
  const chart = echarts.init(historyChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['供水温度', '回水温度', '室外温度'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
    yAxis: { type: 'value', name: '温度(℃)' },
    series: [
      { name: '供水温度', type: 'line', data: [50, 52, 48, 51, 50, 49, 50], smooth: true },
      { name: '回水温度', type: 'line', data: [40, 42, 38, 41, 40, 39, 40], smooth: true },
      { name: '室外温度', type: 'line', data: [-3, -2, -4, -1, -2, -3, -2], smooth: true }
    ]
  }
  chart.setOption(option)
}

onMounted(() => {
  initProcessChart()
  initPIDChart()
  initHistoryChart()
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
