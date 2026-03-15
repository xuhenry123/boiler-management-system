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
              <el-radio-group v-model="tempTimeRange" size="small">
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
            </div>
          </template>
          <el-table :data="forecastData" style="width: 100%">
            <el-table-column prop="time" label="时间" width="80" />
            <el-table-column prop="temp" label="温度(°C)" width="80" />
            <el-table-column prop="weather" label="天气" />
            <el-table-column prop="wind" label="风力" width="80" />
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
              <el-button type="primary" size="small">导出数据</el-button>
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
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>气象预警信息</span>
              <el-tag type="warning" v-if="coldWaveWarning">寒潮预警</el-tag>
            </div>
          </template>
          <el-alert
            v-if="coldWaveWarning"
            title="寒潮预警"
            type="warning"
            :closable="false"
            style="margin-bottom: 10px"
          >
            预计未来24小时内气温将下降8°C以上，请做好供热保障措施。
          </el-alert>
          <el-table :data="warnings" style="width: 100%">
            <el-table-column prop="type" label="预警类型" width="120" />
            <el-table-column prop="level" label="预警级别" width="100">
              <template #default="{ row }">
                <el-tag :type="row.level === '橙色' ? 'warning' : 'danger'">{{ row.level }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="预警内容" />
            <el-table-column prop="publishTime" label="发布时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Temperature, Sunny, WindPower, Warning } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const tempChartRef = ref(null)
const correlationChartRef = ref(null)
const tempTimeRange = ref('day')
const coldWaveWarning = ref(true)

const currentWeather = ref({
  outdoorTemp: -5,
  weatherType: '晴',
  windSpeed: 3.5,
  humidity: 45
})

const forecastData = ref([
  { time: '08:00', temp: -6, weather: '晴', wind: '3-4级' },
  { time: '14:00', temp: -2, weather: '晴', wind: '2-3级' },
  { time: '20:00', temp: -8, weather: '多云', wind: '3-4级' },
  { time: '02:00', temp: -10, weather: '阴', wind: '4-5级' }
])

const historyData = ref([
  { date: '2026-03-15', avgTemp: -3.5, maxTemp: 2, minTemp: -8, weather: '晴', heatingDegree: 23.5 },
  { date: '2026-03-14', avgTemp: -2.0, maxTemp: 4, minTemp: -7, weather: '多云', heatingDegree: 22.0 },
  { date: '2026-03-13', avgTemp: 0.5, maxTemp: 5, minTemp: -4, weather: '阴', heatingDegree: 19.5 }
])

const warnings = ref([
  { type: '寒潮', level: '橙色', content: '预计未来24小时气温下降8-10°C', publishTime: '2026-03-15 06:00:00' }
])

const initTempChart = () => {
  const chart = echarts.init(tempChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['室外温度', '供暖度日'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
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
        data: [-8, -10, -6, -2, -4, -7]
      },
      {
        name: '供暖度日',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: [28, 30, 26, 22, 24, 27]
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
        data: [-5, -3, -8, -6, -4, -2, -7]
      },
      {
        name: '供热负荷',
        type: 'line',
        smooth: true,
        data: [52, 48, 58, 55, 50, 45, 56]
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
