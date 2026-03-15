<template>
  <div class="trend-analysis">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <el-radio-group v-model="trendType" size="small">
                <el-radio-button label="temperature">温度趋势</el-radio-button>
                <el-radio-button label="energy">能耗趋势</el-radio-button>
                <el-radio-button label="load">负荷趋势</el-radio-button>
                <el-radio-button label="equipment">设备趋势</el-radio-button>
              </el-radio-group>
              <el-radio-group v-model="timeRange" size="small" style="margin-left: 20px">
                <el-radio-button label="day">日</el-radio-button>
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="year">年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" style="height: 400px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px" v-if="trendType === 'energy'">
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>能耗统计</span>
            </div>
          </template>
          <div class="energy-stats">
            <div class="energy-item">
              <span class="label">今日耗气量</span>
              <span class="value">12,500 m³</span>
            </div>
            <div class="energy-item">
              <span class="label">今日耗电量</span>
              <span class="value">2,850 kWh</span>
            </div>
            <div class="energy-item">
              <span class="label">综合能耗</span>
              <span class="value">156.8 tce</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>能效指标</span>
            </div>
          </template>
          <div class="efficiency-stats">
            <div class="efficiency-item">
              <el-progress type="circle" :percentage="85" :width="100" />
              <span class="label">系统能效比</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>能耗排名</span>
            </div>
          </template>
          <el-table :data="energyRankData" size="small">
            <el-table-column prop="rank" label="排名" width="60" />
            <el-table-column prop="station" label="换热站" />
            <el-table-column prop="consumption" label="能耗" width="80" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>趋势对比分析</span>
            </div>
          </template>
          <div ref="compareChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>异常告警统计</span>
            </div>
          </template>
          <el-table :data="anomalyData" style="width: 100%">
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="count" label="次数" width="80" />
            <el-table-column prop="trend" label="趋势" width="80">
              <template #default="{ row }">
                <el-icon :style="{ color: row.trend === '上升' ? '#f56c6c' : '#67c23a' }">
                  <ArrowUp v-if="row.trend === '上升'" />
                  <ArrowDown v-else />
                </el-icon>
                {{ row.trend }}
              </template>
            </el-table-column>
            <el-table-column prop="suggestion" label="建议" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>趋势分析报告</span>
              <el-button type="primary" size="small">导出报告</el-button>
            </div>
          </template>
          <el-table :data="reportData" style="width: 100%">
            <el-table-column prop="indicator" label="指标" width="150" />
            <el-table-column prop="currentValue" label="当前值" width="120" />
            <el-table-column prop="lastPeriod" label="上期值" width="120" />
            <el-table-column prop="changeRate" label="变化率" width="100">
              <template #default="{ row }">
                <span :style="{ color: row.changeRate > 0 ? '#f56c6c' : '#67c23a' }">
                  {{ row.changeRate > 0 ? '+' : '' }}{{ row.changeRate }}%
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="analysis" label="分析说明" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const trendChartRef = ref(null)
const compareChartRef = ref(null)

const trendType = ref('temperature')
const timeRange = ref('week')

const energyRankData = ref([
  { rank: 1, station: '换热站1', consumption: '156.8 tce' },
  { rank: 2, station: '换热站2', consumption: '142.3 tce' },
  { rank: 3, station: '换热站3', consumption: '128.5 tce' }
])

const anomalyData = ref([
  { type: '温度异常', count: 12, trend: '下降', suggestion: '继续观察' },
  { type: '压力异常', count: 3, trend: '上升', suggestion: '检查管网' },
  { type: '能耗异常', count: 5, trend: '下降', suggestion: '优化运行' }
])

const reportData = ref([
  { indicator: '平均室温', currentValue: '20.5°C', lastPeriod: '19.8°C', changeRate: 3.5, analysis: '室温达标率提升，供热质量改善' },
  { indicator: '单位面积能耗', currentValue: '0.45 kWh/m²', lastPeriod: '0.52 kWh/m²', changeRate: -13.5, analysis: '能效显著提升，节能效果明显' },
  { indicator: '系统能效比', currentValue: '3.2', lastPeriod: '2.9', changeRate: 10.3, analysis: '能效比提高，锅炉运行优化' }
])

const initTrendChart = () => {
  const chart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['一次侧供水', '二次侧供水', '室外温度'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: { type: 'value', name: '温度(°C)' },
    series: [
      { name: '一次侧供水', type: 'line', smooth: true, data: [72, 75, 78, 76, 74, 73, 75] },
      { name: '二次侧供水', type: 'line', smooth: true, data: [48, 50, 52, 51, 49, 48, 50] },
      { name: '室外温度', type: 'line', smooth: true, data: [-5, -3, -8, -6, -4, -2, -7] }
    ]
  }
  chart.setOption(option)
}

const initCompareChart = () => {
  const chart = echarts.init(compareChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['本期', '上期'] },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: { type: 'value', name: '数值' },
    series: [
      { name: '本期', type: 'bar', data: [120, 132, 101, 134, 90, 230, 210] },
      { name: '上期', type: 'bar', data: [110, 122, 111, 124, 100, 210, 190] }
    ]
  }
  chart.setOption(option)
}

onMounted(() => {
  initTrendChart()
  initCompareChart()
})
</script>

<style scoped>
.trend-analysis {
  padding: 0;
}

.chart-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
}

.energy-stats {
  padding: 10px 0;
}

.energy-item {
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.energy-item .label {
  color: #606266;
}

.energy-item .value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.efficiency-stats {
  padding: 20px 0;
  text-align: center;
}

.efficiency-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.efficiency-item .label {
  color: #606266;
}
</style>
