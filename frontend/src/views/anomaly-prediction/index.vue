<template>
  <div class="anomaly-prediction">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><WarnTriangle /></el-icon>
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
                <el-select v-model="severity" placeholder="严重程度" size="small" style="width: 120px">
                  <el-option label="全部" value="" />
                  <el-option label="轻微" value="1" />
                  <el-option label="一般" value="2" />
                  <el-option label="严重" value="3" />
                </el-select>
              </div>
            </div>
          </template>
          <el-table :data="predictionList" style="width: 100%">
            <el-table-column prop="predictTime" label="预测时间" width="180" />
            <el-table-column prop="type" label="预测类型" width="100" />
            <el-table-column prop="location" label="预测位置" />
            <el-table-column prop="anomalyProb" label="异常概率(%)" width="100">
              <template #default="{ row }">
                <el-progress :percentage="row.anomalyProb" :stroke-width="8" :color="getProgressColor(row.anomalyProb)" />
              </template>
            </el-table-column>
            <el-table-column prop="occurTime" label="预计发生时间" width="180" />
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
                <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'primary' : 'success'">
                  {{ row.status === 0 ? '待确认' : row.status === 1 ? '已确认' : '已消除' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link size="small" v-if="row.status === 0">确认</el-button>
                <el-button type="success" link size="small" v-if="row.status === 1">消除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>温度异常预测趋势</span>
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
            </div>
          </template>
          <div ref="accuracyChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { WarnTriangle, Warning, CircleCheck, Timer } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const tempPredictChartRef = ref(null)
const accuracyChartRef = ref(null)

const predictType = ref('')
const severity = ref('')

const predictionStats = ref({
  pendingCount: 5,
  highProbaCount: 3,
  accuracy: 92.5,
  avgLeadTime: '2.5小时'
})

const predictionList = ref([
  { predictTime: '2026-03-15 10:00:00', type: '温度异常', location: '1号楼101室', anomalyProb: 85, occurTime: '2026-03-15 14:00:00', severity: 3, description: '预计温度骤降至16°C以下', advice: '检查供热系统', status: 0 },
  { predictTime: '2026-03-15 09:30:00', type: '压力异常', location: '2号楼入口', anomalyProb: 72, occurTime: '2026-03-15 12:00:00', severity: 2, description: '预计压力下降超过10%', advice: '检查管网泄漏', status: 1 },
  { predictTime: '2026-03-15 09:00:00', type: '设备故障', location: '3号循环泵', anomalyProb: 68, occurTime: '2026-03-16 08:00:00', severity: 2, description: '轴承温度异常升高', advice: '准备备件检修', status: 0 }
])

const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#f56c6c'
  if (percentage >= 60) return '#e6a23c'
  return '#67c23a'
}

const initTempPredictChart = () => {
  const chart = echarts.init(tempPredictChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['实际值', '预测值', '异常阈值'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'] },
    yAxis: { type: 'value', name: '温度(°C)' },
    series: [
      { name: '实际值', type: 'line', data: [20, 19, 18, 17, 16, 15] },
      { name: '预测值', type: 'line', lineStyle: { type: 'dashed' }, data: [20, 19, 18, 16.5, 15, 14] },
      { name: '异常阈值', type: 'line', data: [16, 16, 16, 16, 16, 16], lineStyle: { color: '#f56c6c' } }
    ]
  }
  chart.setOption(option)
}

const initAccuracyChart = () => {
  const chart = echarts.init(accuracyChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
    yAxis: { type: 'value', name: '准确率(%)', min: 80, max: 100 },
    series: [{
      type: 'line',
      smooth: true,
      data: [90, 92, 91, 94, 93, 92, 92.5]
    }]
  }
  chart.setOption(option)
}

onMounted(() => {
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
