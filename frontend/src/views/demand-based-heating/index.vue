<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>热用户按需供热管理</span>
          <el-button type="primary" @click="handleRefresh">刷新数据</el-button>
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
              <span>实时温度监控</span>
            </template>
            <el-table :data="temperatureData" style="width: 100%">
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
              <el-table-column prop="targetTemp" label="目标温度(℃)" width="120" />
              <el-table-column prop="collectTime" label="采集时间" width="160" />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button size="small" @click="viewPrediction(row)">预测</el-button>
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
                <el-select v-model="predictHours" style="width: 120px" @change="loadPrediction">
                  <el-option :value="2" label="预测2小时" />
                  <el-option :value="4" label="预测4小时" />
                  <el-option :value="6" label="预测6小时" />
                </el-select>
              </div>
            </template>
            <div ref="predictChartRef" style="height: 350px"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <span>供热需求统计</span>
            </template>
            <div ref="demandChartRef" style="height: 300px"></div>
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
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { heatUserApi, buildingApi } from '@/api'

const tempDistChartRef = ref(null)
const predictChartRef = ref(null)
const demandChartRef = ref(null)

const predictHours = ref(2)
const predictionDialogVisible = ref(false)
const predictionData = ref([])

const temperatureData = ref([
  { userCode: 'U001', userName: '张三', buildingName: '阳光花园1号楼', temperature: 19.5, targetTemp: 20.0, collectTime: '2026-03-14 10:00:00' },
  { userCode: 'U002', userName: '李四', buildingName: '阳光花园1号楼', temperature: 20.5, targetTemp: 21.0, collectTime: '2026-03-14 10:00:00' },
  { userCode: 'U003', userName: '王五', buildingName: '阳光花园1号楼', temperature: 19.0, targetTemp: 20.0, collectTime: '2026-03-14 10:00:00' },
  { userCode: 'U004', userName: '赵六', buildingName: '阳光花园2号楼', temperature: 21.5, targetTemp: 22.0, collectTime: '2026-03-14 10:00:00' },
  { userCode: 'U005', userName: '钱七', buildingName: '商业大厦A座', temperature: 20.0, targetTemp: 20.0, collectTime: '2026-03-14 10:00:00' },
  { userCode: 'U006', userName: '孙八', buildingName: '商业大厦A座', temperature: 20.2, targetTemp: 20.0, collectTime: '2026-03-14 10:00:00' }
])

const getTempColor = (current, target) => {
  const diff = Math.abs(current - target)
  if (diff <= 1) return '#67c23a'
  if (diff <= 2) return '#e6a23c'
  return '#f56c6c'
}

const initTempDistChart = () => {
  const chart = echarts.init(tempDistChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['30%', '70%'],
      data: [
        { value: 4, name: '正常(±1℃)', itemStyle: { color: '#67c23a' } },
        { value: 1, name: '偏低(>1℃)', itemStyle: { color: '#409eff' } },
        { value: 1, name: '偏高(>1℃)', itemStyle: { color: '#f56c6c' } }
      ]
    }]
  }
  chart.setOption(option)
}

const initPredictChart = () => {
  const chart = echarts.init(predictChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['实际温度', '预测温度', '置信区间'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00']
    },
    yAxis: { type: 'value', name: '温度(℃)' },
    series: [
      { name: '实际温度', type: 'line', data: [19.5, 19.8, 20.0, 20.2, null, null, null], smooth: true },
      { name: '预测温度', type: 'line', data: [null, null, null, 20.2, 20.3, 20.5, 20.6], smooth: true, lineStyle: { type: 'dashed' } },
      { name: '置信区间', type: 'line', areaStyle: { opacity: 0.2 }, data: [null, null, null, 20.2, 19.8, 20.0, 20.1], smooth: true }
    ]
  }
  chart.setOption(option)
}

const initDemandChart = () => {
  const chart = echarts.init(demandChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['1号楼', '2号楼', '3号楼', '4号楼']
    },
    yAxis: { type: 'value', name: '需求(kW)' },
    series: [{
      type: 'bar',
      data: [1200, 1500, 2000, 2500],
      itemStyle: { color: '#409eff' }
    }]
  }
  chart.setOption(option)
}

const handleRefresh = () => {
  ElMessage.success('数据已刷新')
}

const viewPrediction = (row) => {
  predictionData.value = [
    { predictTime: '2026-03-14 11:00', predictedTemp: 19.8, confidenceLower: 19.3, confidenceUpper: 20.3, confidenceLevel: '85%' },
    { predictTime: '2026-03-14 12:00', predictedTemp: 20.0, confidenceLower: 19.4, confidenceUpper: 20.6, confidenceLevel: '82%' }
  ]
  predictionDialogVisible.value = true
}

const loadPrediction = () => {
  initPredictChart()
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
