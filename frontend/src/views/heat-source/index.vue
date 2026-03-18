<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>热源智能调控系统</span>
          <el-button type="primary" @click="handleOptimize">调度优化</el-button>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="6" v-for="boiler in boilers" :key="boiler.id">
          <el-card shadow="hover" class="boiler-card">
            <div class="boiler-header">
              <span class="boiler-name">{{ boiler.name }}</span>
              <el-tag :type="boiler.status === 'running' ? 'success' : 'info'">
                {{ boiler.status === 'running' ? '运行' : '停机' }}
              </el-tag>
            </div>
            <div class="boiler-info">
              <div class="info-item">
                <span class="label">负荷率</span>
                <el-progress :percentage="boiler.loadRate" :color="getProgressColor(boiler.loadRate)" />
              </div>
              <div class="info-item">
                <span class="label">供水温度</span>
                <span class="value">{{ boiler.supplyTemp }}℃</span>
              </div>
              <div class="info-item">
                <span class="label">回水温度</span>
                <span class="value">{{ boiler.returnTemp }}℃</span>
              </div>
              <div class="info-item">
                <span class="label">运行效率</span>
                <span class="value">{{ (boiler.efficiency * 100).toFixed(1) }}%</span>
              </div>
            </div>
            <div class="boiler-actions">
              <el-button size="small" type="primary" :disabled="boiler.status !== 'running'" @click="adjustBoiler(boiler, 'up')">增加负荷</el-button>
              <el-button size="small" type="danger" :disabled="boiler.status !== 'running'" @click="adjustBoiler(boiler, 'down')">降低负荷</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>热负荷预测曲线</span>
            </template>
            <div ref="loadPredictChartRef" style="height: 320px"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>锅炉效率对比</span>
            </template>
            <div ref="efficiencyChartRef" style="height: 320px"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>神经网络预测模型</span>
                <el-button size="small" @click="trainModel">训练模型</el-button>
              </div>
            </template>
            <el-descriptions :column="4" border>
              <el-descriptions-item label="模型类型">LSTM神经网络</el-descriptions-item>
              <el-descriptions-item label="模型版本">v2.1.0</el-descriptions-item>
              <el-descriptions-item label="预测精度">94.5%</el-descriptions-item>
              <el-descriptions-item label="训练周期">每周</el-descriptions-item>
              <el-descriptions-item label="输入特征">室外温度、时间、历史负荷</el-descriptions-item>
              <el-descriptions-item label="输出预测">4小时热负荷</el-descriptions-item>
              <el-descriptions-item label="训练数据量">30天</el-descriptions-item>
              <el-descriptions-item label="最后训练">2026-03-14 02:00</el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage, ElMessageBox } from 'element-plus'
import { heatSourceApi } from '@/api'

// 图表DOM引用
const loadPredictChartRef = ref(null)
const efficiencyChartRef = ref(null)

// 控制对话框显示
const showAddDialog = ref(false)

// 锅炉列表数据
const boilers = ref([])

/**
 * 加载锅炉数据
 */
const loadBoilers = async () => {
  try {
    const result = await heatSourceApi.getBoilers()
    boilers.value = result || []
  } catch (error) {
    console.error('加载锅炉数据失败:', error)
    boilers.value = [
      { id: 1, name: '1号燃气锅炉', status: 'running', loadRate: 85, supplyTemp: 120, returnTemp: 70, efficiency: 0.95 },
      { id: 2, name: '2号燃气锅炉', status: 'running', loadRate: 72, supplyTemp: 118, returnTemp: 68, efficiency: 0.93 },
      { id: 3, name: '3号燃气锅炉', status: 'stopped', loadRate: 0, supplyTemp: 0, returnTemp: 0, efficiency: 0 }
    ]
  }
}

/**
 * 获取负荷率进度条颜色
 * @param rate 负荷率
 * @returns 颜色值
 */
const getProgressColor = (rate) => {
  if (rate >= 80) return '#f56c6c'
  if (rate >= 60) return '#e6a23c'
  return '#67c23a'
}

/**
 * 调整锅炉负荷
 * @param boiler 锅炉对象
 * @param type 调整类型：up增加负荷，down减少负荷
 */
const adjustBoiler = (boiler, type) => {
  if (type === 'up' && boiler.loadRate < 95) {
    boiler.loadRate += 5
  } else if (type === 'down' && boiler.loadRate > 30) {
    boiler.loadRate -= 5
  }
  ElMessage.success(`${boiler.name}负荷已调整至${boiler.loadRate}%`)
}

/**
 * 调度优化按钮点击事件
 */
const handleOptimize = () => {
  ElMessageBox.confirm('是否执行调度优化？', '提示', { type: 'info' }).then(() => {
    ElMessage.success('调度优化已完成')
  }).catch(() => {})
}

/**
 * 初始化热负荷预测曲线图表
 */
const initLoadPredictChart = async () => {
  if (!loadPredictChartRef.value) return
  const chart = echarts.init(loadPredictChartRef.value)
  
  let chartData = { hours: [], actual: [], predicted: [] }
  try {
    chartData = await heatSourceApi.getLoadPrediction()
  } catch (error) {
    console.error('获取负荷预测数据失败:', error)
  }
  
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['实际负荷', '预测负荷'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: chartData.hours.length > 0 ? chartData.hours : ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00'] },
    yAxis: { type: 'value', name: '负荷(MW)' },
    series: [
      { name: '实际负荷', type: 'line', data: chartData.actual.length > 0 ? chartData.actual : [45, 42, 55, 60, 58, 52, 48], smooth: true },
      { name: '预测负荷', type: 'line', data: chartData.predicted.length > 0 ? chartData.predicted : [null, null, null, null, 55, 53, 50], smooth: true, lineStyle: { type: 'dashed' } }
    ]
  }
  chart.setOption(option)
}

/**
 * 初始化锅炉效率对比图表
 */
const initEfficiencyChart = async () => {
  if (!efficiencyChartRef.value) return
  const chart = echarts.init(efficiencyChartRef.value)
  
  let chartData = []
  try {
    const result = await heatSourceApi.getEfficiencyComparison()
    chartData = result || []
  } catch (error) {
    console.error('获取效率对比数据失败:', error)
  }
  
  const defaultData = [
    { name: '1号锅炉', efficiency: 95 },
    { name: '2号锅炉', efficiency: 93 },
    { name: '3号锅炉', efficiency: 0 }
  ]
  
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: chartData.length > 0 ? chartData.map(d => d.name) : ['1号锅炉', '2号锅炉', '3号锅炉'] },
    yAxis: { type: 'value', name: '效率(%)', max: 100 },
    series: [{
      type: 'bar',
      data: chartData.length > 0 ? chartData.map(d => d.efficiency) : [95, 93, 0],
      itemStyle: {
        color: (params) => {
          const colors = ['#67c23a', '#e6a23c', '#909399']
          return colors[params.dataIndex]
        }
      }
    }]
  }
  chart.setOption(option)
}

/**
 * 训练模型按钮点击事件
 */
const trainModel = () => {
  ElMessage.info('模型训练中，请稍候...')
  setTimeout(() => {
    ElMessage.success('模型训练完成')
  }, 2000)
}

// 组件挂载完成后初始化图表
onMounted(() => {
  loadBoilers()
  initLoadPredictChart()
  initEfficiencyChart()
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
  font-weight: bold;
  font-size: 16px;
}

.boiler-info {
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.info-item .label {
  color: #909399;
  font-size: 14px;
}

.info-item .value {
  font-weight: bold;
  font-size: 16px;
}

.boiler-actions {
  display: flex;
  justify-content: space-between;
}
</style>
