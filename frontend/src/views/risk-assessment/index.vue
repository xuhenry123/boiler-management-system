<template>
  <div class="risk-assessment">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ riskIndex }}</div>
              <div class="stat-label">综合风险指数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: riskLevel === '低' ? '#67c23a' : riskLevel === '中' ? '#e6a23c' : '#f56c6c' }">
              <el-icon :size="32"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ riskLevel }}</div>
              <div class="stat-label">风险等级</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><WarningFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ warningCount }}</div>
              <div class="stat-label">风险预警</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #909399">
              <el-icon :size="32"><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ handledCount }}</div>
              <div class="stat-label">已处理</div>
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
              <span>风险趋势分析</span>
              <el-radio-group v-model="timeRange" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="year">年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="riskTrendChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>风险分布</span>
            </div>
          </template>
          <div ref="riskDistChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>风险评估详情</span>
              <el-select v-model="riskType" placeholder="风险类型" size="small" style="width: 150px">
                <el-option label="全部" value="" />
                <el-option label="设备风险" value="equipment" />
                <el-option label="运行风险" value="operation" />
                <el-option label="能效风险" value="energy" />
                <el-option label="安全风险" value="safety" />
              </el-select>
            </div>
          </template>
          <el-table :data="riskDetails" style="width: 100%">
            <el-table-column prop="type" label="风险类型" width="100" />
            <el-table-column prop="item" label="评估项目" />
            <el-table-column prop="level" label="风险等级" width="100">
              <template #default="{ row }">
                <el-tag :type="row.level === '低' ? 'success' : row.level === '中' ? 'warning' : 'danger'">
                  {{ row.level }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="score" label="风险评分" width="100" />
            <el-table-column prop="factors" label="风险因素" />
            <el-table-column prop="suggestion" label="处理建议" width="200" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '已处理' ? 'success' : 'warning'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { DataAnalysis, Warning, WarningFilled, CircleClose } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const riskTrendChartRef = ref(null)
const riskDistChartRef = ref(null)

const riskIndex = ref(72)
const riskLevel = ref('中')
const warningCount = ref(8)
const handledCount = ref(15)
const timeRange = ref('week')
const riskType = ref('')

const riskDetails = ref([
  { type: '设备', item: '1号锅炉效率下降', level: '高', score: 85, factors: '效率从92%下降至82%', suggestion: '检查燃烧器', status: '待处理' },
  { type: '设备', item: '3号循环泵振动超标', level: '中', score: 68, factors: '振动幅值超过标准', suggestion: '轴承检修', status: '已处理' },
  { type: '运行', item: '2号楼管网不平衡', level: '中', score: 65, factors: '流量偏差率12%', suggestion: '调节阀门', status: '待处理' },
  { type: '能效', item: '系统能效比下降', level: '低', score: 45, factors: 'COP从3.2下降至2.9', suggestion: '优化运行参数', status: '已处理' },
  { type: '安全', item: '燃气压力波动', level: '低', score: 35, factors: '压力波动在允许范围内', suggestion: '继续监控', status: '已处理' }
])

const initRiskTrendChart = () => {
  const chart = echarts.init(riskTrendChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['设备风险', '运行风险', '能效风险', '安全风险'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
    yAxis: { type: 'value', name: '风险指数' },
    series: [
      { name: '设备风险', type: 'line', smooth: true, data: [65, 68, 72, 70, 75, 78, 72] },
      { name: '运行风险', type: 'line', smooth: true, data: [55, 58, 62, 60, 65, 68, 65] },
      { name: '能效风险', type: 'line', smooth: true, data: [45, 48, 52, 50, 55, 52, 45] },
      { name: '安全风险', type: 'line', smooth: true, data: [30, 32, 35, 33, 38, 35, 32] }
    ]
  }
  chart.setOption(option)
}

const initRiskDistChart = () => {
  const chart = echarts.init(riskDistChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 35, name: '设备风险' },
        { value: 28, name: '运行风险' },
        { value: 22, name: '能效风险' },
        { value: 15, name: '安全风险' }
      ]
    }]
  }
  chart.setOption(option)
}

onMounted(() => {
  initRiskTrendChart()
  initRiskDistChart()
})
</script>

<style scoped>
.risk-assessment {
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
