<template>
  <div class="load-forecast">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ currentLoad }}MW</div>
              <div class="stat-label">当前负荷</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ maxLoad }}MW</div>
              <div class="stat-label">今日最大负荷</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ avgLoad }}MW</div>
              <div class="stat-label">平均负荷</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="32"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ accuracy }}%</div>
              <div class="stat-label">预测准确率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>负荷预测曲线</span>
              <el-radio-group v-model="forecastType" size="small">
                <el-radio-button label="ultra-short">超短期(2h)</el-radio-button>
                <el-radio-button label="short">短期(24h)</el-radio-button>
                <el-radio-button label="medium">中期(7d)</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="forecastChartRef" style="height: 400px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>预测误差分析</span>
            </div>
          </template>
          <div ref="errorChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>各换热站负荷分布</span>
            </div>
          </template>
          <div ref="stationLoadChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>负荷预测详情</span>
              <el-select v-model="selectedStation" placeholder="选择换热站" size="small">
                <el-option label="全部" value="" />
                <el-option v-for="item in stations" :key="item" :label="item" :value="item" />
              </el-select>
            </div>
          </template>
          <el-table :data="forecastDetails" style="width: 100%">
            <el-table-column prop="time" label="时间" width="180" />
            <el-table-column prop="predictedLoad" label="预测负荷(MW)" width="120" />
            <el-table-column prop="confidenceLower" label="置信下限(MW)" width="120" />
            <el-table-column prop="confidenceUpper" label="置信上限(MW)" width="120" />
            <el-table-column prop="actualLoad" label="实际负荷(MW)" width="120" />
            <el-table-column prop="error" label="误差(%)" width="100">
              <template #default="{ row }">
                <span :style="{ color: Math.abs(row.error) < 5 ? '#67c23a' : '#f56c6c' }">
                  {{ row.error }}%
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="weather" label="气象条件" />
          </el-table>
          <el-pagination
            style="margin-top: 20px; text-align: right"
            :current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="total, prev, pager, next"
          />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { TrendCharts, DataLine, Timer, Warning } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const forecastChartRef = ref(null)
const errorChartRef = ref(null)
const stationLoadChartRef = ref(null)

const forecastType = ref('short')
const selectedStation = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(20)

const currentLoad = ref(48.5)
const maxLoad = ref(62.3)
const avgLoad = ref(45.2)
const accuracy = ref(94.5)

const stations = ref(['换热站1', '换热站2', '换热站3'])

const forecastDetails = ref([
  { time: '2026-03-15 08:00', predictedLoad: 52.3, confidenceLower: 50.1, confidenceUpper: 54.5, actualLoad: 51.8, error: 0.97, weather: '晴' },
  { time: '2026-03-15 09:00', predictedLoad: 55.6, confidenceLower: 53.2, confidenceUpper: 58.0, actualLoad: 54.9, error: 1.28, weather: '晴' },
  { time: '2026-03-15 10:00', predictedLoad: 58.2, confidenceLower: 55.8, confidenceUpper: 60.6, actualLoad: 57.5, error: 1.22, weather: '晴' }
])

const initForecastChart = () => {
  const chart = echarts.init(forecastChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['实际负荷', '预测负荷', '置信区间'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00']
    },
    yAxis: { type: 'value', name: '负荷(MW)' },
    series: [
      {
        name: '实际负荷',
        type: 'line',
        smooth: true,
        data: [42, 38, 52, 58, 55, 48, 44]
      },
      {
        name: '预测负荷',
        type: 'line',
        smooth: true,
        lineStyle: { type: 'dashed' },
        data: [null, null, 53, 59, 56, 49, 45]
      },
      {
        name: '置信区间',
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.2 },
        data: [null, null, [50, 56], [56, 62], [53, 59], [46, 52], [42, 48]]
      }
    ]
  }
  chart.setOption(option)
}

const initErrorChart = () => {
  const chart = echarts.init(errorChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
    },
    yAxis: { type: 'value', name: '误差(%)' },
    series: [{
      type: 'bar',
      data: [-2.5, 3.2, 1.8, -1.2, 2.5, -0.8],
      itemStyle: {
        color: (params) => params.data >= 0 ? '#f56c6c' : '#67c23a'
      }
    }]
  }
  chart.setOption(option)
}

const initStationLoadChart = () => {
  const chart = echarts.init(stationLoadChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 18.5, name: '换热站1' },
        { value: 15.2, name: '换热站2' },
        { value: 14.8, name: '换热站3' }
      ]
    }]
  }
  chart.setOption(option)
}

onMounted(() => {
  initForecastChart()
  initErrorChart()
  initStationLoadChart()
})
</script>

<style scoped>
.load-forecast {
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
