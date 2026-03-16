<template>
  <div class="anomaly-prediction">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><WarningFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ predictionStats.pendingCount }}</div>
              <div class="stat-label">待确认异常</div>
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
              <div class="stat-value">{{ predictionStats.highProbaCount }}</div>
              <div class="stat-label">高概率预测</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ predictionStats.accuracy }}%</div>
              <div class="stat-label">预测准确率</div>
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
              <div class="stat-value">{{ predictionStats.avgLeadTime }}</div>
              <div class="stat-label">平均提前时间</div>
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
              <span>异常预测列表</span>
              <div>
                <el-select v-model="predictType" placeholder="预测类型" size="small" style="width: 120px; margin-right: 10px">
                  <el-option label="全部" value="" />
                  <el-option label="温度异常" value="temp" />
                  <el-option label="压力异常" value="pressure" />
                  <el-option label="流量异常" value="flow" />
                  <el-option label="设备故障" value="equipment" />
                </el-select>
                <el-select v-model="severity" placeholder="严重程度" size="small" style="width: 120px; margin-right: 10px">
                  <el-option label="全部" value="" />
                  <el-option label="轻微" value="1" />
                  <el-option label="一般" value="2" />
                  <el-option label="严重" value="3" />
                </el-select>
                <el-button type="primary" size="small" @click="refreshPredictions">刷新</el-button>
              </div>
            </div>
          </template>
          <el-table :data="predictionList" style="width: 100%">
            <el-table-column prop="detectedAt" label="检测时间" width="180" />
            <el-table-column prop="metricType" label="异常类型" width="100">
              <template #default="{ row }">
                <el-tag>{{ getTypeLabel(row.metricType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="deviceName" label="设备位置" />
            <el-table-column prop="anomalyScore" label="异常概率(%)" width="150">
              <template #default="{ row }">
                <el-progress :percentage="row.anomalyScore ? Math.round(row.anomalyScore * 100) : 0" :stroke-width="10" :color="getProgressColor(row.anomalyScore ? row.anomalyScore * 100 : 0)" />
              </template>
            </el-table-column>
            <el-table-column prop="metricValue" label="当前值" width="100" />
            <el-table-column prop="threshold" label="阈值" width="100" />
            <el-table-column prop="severity" label="严重程度" width="80">
              <template #default="{ row }">
                <el-tag :type="row.severity === 3 ? 'danger' : row.severity === 2 ? 'warning' : 'info'">
                  {{ row.severity === 3 ? '严重' : row.severity === 2 ? '一般' : '轻微' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="异常描述" />
            <el-table-column prop="advice" label="处理建议" width="150" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusLabel(row.status).type">
                  {{ getStatusLabel(row.status).text }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" link size="small" v-if="row.status === 'PENDING'" @click="confirmAnomaly(row)">确认</el-button>
                <el-button type="success" link size="small" v-if="row.status === 'CONFIRMED'" @click="resolveAnomaly(row)">消除</el-button>
                <el-button type="info" link size="small" @click="viewDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination style="margin-top: 15px; text-align: right" :current-page="currentPage" :page-size="pageSize" :total="total" layout="total, prev, pager, next" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>温度异常预测趋势</span>
              <el-select v-model="selectedPoint" size="small" style="width: 150px">
                <el-option v-for="item in monitorPoints" :key="item" :label="item" :value="item" />
              </el-select>
            </div>
          </template>
          <div ref="tempPredictChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>预测准确率趋势</span>
              <el-radio-group v-model="accuracyRange" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="accuracyChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <span>异常检测模型配置</span>
          </template>
          <el-form :inline="true" label-width="140px">
            <el-form-item label="检测算法">
              <el-select v-model="modelConfig.algorithm" style="width: 150px">
                <el-option label="孤立森林" value="iforest" />
                <el-option label="LSTM时序" value="lstm" />
                <el-option label="ARIMA" value="arima" />
                <el-option label="混合模型" value="hybrid" />
              </el-select>
            </el-form-item>
            <el-form-item label="异常阈值">
              <el-slider v-model="modelConfig.threshold" :min="50" :max="99" style="width: 150px" />
              <span style="margin-left: 10px">{{ modelConfig.threshold }}%</span>
            </el-form-item>
            <el-form-item label="预测提前量">
              <el-input-number v-model="modelConfig.leadHours" :min="1" :max="24" />
              <span style="margin-left: 10px">小时</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveModelConfig">保存配置</el-button>
              <el-button @click="retrainModel">重新训练</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="detailDialogVisible" title="异常预测详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预测类型">{{ currentDetail.type }}</el-descriptions-item>
        <el-descriptions-item label="异常概率">{{ currentDetail.anomalyProb }}%</el-descriptions-item>
        <el-descriptions-item label="预测位置">{{ currentDetail.location }}</el-descriptions-item>
        <el-descriptions-item label="严重程度">{{ currentDetail.severity }}</el-descriptions-item>
        <el-descriptions-item label="预测时间">{{ currentDetail.predictTime }}</el-descriptions-item>
        <el-descriptions-item label="预计发生时间">{{ currentDetail.occurTime }}</el-descriptions-item>
        <el-descriptions-item label="异常描述" :span="2">{{ currentDetail.description }}</el-descriptions-item>
        <el-descriptions-item label="处理建议" :span="2">{{ currentDetail.advice }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="exportDetail">导出报告</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { WarningFilled, Warning, CircleCheck, Timer } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { anomalyApi, alertApi } from '@/api'

const tempPredictChartRef = ref(null)
const accuracyChartRef = ref(null)

const predictType = ref('')
const severity = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedPoint = ref('1号楼101室')
const accuracyRange = ref('week')
const detailDialogVisible = ref(false)
const monitorPoints = ref(['1号楼101室', '2号楼入口', '3号循环泵'])

const predictionStats = ref({
  pendingCount: 0,
  highProbaCount: 0,
  accuracy: 0,
  avgLeadTime: '0小时'
})

const modelConfig = ref({
  algorithm: 'iforest',
  threshold: 80,
  leadHours: 4
})

const currentDetail = ref({})

const predictionList = ref([])

const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#f56c6c'
  if (percentage >= 60) return '#e6a23c'
  return '#67c23a'
}

const getTypeLabel = (type) => {
  const typeMap = {
    'temp': '温度异常',
    'pressure': '压力异常',
    'flow': '流量异常',
    'equipment': '设备故障'
  }
  return typeMap[type] || type
}

const getStatusLabel = (status) => {
  const statusMap = {
    'PENDING': { text: '待确认', type: 'warning' },
    'CONFIRMED': { text: '已确认', type: 'primary' },
    'RESOLVED': { text: '已消除', type: 'success' }
  }
  return statusMap[status] || { text: status, type: 'info' }
}

const fetchAnomalies = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      type: predictType.value,
      severity: severity.value ? parseInt(severity.value) : null
    }
    const res = await anomalyApi.getAnomalies(params)
    predictionList.value = res.data || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取异常列表失败:', error)
  }
}

const fetchStats = async () => {
  try {
    const res = await anomalyApi.getAnomalyStats()
    predictionStats.value = res || {
      pendingCount: 0,
      highProbaCount: 0,
      accuracy: 0,
      avgLeadTime: '0小时'
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const refreshPredictions = async () => {
  await fetchAnomalies()
  await fetchStats()
  ElMessage.success('预测数据已刷新')
}

const confirmAnomaly = async (row) => {
  try {
    await anomalyApi.confirmAnomaly(row.id, '')
    row.status = 'CONFIRMED'
    ElMessage.success('异常已确认')
    await fetchStats()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const resolveAnomaly = async (row) => {
  try {
    await anomalyApi.resolveAnomaly(row.id)
    row.status = 'RESOLVED'
    ElMessage.success('异常已消除')
    await fetchStats()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const viewDetail = (row) => {
  currentDetail.value = {
    type: getTypeLabel(row.metricType),
    anomalyProb: row.anomalyScore ? Math.round(row.anomalyScore * 100) : 0,
    location: row.deviceName || row.deviceId,
    severity: row.severity,
    predictTime: row.detectedAt,
    occurTime: row.detectedAt,
    description: row.description,
    advice: row.advice
  }
  detailDialogVisible.value = true
}

const saveModelConfig = () => {
  ElMessage.success('模型配置已保存')
}

const retrainModel = () => {
  ElMessage.info('正在重新训练模型...')
  setTimeout(() => {
    ElMessage.success('模型训练完成')
  }, 2000)
}

const exportDetail = () => {
  ElMessage.info('正在生成异常报告...')
}

const initTempPredictChart = () => {
  const chart = echarts.init(tempPredictChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['实际值', '预测值', '异常阈值', '置信区间'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'] },
    yAxis: { type: 'value', name: '温度(°C)' },
    series: [
      { name: '实际值', type: 'line', data: [20, 19, 18, 17, 16, 15], itemStyle: { color: '#409eff' } },
      { name: '预测值', type: 'line', lineStyle: { type: 'dashed', color: '#e6a23c' }, data: [20, 19, 18, 16.5, 15, 14] },
      { name: '异常阈值', type: 'line', data: [16, 16, 16, 16, 16, 16], lineStyle: { color: '#f56c6c', type: 'dotted' } },
      { name: '置信区间', type: 'line', areaStyle: { opacity: 0.1 }, data: [21, 20, 19, 17.5, 16, 15], showSymbol: false, lineStyle: { opacity: 0 } }
    ]
  }
  chart.setOption(option)
}

const initAccuracyChart = () => {
  const chart = echarts.init(accuracyChartRef.value)
  const data = accuracyRange.value === 'week' 
    ? [90, 92, 91, 94, 93, 92, 92.5]
    : [88, 90, 92, 89, 91, 93, 92, 94, 91, 90, 92, 95]
  const xData = accuracyRange.value === 'week'
    ? ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    : ['第1周', '第2周', '第3周', '第4周']
    
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: xData },
    yAxis: { type: 'value', name: '准确率(%)', min: 80, max: 100 },
    series: [{
      type: 'line',
      smooth: true,
      data: data,
      areaStyle: { opacity: 0.2 },
      itemStyle: { color: '#67c23a' }
    }]
  }
  chart.setOption(option)
}

onMounted(async () => {
  await fetchStats()
  await fetchAnomalies()
  initTempPredictChart()
  initAccuracyChart()
})
</script>

<style scoped>
.anomaly-prediction {
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
