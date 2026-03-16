<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>能源成本分析</span>
          <el-radio-group v-model="timeGranularity" size="small">
            <el-radio-button value="day">日</el-radio-button>
            <el-radio-button value="month">月</el-radio-button>
            <el-radio-button value="year">年</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #409eff">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">总成本</div>
              <div class="stat-value">{{ formatNumber(totalCost) }} 万元</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">总能耗</div>
              <div class="stat-value">{{ formatNumber(totalConsumption) }} MWh</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon><Odometer /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">平均效率</div>
              <div class="stat-value">{{ averageEfficiency }}%</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon><Coin /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">碳排放</div>
              <div class="stat-value">{{ formatNumber(totalCarbon) }} t</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="16">
          <el-card shadow="hover">
            <template #header>
              <span>成本趋势</span>
            </template>
            <div ref="trendChartRef" style="height: 300px"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>能源类型分布</span>
            </template>
            <div ref="pieChartRef" style="height: 300px"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>多维度排名</span>
                <el-select v-model="rankType" placeholder="选择排名类型" size="small" style="width: 150px">
                  <el-option label="按效率排名" value="efficiency" />
                  <el-option label="按能耗排名" value="consumption" />
                  <el-option label="按成本排名" value="cost" />
                  <el-option label="按碳排放排名" value="carbon" />
                </el-select>
              </div>
            </template>
            <el-table :data="rankList" style="width: 100%">
              <el-table-column prop="rank" label="排名" width="80" />
              <el-table-column prop="stationName" label="站点名称" />
              <el-table-column prop="value" label="指标值">
                <template #default="{ row }">
                  {{ formatNumber(row.value) }}
                  <span v-if="rankType === 'efficiency'">%</span>
                  <span v-else-if="rankType === 'consumption'"> MWh</span>
                  <span v-else-if="rankType === 'cost'"> 万元</span>
                  <span v-else-if="rankType === 'carbon'"> t</span>
                </template>
              </el-table-column>
              <el-table-column prop="trend" label="环比变化">
                <template #default="{ row }">
                  <span :style="{ color: row.trend > 0 ? '#f56c6c' : '#67c23a' }">
                    {{ row.trend > 0 ? '+' : '' }}{{ row.trend }}%
                  </span>
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
              <span>成本明细</span>
            </template>
            <el-table :data="costList" style="width: 100%">
              <el-table-column prop="stationName" label="站点" width="150" />
              <el-table-column prop="energyType" label="能源类型" width="100" />
              <el-table-column prop="consumption" label="消耗量" width="120">
                <template #default="{ row }">
                  {{ formatNumber(row.consumption) }}
                  {{ getEnergyUnit(row.energyType) }}
                </template>
              </el-table-column>
              <el-table-column prop="unitPrice" label="单价" width="100">
                <template #default="{ row }">
                  {{ formatNumber(row.unitPrice) }} 元/{{ getEnergyUnit(row.energyType) }}
                </template>
              </el-table-column>
              <el-table-column prop="totalCost" label="总成本" width="120">
                <template #default="{ row }">
                  {{ formatNumber(row.totalCost) }} 万元
                </template>
              </el-table-column>
              <el-table-column prop="dataTime" label="统计时间" width="180" />
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'
import { Money, TrendCharts, Odometer, Coin } from '@element-plus/icons-vue'

const timeGranularity = ref('month')
const rankType = ref('efficiency')

const totalCost = ref(0)
const totalConsumption = ref(0)
const averageEfficiency = ref(0)
const totalCarbon = ref(0)

const trendChartRef = ref(null)
const pieChartRef = ref(null)

const rankList = ref([])
const costList = ref([])
const trendData = ref({ dates: [], costs: [] })
const energySummary = ref({})

const formatNumber = (num) => {
  if (!num) return '0'
  return Number(num).toFixed(2)
}

const getEnergyUnit = (type) => {
  const units = {
    'NATURAL_GAS': 'Nm³',
    'ELECTRICITY': 'kWh',
    'COAL': 't',
    'BIOMASS': 't'
  }
  return units[type] || ''
}

const fetchCostData = async () => {
  const now = new Date()
  let startDate, endDate
  
  if (timeGranularity.value === 'day') {
    startDate = new Date(now.getFullYear(), now.getMonth(), 1)
    endDate = now
  } else if (timeGranularity.value === 'month') {
    startDate = new Date(now.getFullYear(), 0, 1)
    endDate = now
  } else {
    startDate = new Date(now.getFullYear() - 1, 0, 1)
    endDate = now
  }

  try {
    const baseUrl = '/api/cost'
    let url = `${baseUrl}/monthly?year=${now.getFullYear()}&month=${now.getMonth() + 1}`
    
    const costRes = await fetch(url)
    if (!costRes.ok) {
      throw new Error('API not available')
    }
    const costData = await costRes.json()
    costList.value = costData.data || []
    
    const total = costList.value.reduce((sum, item) => sum + (item.totalCost || 0), 0)
    totalCost.value = total
    
    const consumption = costList.value.reduce((sum, item) => sum + (item.consumption || 0), 0)
    totalConsumption.value = consumption
    
    const trendRes = await fetch(`${baseUrl}/trend?startDate=${startDate.toISOString().split('T')[0]}&endDate=${endDate.toISOString().split('T')[0]}`)
    if (!trendRes.ok) {
      throw new Error('API not available')
    }
    const trendDataRes = await trendRes.json()
    trendData.value = trendDataRes
    initTrendChart()
    
    const summaryRes = await fetch(`${baseUrl}/summary?startDate=${startDate.toISOString().split('T')[0]}&endDate=${endDate.toISOString().split('T')[0]}`)
    if (!summaryRes.ok) {
      throw new Error('API not available')
    }
    const summaryData = await summaryRes.json()
    energySummary.value = summaryData
    initPieChart()
    
    fetchRankData()
  } catch (error) {
    console.error('Failed to fetch cost data:', error)
    loadMockData()
  }
}

const fetchRankData = async () => {
  try {
    const now = new Date()
    const url = `/api/cost/rank/${rankType.value}?period=${now.toISOString().split('T')[0]}&topN=10`
    const res = await fetch(url)
    if (!res.ok) {
      return
    }
    const data = await res.json()
    rankList.value = data || []
  } catch (error) {
    console.error('Failed to fetch rank data:', error)
  }
}

const initTrendChart = () => {
  if (!trendChartRef.value) return
  
  const chart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: trendData.value.dates || []
    },
    yAxis: {
      type: 'value',
      name: '成本(万元)'
    },
    series: [{
      data: trendData.value.costs || [],
      type: 'line',
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      }
    }]
  }
  chart.setOption(option)
}

const initPieChart = () => {
  if (!pieChartRef.value) return
  
  const chart = echarts.init(pieChartRef.value)
  const energies = energySummary.value.energies || []
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      type: 'pie',
      radius: '60%',
      data: energies.map((e, i) => ({
        value: e.totalCost || 0,
        name: e.energyType || 'Unknown'
      })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  chart.setOption(option)
}

const loadMockData = () => {
  costList.value = [
    { stationName: '东城区换热站', energyType: 'NATURAL_GAS', consumption: 12500, unitPrice: 3.5, totalCost: 4.38, dataTime: '2026-03-01' },
    { stationName: '西城区换热站', energyType: 'ELECTRICITY', consumption: 8500, unitPrice: 0.8, totalCost: 6.8, dataTime: '2026-03-01' },
    { stationName: '朝阳区换热站', energyType: 'NATURAL_GAS', consumption: 15800, unitPrice: 3.5, totalCost: 5.53, dataTime: '2026-03-01' }
  ]
  
  totalCost.value = 16.71
  totalConsumption.value = 36800
  averageEfficiency.value = 92.5
  totalCarbon.value = 156.8
  
  rankList.value = [
    { rank: 1, stationName: '东城区换热站', value: 95.2, trend: 2.3 },
    { rank: 2, stationName: '朝阳区换热站', value: 93.8, trend: -1.2 },
    { rank: 3, stationName: '西城区换热站', value: 91.5, trend: 0.8 }
  ]
  
  trendData.value = {
    dates: ['2026-01', '2026-02', '2026-03'],
    costs: [45.2, 52.3, 48.6]
  }
  energySummary.value = {
    energies: [
      { energyType: '天然气', totalCost: 25.5 },
      { energyType: '电力', totalCost: 18.2 }
    ]
  }
  
  initTrendChart()
  initPieChart()
}

watch(timeGranularity, () => {
  fetchCostData()
})

watch(rankType, () => {
  fetchRankData()
})

onMounted(() => {
  fetchCostData()
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

.stat-card {
  display: flex;
  align-items: center;
  padding: 15px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}
</style>
