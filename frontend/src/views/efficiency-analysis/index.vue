<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>冷热效率分析</span>
          <el-select v-model="selectedStation" placeholder="选择换热站" style="width: 200px">
            <el-option v-for="s in stations" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ overview.cop }}</div>
            <div class="stat-label">能效比 (COP)</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ overview.heatLossRate }}%</div>
            <div class="stat-label">热损失率</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ overview.supplyBalance }}%</div>
            <div class="stat-label">供热均衡度</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ overview.avgIndoorTemp }}°C</div>
            <div class="stat-label">平均室温</div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>换热站效率排行</span>
            </template>
            <el-table :data="rankingData" style="width: 100%">
              <el-table-column prop="rank" label="排名" width="60" />
              <el-table-column prop="stationName" label="换热站" />
              <el-table-column prop="cop" label="COP" />
              <el-table-column prop="heatLossRate" label="热损失率" />
              <el-table-column prop="supplyBalance" label="均衡度" />
              <el-table-column prop="trend" label="趋势">
                <template #default="{ row }">
                  <el-tag :type="getTrendType(row.trend)">{{ getTrendLabel(row.trend) }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>效率趋势</span>
            </template>
            <div ref="trendChartRef" style="height: 300px"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>实时/历史对比</span>
            </template>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="指标">实时</el-descriptions-item>
              <el-descriptions-item label="COP">{{ comparison.realtime?.cop || '-' }}</el-descriptions-item>
              <el-descriptions-item label="热损失率">{{ comparison.realtime?.heatLossRate || '-' }}%</el-descriptions-item>
              <el-descriptions-item label="均衡度">{{ comparison.realtime?.supplyBalance || '-' }}%</el-descriptions-item>
            </el-descriptions>
            <el-divider />
            <el-descriptions :column="1" border>
              <el-descriptions-item label="指标">同期</el-descriptions-item>
              <el-descriptions-item label="COP">{{ comparison.history?.cop || '-' }}</el-descriptions-item>
              <el-descriptions-item label="热损失率">{{ comparison.history?.heatLossRate || '-' }}%</el-descriptions-item>
              <el-descriptions-item label="均衡度">{{ comparison.history?.supplyBalance || '-' }}%</el-descriptions-item>
            </el-descriptions>
            <el-divider />
            <el-descriptions :column="1" border>
              <el-descriptions-item label="变化率">
                <span style="color: #67c23a">COP: {{ comparison.comparison?.copChange || '-' }}</span><br/>
                <span style="color: #67c23a">热损失: {{ comparison.comparison?.heatLossChange || '-' }}</span><br/>
                <span style="color: #67c23a">均衡度: {{ comparison.comparison?.balanceChange || '-' }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>终点站监控</span>
                <el-button size="small" type="primary" @click="showTempDialog = true">住户温度录入</el-button>
              </div>
            </template>
            <el-table :data="terminalData" style="width: 100%">
              <el-table-column prop="buildingName" label="建筑" />
              <el-table-column prop="avgIndoorTemp" label="平均温度" />
              <el-table-column prop="targetTemp" label="目标温度" />
              <el-table-column prop="deviation" label="偏差" />
              <el-table-column prop="sampleCount" label="样本数" />
              <el-table-column prop="abnormalCount" label="异常">
                <template #default="{ row }">
                  <el-tag v-if="row.abnormalCount > 0" type="danger">{{ row.abnormalCount }}</el-tag>
                  <span v-else>0</span>
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
                <span>供暖曲线优化</span>
                <el-button size="small" type="primary" @click="showCurveDialog = true">优化建议</el-button>
              </div>
            </template>
            <div ref="curveChartRef" style="height: 350px"></div>
            <div style="margin-top: 16px">
              <el-alert
                :title="heatingCurve.analysis"
                type="success"
                :closable="false"
                show-icon
              />
              <div style="margin-top: 12px">
                <span>预计节能: </span>
                <strong style="color: #67c23a; font-size: 18px">{{ heatingCurve.estimatedSavings }}%</strong>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog v-model="showTempDialog" title="住户温度录入" width="500px">
      <el-form :model="tempForm" label-width="100px">
        <el-form-item label="建筑">
          <el-select v-model="tempForm.buildingId" placeholder="选择建筑">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="tempForm.roomCode" placeholder="如: 101" />
        </el-form-item>
        <el-form-item label="温度">
          <el-input-number v-model="tempForm.temperature" :min="10" :max="30" :precision="1" />
        </el-form-item>
        <el-form-item label="记录时间">
          <el-date-picker v-model="tempForm.recordTime" type="datetime" placeholder="选择时间" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="tempForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showTempDialog = false">取消</el-button>
        <el-button type="primary" @click="saveTemperature">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showCurveDialog" title="供暖曲线优化建议" width="600px">
      <el-alert
        title="基于历史数据分析，建议降低供水温度2-3度，可降低热损失约8.5%"
        type="success"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      />
      <el-table :data="curveComparisonData" border>
        <el-table-column prop="outdoorTemp" label="室外温度" />
        <el-table-column prop="currentSupply" label="当前供水" />
        <el-table-column prop="optimizedSupply" label="优化供水" />
        <el-table-column prop="currentReturn" label="当前回水" />
        <el-table-column prop="optimizedReturn" label="优化回水" />
      </el-table>
      <template #footer>
        <el-button @click="showCurveDialog = false">取消</el-button>
        <el-button type="primary" @click="applyCurve">应用优化曲线</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import * as echarts from 'echarts'
import { efficiencyApi, stationApi } from '@/api'
import { ElMessage } from 'element-plus'

const selectedStation = ref(null)
const stations = ref([])
const buildings = ref([
  { id: 1, name: '1号楼' },
  { id: 2, name: '2号楼' },
  { id: 3, name: '3号楼' }
])

const overview = ref({
  cop: '3.2',
  heatLossRate: '12.5',
  supplyBalance: '85.0',
  avgIndoorTemp: '20.5'
})

const rankingData = ref([])
const terminalData = ref([])
const comparison = ref({})
const trendData = ref({})
const heatingCurve = ref({})

const trendChartRef = ref(null)
const curveChartRef = ref(null)

const showTempDialog = ref(false)
const showCurveDialog = ref(false)

const tempForm = reactive({
  buildingId: null,
  roomCode: '',
  temperature: 20,
  recordTime: new Date(),
  remark: ''
})

const curveComparisonData = ref([
  { outdoorTemp: '-15°C', currentSupply: 75, optimizedSupply: 72, currentReturn: 55, optimizedReturn: 52 },
  { outdoorTemp: '-10°C', currentSupply: 65, optimizedSupply: 62, currentReturn: 48, optimizedReturn: 45 },
  { outdoorTemp: '-5°C', currentSupply: 55, optimizedSupply: 52, currentReturn: 40, optimizedReturn: 38 },
  { outdoorTemp: '0°C', currentSupply: 45, optimizedSupply: 42, currentReturn: 33, optimizedReturn: 30 },
  { outdoorTemp: '5°C', currentSupply: 38, optimizedSupply: 35, currentReturn: 28, optimizedReturn: 26 }
])

let trendChart = null
let curveChart = null

const getTrendType = (trend) => {
  if (trend === 'up') return 'success'
  if (trend === 'down') return 'danger'
  return 'info'
}

const getTrendLabel = (trend) => {
  if (trend === 'up') return '↑上升'
  if (trend === 'down') return '↓下降'
  return '→稳定'
}

const loadStations = async () => {
  try {
    const res = await stationApi.getAllStations()
    stations.value = res.map(s => ({ id: s.id, name: s.stationName || s.stationCode }))
    if (stations.value.length > 0) {
      selectedStation.value = stations.value[0].id
    }
  } catch (e) {
    stations.value = [
      { id: 1, name: '换热站A' },
      { id: 2, name: '换热站B' },
      { id: 3, name: '换热站C' }
    ]
    selectedStation.value = 1
  }
}

const loadOverview = async () => {
  try {
    const res = await efficiencyApi.getOverview({ stationId: selectedStation.value })
    overview.value = res
  } catch (e) {
    console.error(e)
  }
}

const loadRanking = async () => {
  try {
    const res = await efficiencyApi.getRanking({ limit: 10 })
    rankingData.value = res.data || []
  } catch (e) {
    rankingData.value = [
      { rank: 1, stationName: '换热站A', cop: 3.5, heatLossRate: 8.2, supplyBalance: 95.0, trend: 'up' },
      { rank: 2, stationName: '换热站B', cop: 3.2, heatLossRate: 12.0, supplyBalance: 88.5, trend: 'stable' },
      { rank: 3, stationName: '换热站C', cop: 3.0, heatLossRate: 14.5, supplyBalance: 82.0, trend: 'down' }
    ]
  }
}

const loadTerminalMonitor = async () => {
  try {
    const res = await efficiencyApi.getTerminalMonitor({ stationId: selectedStation.value })
    terminalData.value = res.data || []
  } catch (e) {
    terminalData.value = [
      { buildingId: 1, buildingName: '1号楼', avgIndoorTemp: 21.2, targetTemp: 20.0, deviation: 1.2, sampleCount: 120, abnormalCount: 3 },
      { buildingId: 2, buildingName: '2号楼', avgIndoorTemp: 20.8, targetTemp: 20.0, deviation: 0.8, sampleCount: 95, abnormalCount: 2 },
      { buildingId: 3, buildingName: '3号楼', avgIndoorTemp: 21.5, targetTemp: 20.0, deviation: 1.5, sampleCount: 110, abnormalCount: 1 }
    ]
  }
}

const loadComparison = async () => {
  try {
    const res = await efficiencyApi.getComparison({
      stationId: selectedStation.value,
      type: 'realtime-history',
      startDate: '2026-03-01',
      endDate: '2026-03-16'
    })
    comparison.value = res
  } catch (e) {
    comparison.value = {
      realtime: { cop: 3.2, heatLossRate: 12.5, supplyBalance: 85.0 },
      history: { cop: 3.0, heatLossRate: 15.0, supplyBalance: 78.0 },
      comparison: { copChange: '+6.7%', heatLossChange: '-16.7%', balanceChange: '+9.0%' }
    }
  }
}

const loadTrend = async () => {
  try {
    const res = await efficiencyApi.getTrend({ stationId: selectedStation.value, period: 'week' })
    trendData.value = res
    initTrendChart()
  } catch (e) {
    trendData.value = {
      dates: ['03-10', '03-11', '03-12', '03-13', '03-14', '03-15', '03-16'],
      cop: [3.0, 3.1, 3.2, 3.1, 3.3, 3.2, 3.2],
      heatLossRate: [15.0, 14.5, 13.8, 14.2, 13.0, 12.8, 12.5]
    }
    initTrendChart()
  }
}

const loadHeatingCurve = async () => {
  try {
    const res = await efficiencyApi.getHeatingCurve({ stationId: selectedStation.value })
    heatingCurve.value = res
    initCurveChart()
  } catch (e) {
    heatingCurve.value = {
      currentCurve: [
        { outdoorTemp: -15, supplyTemp: 75, returnTemp: 55 },
        { outdoorTemp: -10, supplyTemp: 65, returnTemp: 48 },
        { outdoorTemp: -5, supplyTemp: 55, returnTemp: 40 },
        { outdoorTemp: 0, supplyTemp: 45, returnTemp: 33 },
        { outdoorTemp: 5, supplyTemp: 38, returnTemp: 28 }
      ],
      optimizedCurve: [
        { outdoorTemp: -15, supplyTemp: 72, returnTemp: 52 },
        { outdoorTemp: -10, supplyTemp: 62, returnTemp: 45 },
        { outdoorTemp: -5, supplyTemp: 52, returnTemp: 38 },
        { outdoorTemp: 0, supplyTemp: 42, returnTemp: 30 },
        { outdoorTemp: 5, supplyTemp: 35, returnTemp: 26 }
      ],
      estimatedSavings: 8.5,
      analysis: '基于历史数据分析，建议降低供水温度2-3度，可降低热损失约8.5%'
    }
    initCurveChart()
  }
}

const initTrendChart = () => {
  if (!trendChartRef.value) return
  if (trendChart) trendChart.dispose()
  
  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['COP', '热损失率(%)'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: trendData.value.dates || [] },
    yAxis: [
      { type: 'value', name: 'COP', min: 2, max: 4 },
      { type: 'value', name: '热损失率(%)', min: 0, max: 20 }
    ],
    series: [
      { name: 'COP', type: 'line', data: trendData.value.cop || [], smooth: true },
      { name: '热损失率(%)', type: 'line', yAxisIndex: 1, data: trendData.value.heatLossRate || [], smooth: true }
    ]
  })
}

const initCurveChart = () => {
  if (!curveChartRef.value) return
  if (curveChart) curveChart.dispose()
  
  curveChart = echarts.init(curveChartRef.value)
  const currentCurve = heatingCurve.value.currentCurve || []
  const optimizedCurve = heatingCurve.value.optimizedCurve || []
  
  curveChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['当前供水温度', '优化供水温度', '当前回水温度', '优化回水温度'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { 
      type: 'category', 
      data: currentCurve.map(c => c.outdoorTemp + '°C'),
      name: '室外温度'
    },
    yAxis: { type: 'value', name: '温度(°C)' },
    series: [
      { name: '当前供水温度', type: 'line', data: currentCurve.map(c => c.supplyTemp), smooth: true },
      { name: '优化供水温度', type: 'line', data: optimizedCurve.map(c => c.supplyTemp), smooth: true, linestyle: { type: 'dashed' } },
      { name: '当前回水温度', type: 'line', data: currentCurve.map(c => c.returnTemp), smooth: true },
      { name: '优化回水温度', type: 'line', data: optimizedCurve.map(c => c.returnTemp), smooth: true, linestyle: { type: 'dashed' } }
    ]
  })
}

const saveTemperature = async () => {
  try {
    await efficiencyApi.saveResidentTemperature({
      buildingId: tempForm.buildingId,
      userId: 1001,
      roomCode: tempForm.roomCode,
      temperature: tempForm.temperature,
      collectTime: tempForm.recordTime,
      remark: tempForm.remark
    })
    ElMessage.success('保存成功')
    showTempDialog.value = false
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const applyCurve = async () => {
  try {
    await efficiencyApi.applyHeatingCurve(selectedStation.value, heatingCurve.value.optimizedCurve)
    ElMessage.success('优化曲线已应用')
    showCurveDialog.value = false
  } catch (e) {
    ElMessage.error('应用失败')
  }
}

watch(selectedStation, () => {
  loadOverview()
  loadTerminalMonitor()
  loadComparison()
  loadTrend()
  loadHeatingCurve()
})

onMounted(() => {
  loadStations()
  loadRanking()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-card {
  text-align: center;
  padding: 10px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}
</style>
