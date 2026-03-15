<template>
  <div class="meteorology">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><Temperature /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ currentWeather.outdoorTemp }}°C</div>
              <div class="stat-label">当前室外温度</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><Sunny /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ currentWeather.weatherType }}</div>
              <div class="stat-label">天气状况</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><WindPower /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ currentWeather.windSpeed }}m/s</div>
              <div class="stat-label">风速</div>
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
              <div class="stat-value">{{ currentWeather.humidity }}%</div>
              <div class="stat-label">相对湿度</div>
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
              <span>温度趋势曲线</span>
              <el-radio-group v-model="tempTimeRange" size="small" @change="loadTempData">
                <el-radio-button label="day">日</el-radio-button>
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="tempChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>72小时预报</span>
              <el-button type="primary" size="small" @click="refreshForecast">刷新</el-button>
            </div>
          </template>
          <el-table :data="forecastData" style="width: 100%">
            <el-table-column prop="time" label="时间" width="80" />
            <el-table-column prop="temp" label="温度(°C)" width="80">
              <template #default="{ row }">
                <span :style="{ color: row.temp < 0 ? '#409eff' : row.temp > 30 ? '#f56c6c' : '#303133' }">
                  {{ row.temp }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="weather" label="天气" />
            <el-table-column prop="wind" label="风力" width="80" />
            <el-table-column prop="humidity" label="湿度" width="60" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>气象与供热负荷关联分析</span>
              <el-select v-model="correlationType" size="small" style="width: 120px">
                <el-option label="温度-负荷" value="temp" />
                <el-option label="湿度-负荷" value="humidity" />
                <el-option label="风速-负荷" value="wind" />
              </el-select>
            </div>
          </template>
          <div ref="correlationChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>历史气象数据</span>
              <div>
                <el-date-picker v-model="historyDateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" size="small" style="width: 240px" />
                <el-button type="primary" size="small" style="margin-left: 10px" @click="exportData">导出数据</el-button>
              </div>
            </div>
          </template>
          <el-table :data="historyData" style="width: 100%">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="avgTemp" label="平均温度(°C)" width="100" />
            <el-table-column prop="maxTemp" label="最高温度(°C)" width="100" />
            <el-table-column prop="minTemp" label="最低温度(°C)" width="100" />
            <el-table-column prop="weather" label="天气" />
            <el-table-column prop="heatingDegree" label="供暖度日" width="100" />
          </el-table>
          <el-pagination style="margin-top: 10px; text-align: right" :current-page="historyPage" :page-size="pageSize" :total="historyTotal" layout="total, prev, pager, next" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>气象预警信息</span>
              <div>
                <el-tag type="warning" v-if="coldWaveWarning">寒潮预警</el-tag>
                <el-button type="primary" size="small" @click="refreshWarnings">刷新</el-button>
              </div>
            </div>
          </template>
          <el-alert v-if="coldWaveWarning" title="寒潮橙色预警" type="warning" :closable="false" style="margin-bottom: 15px">
            预计未来24小时内气温将下降8°C以上，请做好供热保障措施。
          </el-alert>
          <el-table :data="warnings" style="width: 100%">
            <el-table-column prop="type" label="预警类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getWarningType(row.type)">{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="level" label="预警级别" width="100">
              <template #default="{ row }">
                <el-tag :type="row.level === '橙色' ? 'warning' : 'danger'">{{ row.level }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="预警内容" />
            <el-table-column prop="publishTime" label="发布时间" width="180" />
            <el-table-column prop="effect" label="对供热影响">
              <template #default="{ row }">
                <el-tag v-if="row.effect === 'high'" type="danger">大幅增加</el-tag>
                <el-tag v-else-if="row.effect === 'medium'" type="warning">中等增加</el-tag>
                <el-tag v-else type="info">影响较小</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default>
                <el-button type="primary" size="small">响应</el-button>
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
            <span>气象数据接入配置</span>
          </template>
          <el-form :inline="true" label-width="120px">
            <el-form-item label="数据源">
              <el-select v-model="weatherConfig.source" style="width: 150px">
                <el-option label="气象局API" value="weather bureau" />
                <el-option label="第三方服务" value="third party" />
                <el-option label="本地监测站" value="local" />
              </el-select>
            </el-form-item>
            <el-form-item label="更新频率">
              <el-select v-model="weatherConfig.updateFreq" style="width: 150px">
                <el-option label="每10分钟" value="10" />
                <el-option label="每30分钟" value="30" />
                <el-option label="每小时" value="60" />
              </el-select>
            </el-form-item>
            <el-form-item label="预报时长">
              <el-select v-model="weatherConfig.forecastHours" style="width: 150px">
                <el-option label="24小时" value="24" />
                <el-option label="48小时" value="48" />
                <el-option label="72小时" value="72" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveWeatherConfig">保存配置</el-button>
              <el-button @click="testConnection">测试连接</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Temperature, Sunny, WindPower, Warning } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const tempChartRef = ref(null)
const correlationChartRef = ref(null)
const tempTimeRange = ref('day')
const correlationType = ref('temp')
const historyDateRange = ref([])
const historyPage = ref(1)
const pageSize = ref(5)
const historyTotal = ref(15)
const coldWaveWarning = ref(true)

const currentWeather = ref({
  outdoorTemp: -5,
  weatherType: '晴',
  windSpeed: 3.5,
  humidity: 45
})

const weatherConfig = ref({
  source: 'weather bureau',
  updateFreq: '30',
  forecastHours: '72'
})

const forecastData = ref([
  { time: '08:00', temp: -6, weather: '晴', wind: '3-4级', humidity: '50%' },
  { time: '14:00', temp: -2, weather: '晴', wind: '2-3级', humidity: '45%' },
  { time: '20:00', temp: -8, weather: '多云', wind: '3-4级', humidity: '55%' },
  { time: '02:00', temp: -10, weather: '阴', wind: '4-5级', humidity: '60%' },
  { time: '08:00', temp: -12, weather: '小雪', wind: '4-5级', humidity: '70%' }
])

const historyData = ref([
  { date: '2026-03-15', avgTemp: -3.5, maxTemp: 2, minTemp: -8, weather: '晴', heatingDegree: 23.5 },
  { date: '2026-03-14', avgTemp: -2.0, maxTemp: 4, minTemp: -7, weather: '多云', heatingDegree: 22.0 },
  { date: '2026-03-13', avgTemp: 0.5, maxTemp: 5, minTemp: -4, weather: '阴', heatingDegree: 19.5 },
  { date: '2026-03-12', avgTemp: 1.2, maxTemp: 6, minTemp: -3, weather: '晴', heatingDegree: 18.8 },
  { date: '2026-03-11', avgTemp: -1.5, maxTemp: 3, minTemp: -6, weather: '多云', heatingDegree: 21.5 }
])

const warnings = ref([
  { type: '寒潮', level: '橙色', content: '预计未来24小时气温下降8-10°C', publishTime: '2026-03-15 06:00:00', effect: 'high' },
  { type: '大风', level: '黄色', content: '预计未来12小时有4-5级大风', publishTime: '2026-03-15 08:00:00', effect: 'medium' },
  { type: '低温', level: '蓝色', content: '最低温度将降至-10°C以下', publishTime: '2026-03-14 18:00:00', effect: 'high' }
])

const getWarningType = (type) => {
  const map = { '寒潮': 'warning', '大风': '', '低温': 'info' }
  return map[type] || 'info'
}

const loadTempData = () => {
  initTempChart()
}

const refreshForecast = () => {
  ElMessage.success('预报数据已刷新')
}

const exportData = () => {
  ElMessage.info('正在导出气象数据...')
}

const refreshWarnings = () => {
  ElMessage.success('预警信息已刷新')
}

const saveWeatherConfig = () => {
  ElMessage.success('气象配置已保存')
}

const testConnection = () => {
  ElMessage.info('正在测试气象数据连接...')
  setTimeout(() => {
    ElMessage.success('连接测试成功')
  }, 1000)
}

const initTempChart = () => {
  const chart = echarts.init(tempChartRef.value)
  let xAxisData = []
  let tempData = []
  let heatingDegreeData = []
  
  if (tempTimeRange.value === 'day') {
    xAxisData = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
    tempData = [-8, -10, -6, -2, -4, -7]
    heatingDegreeData = [28, 30, 26, 22, 24, 27]
  } else if (tempTimeRange.value === 'week') {
    xAxisData = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    tempData = [-5, -3, -8, -6, -4, -2, -7]
    heatingDegreeData = [25, 23, 28, 26, 24, 22, 27]
  } else {
    xAxisData = ['第1周', '第2周', '第3周', '第4周']
    tempData = [-8, -5, -2, 1]
    heatingDegreeData = [28, 25, 22, 19]
  }
  
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['室外温度', '供暖度日'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xAxisData
    },
    yAxis: [
      { type: 'value', name: '温度(°C)' },
      { type: 'value', name: '供暖度日' }
    ],
    series: [
      {
        name: '室外温度',
        type: 'line',
        smooth: true,
        data: tempData,
        itemStyle: { color: '#409eff' },
        areaStyle: { opacity: 0.1 }
      },
      {
        name: '供暖度日',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: heatingDegreeData,
        itemStyle: { color: '#e6a23c' }
      }
    ]
  }
  chart.setOption(option)
}

const initCorrelationChart = () => {
  const chart = echarts.init(correlationChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['室外温度', '供热负荷'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: { type: 'value', name: '数值' },
    series: [
      {
        name: '室外温度',
        type: 'line',
        smooth: true,
        data: [-5, -3, -8, -6, -4, -2, -7],
        itemStyle: { color: '#409eff' }
      },
      {
        name: '供热负荷',
        type: 'line',
        smooth: true,
        data: [52, 48, 58, 55, 50, 45, 56],
        itemStyle: { color: '#f56c6c' }
      }
    ]
  }
  chart.setOption(option)
}

onMounted(() => {
  initTempChart()
  initCorrelationChart()
})
</script>

<style scoped>
.meteorology {
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
