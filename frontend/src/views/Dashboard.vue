<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
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
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%)">
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
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
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
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
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
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>温度趋势监控</span>
              <el-button type="primary" size="small" @click="refreshData">
                <el-icon><Refresh /></el-icon>
                刷新数据
              </el-button>
            </div>
          </template>
          <div ref="tempChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card" shadow="hover">
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
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>热负荷曲线</span>
            </div>
          </template>
          <div ref="loadChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近告警</span>
              <el-button type="text" @click="$router.push('/alarms')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="alarms" style="width: 100%" max-height="280">
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
/**
 * 仪表盘视图组件
 * 展示系统的整体运行状态，包括换热站数量、建筑物数量、热用户数量、告警数量等统计信息
 * 以及温度趋势图、换热站状态图、热负荷曲线等可视化图表
 */
import { ref, onMounted, reactive, nextTick } from 'vue'
import * as echarts from 'echarts'
import { OfficeBuilding, House, User, Warning, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 图表DOM引用
const tempChartRef = ref(null)
const stationChartRef = ref(null)
const loadChartRef = ref(null)

// ECharts图表实例
let tempChart = null
let stationChart = null
let loadChart = null

// 统计数据对象
const stats = reactive({
  stationCount: 0,
  buildingCount: 0,
  userCount: 0,
  alarmCount: 0
})

// 告警列表数据
const alarms = ref([])

/**
 * 获取告警级别的类型映射
 * @param level 告警级别
 * @return 对应的Element Plus标签类型
 */
const getAlarmLevelType = (level) => {
  const map = { Info: 'info', Warning: 'warning', Critical: 'danger' }
  return map[level] || 'info'
}

/**
 * 加载仪表盘数据
 * 从模拟API获取统计数据和告警信息
 */
const loadData = () => {
  // 从模拟API获取数据
  if (window.MockAPI) {
    const dashboardStats = window.MockAPI.getDashboardStats()
    stats.stationCount = dashboardStats.stationCount
    stats.buildingCount = dashboardStats.buildingCount
    stats.userCount = dashboardStats.userCount
    stats.alarmCount = dashboardStats.alarmCount
    
    alarms.value = window.MockAPI.getAlarms().slice(0, 8)
  }
}

/**
 * 初始化ECharts图表
 * 包括温度趋势图、换热站状态饼图、热负荷柱状图
 */
const initCharts = () => {
  if (!tempChartRef.value || !stationChartRef.value || !loadChartRef.value) return
  
  // 销毁已存在的图表实例
  if (tempChart) tempChart.dispose()
  if (stationChart) stationChart.dispose()
  if (loadChart) loadChart.dispose()
  
  // 温度趋势图
  tempChart = echarts.init(tempChartRef.value)
  let tempData = { primarySupply: [], primaryReturn: [], secondarySupply: [], secondaryReturn: [] }
  let hours = []
  
  if (window.MockAPI) {
    tempData = window.MockAPI.getTemperatureTrend()
    hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
  } else {
    hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
    tempData = {
      primarySupply: [118, 120, 122, 121, 119, 120],
      primaryReturn: [68, 70, 72, 71, 69, 70],
      secondarySupply: [48, 50, 51, 50, 49, 50],
      secondaryReturn: [38, 40, 41, 40, 39, 40]
    }
  }
  
  const tempOption = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['一次侧供水', '一次侧回水', '二次侧供水', '二次侧回水'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: hours
    },
    yAxis: { type: 'value', name: '温度(℃)' },
    series: [
      { name: '一次侧供水', type: 'line', smooth: true, data: tempData.primarySupply, itemStyle: { color: '#f56c6c' } },
      { name: '一次侧回水', type: 'line', smooth: true, data: tempData.primaryReturn, itemStyle: { color: '#e6a23c' } },
      { name: '二次侧供水', type: 'line', smooth: true, data: tempData.secondarySupply, itemStyle: { color: '#409eff' } },
      { name: '二次侧回水', type: 'line', smooth: true, data: tempData.secondaryReturn, itemStyle: { color: '#67c23a' } }
    ]
  }
  tempChart.setOption(tempOption)
  
  // 换热站状态饼图
  stationChart = echarts.init(stationChartRef.value)
  let stationData = []
  if (window.MockAPI) {
    stationData = window.MockAPI.getStationStatus()
  } else {
    stationData = [
      { value: 20, name: '运行中', itemStyle: { color: '#67c23a' } },
      { value: 3, name: '停止', itemStyle: { color: '#909399' } },
      { value: 1, name: '故障', itemStyle: { color: '#f56c6c' } }
    ]
  }
  
  const stationOption = {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '45%'],
      data: stationData
    }]
  }
  stationChart.setOption(stationOption)
  
  // 热负荷柱状图
  loadChart = echarts.init(loadChartRef.value)
  let loadData = []
  if (window.MockAPI) {
    loadData = window.MockAPI.getHeatLoad()
  } else {
    loadData = [45, 42, 55, 60, 58, 52]
  }
  
  const loadOption = {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
    },
    yAxis: { type: 'value', name: '负荷(MW)' },
    series: [{
      type: 'bar',
      data: loadData,
      itemStyle: { 
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#667eea' },
          { offset: 1, color: '#764ba2' }
        ])
      },
      label: { show: true, position: 'top' }
    }]
  }
  loadChart.setOption(loadOption)
}

/**
 * 刷新数据
 * 重新加载数据并刷新图表显示
 */
const refreshData = () => {
  loadData()
  initCharts()
  ElMessage.success('数据已刷新')
}

// 窗口大小变化时重新调整图表
window.addEventListener('resize', () => {
  tempChart && tempChart.resize()
  stationChart && stationChart.resize()
  loadChart && loadChart.resize()
})

// 组件挂载完成后初始化数据
onMounted(() => {
  nextTick(() => {
    loadData()
    initCharts()
  })
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  border-radius: 12px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.chart-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>
