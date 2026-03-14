<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.stationCount }}</div>
              <div class="stat-label">换热站数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><House /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.buildingCount }}</div>
              <div class="stat-label">建筑物数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">热用户数量</div>
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
              <div class="stat-value">{{ stats.alarmCount }}</div>
              <div class="stat-label">告警数量</div>
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
              <span>温度趋势监控</span>
            </div>
          </template>
          <div ref="tempChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>换热站运行状态</span>
            </div>
          </template>
          <div ref="stationChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>热负荷曲线</span>
            </div>
          </template>
          <div ref="loadChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最近告警</span>
            </div>
          </template>
          <el-table :data="alarms" style="width: 100%">
            <el-table-column prop="alarmType" label="告警类型" width="100" />
            <el-table-column prop="alarmMessage" label="告警信息" />
            <el-table-column prop="alarmLevel" label="级别" width="80">
              <template #default="{ row }">
                <el-tag :type="getAlarmLevelType(row.alarmLevel)">{{ row.alarmLevel }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="160" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { OfficeBuilding, House, User, Warning } from '@element-plus/icons-vue'

const tempChartRef = ref(null)
const stationChartRef = ref(null)
const loadChartRef = ref(null)

const stats = reactive({
  stationCount: 3,
  buildingCount: 4,
  userCount: 8,
  alarmCount: 2
})

const alarms = ref([
  { alarmType: '温度异常', alarmMessage: '1号楼101室温度过低', alarmLevel: 'Warning', createTime: '2026-03-14 10:30:00' },
  { alarmType: '设备故障', alarmMessage: '2号阀门通讯中断', alarmLevel: 'Critical', createTime: '2026-03-14 10:25:00' }
])

const getAlarmLevelType = (level) => {
  const map = { Info: 'info', Warning: 'warning', Critical: 'danger' }
  return map[level] || 'info'
}

const initTempChart = () => {
  const chart = echarts.init(tempChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['一次侧供水', '一次侧回水', '二次侧供水', '二次侧回水'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
    },
    yAxis: { type: 'value', name: '温度(℃)' },
    series: [
      { name: '一次侧供水', type: 'line', smooth: true, data: [118, 120, 122, 121, 119, 120] },
      { name: '一次侧回水', type: 'line', smooth: true, data: [68, 70, 72, 71, 69, 70] },
      { name: '二次侧供水', type: 'line', smooth: true, data: [48, 50, 51, 50, 49, 50] },
      { name: '二次侧回水', type: 'line', smooth: true, data: [38, 40, 41, 40, 39, 40] }
    ]
  }
  chart.setOption(option)
}

const initStationChart = () => {
  const chart = echarts.init(stationChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 3, name: '运行中', itemStyle: { color: '#67c23a' } },
        { value: 0, name: '停止', itemStyle: { color: '#909399' } },
        { value: 0, name: '故障', itemStyle: { color: '#f56c6c' } }
      ]
    }]
  }
  chart.setOption(option)
}

const initLoadChart = () => {
  const chart = echarts.init(loadChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
    },
    yAxis: { type: 'value', name: '负荷(MW)' },
    series: [{
      type: 'bar',
      data: [45, 42, 55, 60, 58, 52],
      itemStyle: { color: '#409eff' }
    }]
  }
  chart.setOption(option)
}

onMounted(() => {
  initTempChart()
  initStationChart()
  initLoadChart()
})
</script>

<style scoped>
.dashboard {
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
  font-size: 28px;
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
