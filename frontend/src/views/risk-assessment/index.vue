<template>
  <div class="risk-assessment">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: getRiskIndexColor(compositeIndex) }">
              <el-icon :size="32"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ compositeIndex }}</div>
              <div class="stat-label">综合风险指数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: getRiskLevelColor(riskLevel) }">
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
              <div class="stat-value">{{ pendingCount }}</div>
              <div class="stat-label">待处理风险</div>
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
              <div class="stat-value">{{ handledRate }}%</div>
              <div class="stat-label">处理率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6" v-for="(item, index) in riskDimensionData" :key="index">
        <el-card class="dimension-card" @click="filterByDimension(item.type)">
          <div class="dimension-content">
            <div class="dimension-header">
              <span class="dimension-name">{{ item.name }}</span>
              <el-tag :type="item.level === '高' ? 'danger' : item.level === '中' ? 'warning' : 'success'" size="small">
                {{ item.level }}
              </el-tag>
            </div>
            <div class="dimension-score">
              <el-progress :percentage="item.score" :color="item.color" :stroke-width="8" :show-text="false" />
              <span class="score-value">{{ item.score }}</span>
            </div>
            <div class="dimension-count">
              <span>{{ item.count }} 项风险</span>
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
              <div>
                <el-radio-group v-model="timeRange" size="small" @change="updateTrendChart">
                  <el-radio-button label="week">周</el-radio-button>
                  <el-radio-button label="month">月</el-radio-button>
                  <el-radio-button label="year">年</el-radio-button>
                </el-radio-group>
                <el-button type="primary" size="small" style="margin-left: 10px" @click="refreshData">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </div>
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
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>风险评估详情</span>
              <div>
                <el-select v-model="riskTypeFilter" placeholder="风险类型" size="small" style="width: 130px; margin-right: 10px">
                  <el-option label="全部类型" value="" />
                  <el-option label="设备风险" value="equipment" />
                  <el-option label="运行风险" value="operation" />
                  <el-option label="能效风险" value="energy" />
                  <el-option label="环境风险" value="environment" />
                  <el-option label="安全风险" value="safety" />
                </el-select>
                <el-select v-model="riskLevelFilter" placeholder="风险等级" size="small" style="width: 100px; margin-right: 10px">
                  <el-option label="全部等级" value="" />
                  <el-option label="高" value="高" />
                  <el-option label="中" value="中" />
                  <el-option label="低" value="低" />
                </el-select>
                <el-button type="primary" size="small" @click="showConfigDialog = true">
                  <el-icon><Setting /></el-icon>
                  风险配置
                </el-button>
              </div>
            </div>
          </template>
          <el-table :data="filteredRiskDetails" style="width: 100%" @row-click="showRiskDetail">
            <el-table-column prop="type" label="风险类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getTypeTagType(row.type)">{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="item" label="评估项目" min-width="180" />
            <el-table-column prop="level" label="风险等级" width="80">
              <template #default="{ row }">
                <el-tag :type="row.level === '高' ? 'danger' : row.level === '中' ? 'warning' : 'success'">
                  {{ row.level }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="score" label="评分" width="60">
              <template #default="{ row }">
                <span :style="{ color: getRiskLevelColor(row.level), fontWeight: 'bold' }">{{ row.score }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="factors" label="风险因素" min-width="180" show-overflow-tooltip />
            <el-table-column prop="suggestion" label="处理建议" width="180" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === '已处理' ? 'success' : 'warning'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click.stop="handleRisk(row)">处理</el-button>
                <el-button type="danger" link size="small" @click.stop="viewDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>风险预警</span>
              <el-badge :value="alertList.length" :max="99">
                <el-icon><Bell /></el-icon>
              </el-badge>
            </div>
          </template>
          <div class="alert-list">
            <div v-for="alert in alertList" :key="alert.id" class="alert-item" :class="'alert-' + alert.level">
              <div class="alert-header">
                <el-tag :type="alert.level === 'critical' ? 'danger' : alert.level === 'warn' ? 'warning' : 'info'" size="small">
                  {{ alert.levelText }}
                </el-tag>
                <span class="alert-time">{{ alert.time }}</span>
              </div>
              <div class="alert-content">{{ alert.message }}</div>
              <div class="alert-actions">
                <el-button type="primary" link size="small" @click="handleAlert(alert)">处理</el-button>
                <el-button type="info" link size="small" @click="ignoreAlert(alert)">忽略</el-button>
              </div>
            </div>
            <el-empty v-if="alertList.length === 0" description="暂无风险预警" :image-size="60" />
          </div>
        </el-card>

        <el-card class="chart-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="generateReport('day')" style="width: 100%">
              <el-icon><Document /></el-icon>
              生成日报
            </el-button>
            <el-button type="success" @click="generateReport('week')" style="width: 100%; margin-left: 0; margin-top: 10px">
              <el-icon><Document /></el-icon>
              生成周报
            </el-button>
            <el-button type="warning" @click="generateReport('month')" style="width: 100%; margin-left: 0; margin-top: 10px">
              <el-icon><Document /></el-icon>
              生成月报
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showHandleDialog" title="处理风险" width="500px">
      <el-form label-width="80px">
        <el-form-item label="风险项">
          <el-input :value="currentRisk?.item" disabled />
        </el-form-item>
        <el-form-item label="处理方式">
          <el-radio-group v-model="handleForm.method">
            <el-radio label="fixed">已修复</el-radio>
            <el-radio label="ignored">忽略</el-radio>
            <el-radio label="transferred">转派</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理备注" v-if="handleForm.method !== 'ignored'">
          <el-input v-model="handleForm.remark" type="textarea" :rows="3" placeholder="请输入处理说明" />
        </el-form-item>
        <el-form-item label="转派人员" v-if="handleForm.method === 'transferred'">
          <el-select v-model="handleForm.handler" placeholder="选择接收人" style="width: 100%">
            <el-option label="张三" value="zhangsan" />
            <el-option label="李四" value="lisi" />
            <el-option label="王五" value="wangwu" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showHandleDialog = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确认处理</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showDetailDialog" title="风险详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRisk">
        <el-descriptions-item label="风险类型">{{ currentRisk.type }}</el-descriptions-item>
        <el-descriptions-item label="风险等级">
          <el-tag :type="currentRisk.level === '高' ? 'danger' : currentRisk.level === '中' ? 'warning' : 'success'">
            {{ currentRisk.level }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="风险评分">{{ currentRisk.score }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentRisk.status === '已处理' ? 'success' : 'warning'">
            {{ currentRisk.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="评估项目" :span="2">{{ currentRisk.item }}</el-descriptions-item>
        <el-descriptions-item label="风险因素" :span="2">{{ currentRisk.factors }}</el-descriptions-item>
        <el-descriptions-item label="处理建议" :span="2">{{ currentRisk.suggestion }}</el-descriptions-item>
        <el-descriptions-item label="检测时间">{{ currentRisk.detectTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentRisk.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button type="primary" @click="handleRisk(currentRisk); showDetailDialog = false">立即处理</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showConfigDialog" title="风险配置管理" width="700px">
      <el-tabs v-model="activeConfigTab">
        <el-tab-pane label="阈值配置" name="threshold">
          <el-form :model="thresholdConfig" label-width="150px">
            <el-form-item label="锅炉效率低阈值">
              <el-input-number v-model="thresholdConfig.boilerEfficiencyLow" :min="50" :max="100" />
              <span style="margin-left: 10px">%</span>
            </el-form-item>
            <el-form-item label="锅炉效率高阈值">
              <el-input-number v-model="thresholdConfig.boilerEfficiencyHigh" :min="50" :max="100" />
              <span style="margin-left: 10px">%</span>
            </el-form-item>
            <el-form-item label="循环泵振动阈值">
              <el-input-number v-model="thresholdConfig.pumpVibration" :min="0" :max="10" :step="0.5" />
              <span style="margin-left: 10px">mm/s</span>
            </el-form-item>
            <el-form-item label="温度偏差阈值">
              <el-input-number v-model="thresholdConfig.tempDeviation" :min="0" :max="20" :step="1" />
              <span style="margin-left: 10px">°C</span>
            </el-form-item>
            <el-form-item label="压力上限">
              <el-input-number v-model="thresholdConfig.pressureHigh" :min="0" :max="2" :step="0.1" />
              <span style="margin-left: 10px">MPa</span>
            </el-form-item>
            <el-form-item label="COP低阈值">
              <el-input-number v-model="thresholdConfig.copLow" :min="1" :max="5" :step="0.1" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="权重配置" name="weight">
          <el-form :model="weightConfig" label-width="120px">
            <el-form-item label="设备风险权重">
              <el-slider v-model="weightConfig.equipment" :min="10" :max="50" :step="5" show-input />
            </el-form-item>
            <el-form-item label="运行风险权重">
              <el-slider v-model="weightConfig.operation" :min="10" :max="40" :step="5" show-input />
            </el-form-item>
            <el-form-item label="能效风险权重">
              <el-slider v-model="weightConfig.energy" :min="10" :max="30" :step="5" show-input />
            </el-form-item>
            <el-form-item label="环境风险权重">
              <el-slider v-model="weightConfig.environment" :min="5" :max="20" :step="5" show-input />
            </el-form-item>
            <el-form-item label="安全风险权重">
              <el-slider v-model="weightConfig.safety" :min="10" :max="30" :step="5" show-input />
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="通知配置" name="notification">
          <el-form label-width="120px">
            <el-form-item label="页面弹窗">
              <el-switch v-model="notificationConfig.pagePopup" />
            </el-form-item>
            <el-form-item label="站内消息">
              <el-switch v-model="notificationConfig.siteMessage" />
            </el-form-item>
            <el-form-item label="微信通知">
              <el-switch v-model="notificationConfig.wechat" />
            </el-form-item>
            <el-form-item label="短信通知">
              <el-switch v-model="notificationConfig.sms" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="showConfigDialog = false">取消</el-button>
        <el-button type="primary" @click="saveConfig">保存配置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { DataAnalysis, Warning, WarningFilled, CircleCheck, Refresh, Setting, Bell, Document } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { riskApi } from '@/api'

const riskTrendChartRef = ref(null)
const riskDistChartRef = ref(null)

const compositeIndex = ref(0)
const riskLevel = ref('低')
const pendingCount = ref(0)
const handledCount = ref(0)
const handledRate = ref(0)
const timeRange = ref('week')
const riskTypeFilter = ref('')
const riskLevelFilter = ref('')

const showHandleDialog = ref(false)
const showDetailDialog = ref(false)
const showConfigDialog = ref(false)
const activeConfigTab = ref('threshold')

const currentRisk = ref(null)

const handleForm = ref({
  method: 'fixed',
  remark: '',
  handler: ''
})

const thresholdConfig = ref({
  boilerEfficiencyLow: 80,
  boilerEfficiencyHigh: 85,
  pumpVibration: 4.5,
  tempDeviation: 5,
  pressureHigh: 1.0,
  copLow: 2.5
})

const weightConfig = ref({
  equipment: 30,
  operation: 25,
  energy: 20,
  environment: 10,
  safety: 15
})

const notificationConfig = ref({
  pagePopup: true,
  siteMessage: true,
  wechat: false,
  sms: false
})

const riskDimensionData = ref([
  { name: '设备风险', type: 'equipment', score: 72, level: '中', count: 5, color: '#e6a23c' },
  { name: '运行风险', type: 'operation', score: 65, level: '中', count: 3, color: '#e6a23c' },
  { name: '能效风险', type: 'energy', score: 45, level: '低', count: 2, color: '#67c23a' },
  { name: '环境风险', type: 'environment', score: 38, level: '低', count: 1, color: '#67c23a' },
  { name: '安全风险', type: 'safety', score: 28, level: '低', count: 1, color: '#67c23a' }
])

const riskDetails = ref([
  { type: '设备', item: '1号锅炉效率下降', level: '高', score: 85, factors: '效率从92%下降至82%，排烟温度偏高', suggestion: '检查燃烧器、清理换热面', status: '待处理', detectTime: '2026-03-16 10:30', updateTime: '2026-03-16 14:20' },
  { type: '设备', item: '3号循环泵振动超标', level: '中', score: 68, factors: '振动幅值4.8mm/s，超过标准值', suggestion: '轴承检修、平衡校正', status: '已处理', detectTime: '2026-03-15 08:00', updateTime: '2026-03-15 16:30' },
  { type: '运行', item: '2号楼管网不平衡', level: '中', score: 65, factors: '流量偏差率12%，部分用户室温偏低', suggestion: '调节入口阀门开度', status: '待处理', detectTime: '2026-03-16 09:15', updateTime: '2026-03-16 09:15' },
  { type: '运行', item: '供水温度偏离设定值', level: '中', score: 58, factors: '设定50°C，实际55°C，偏差超过5°C', suggestion: '检查气候补偿曲线设置', status: '待处理', detectTime: '2026-03-16 11:00', updateTime: '2026-03-16 11:00' },
  { type: '能效', item: '系统能效比下降', level: '低', score: 45, factors: 'COP从3.2下降至2.9，能耗增加8%', suggestion: '优化运行参数、清洗换热器', status: '已处理', detectTime: '2026-03-14 14:00', updateTime: '2026-03-15 10:00' },
  { type: '能效', item: '2号锅炉效率偏低', level: '低', score: 42, factors: '运行效率82%，低于平均值88%', suggestion: '检查燃烧状况、调整配风', status: '已处理', detectTime: '2026-03-13 16:00', updateTime: '2026-03-14 09:00' },
  { type: '环境', item: '预报低温风险', level: '低', score: 38, factors: '预报明日最低温度-18°C，低于设计温度', suggestion: '提前增加供热量', status: '已处理', detectTime: '2026-03-16 06:00', updateTime: '2026-03-16 08:00' },
  { type: '安全', item: '燃气压力波动', level: '低', score: 28, factors: '压力波动在允许范围内(3.5-4.2kPa)', suggestion: '继续监控', status: '已处理', detectTime: '2026-03-15 20:00', updateTime: '2026-03-15 22:00' }
])

const alertList = ref([
  { id: 1, level: 'critical', levelText: '严重', message: '1号锅炉效率低于80%，需立即检查', time: '10:30' },
  { id: 2, level: 'warn', levelText: '警告', message: '2号楼管网流量偏差超过10%', time: '09:15' },
  { id: 3, level: 'warn', levelText: '警告', message: '供水温度持续偏离设定值', time: '11:00' },
  { id: 4, level: 'info', levelText: '提示', message: '明日预报低温-18°C，请注意', time: '06:00' }
])

const filteredRiskDetails = computed(() => {
  return riskDetails.value.filter(item => {
    const typeMatch = !riskTypeFilter.value || item.type === getTypeName(riskTypeFilter.value)
    const levelMatch = !riskLevelFilter.value || item.level === riskLevelFilter.value
    return typeMatch && levelMatch
  })
})

const getTypeName = (type) => {
  const map = { equipment: '设备', operation: '运行', energy: '能效', environment: '环境', safety: '安全' }
  return map[type] || type
}

const getTypeTagType = (type) => {
  const map = { '设备': '', '运行': 'warning', '能效': 'info', '环境': 'success', '安全': 'danger' }
  return map[type] || ''
}

const getRiskIndexColor = (index) => {
  if (index <= 40) return '#67c23a'
  if (index <= 70) return '#e6a23c'
  return '#f56c6c'
}

const getRiskLevelColor = (level) => {
  if (level === '低' || level === 'Low') return '#67c23a'
  if (level === '中' || level === 'Medium') return '#e6a23c'
  return '#f56c6c'
}

const filterByDimension = (type) => {
  riskTypeFilter.value = type
}

const updateTrendChart = () => {
  initRiskTrendChart()
}

const refreshData = () => {
  ElMessage.success('数据已刷新')
  initRiskTrendChart()
  initRiskDistChart()
}

const handleRisk = (row) => {
  currentRisk.value = row
  handleForm.value = { method: 'fixed', remark: '', handler: '' }
  showHandleDialog.value = true
}

const submitHandle = () => {
  const idx = riskDetails.value.findIndex(r => r.item === currentRisk.value.item)
  if (idx !== -1) {
    if (handleForm.value.method === 'ignored') {
      riskDetails.value.splice(idx, 1)
      ElMessage.success('风险已忽略')
    } else {
      riskDetails.value[idx].status = '已处理'
      ElMessage.success('风险已处理')
    }
  }
  showHandleDialog.value = false
  pendingCount.value = riskDetails.value.filter(r => r.status === '待处理').length
  handledCount.value = riskDetails.value.filter(r => r.status === '已处理').length
  handledRate.value = Math.round(handledCount.value / (pendingCount.value + handledCount.value) * 100)
}

const viewDetail = (row) => {
  currentRisk.value = row
  showDetailDialog.value = true
}

const showRiskDetail = (row) => {
  viewDetail(row)
}

const handleAlert = (alert) => {
  const risk = riskDetails.value.find(r => alert.message.includes(r.item))
  if (risk) {
    handleRisk(risk)
  }
  alertList.value = alertList.value.filter(a => a.id !== alert.id)
}

const ignoreAlert = (alert) => {
  alertList.value = alertList.value.filter(a => a.id !== alert.id)
  ElMessage.success('预警已忽略')
}

const saveConfig = () => {
  ElMessage.success('配置已保存')
  showConfigDialog.value = false
}

const generateReport = (type) => {
  const typeName = { day: '日报', week: '周报', month: '月报' }
  ElMessage.info(`正在生成${typeName[type]}...`)
  setTimeout(() => {
    ElMessage.success(`${typeName[type]}生成成功`)
  }, 1000)
}

const initRiskTrendChart = () => {
  const chart = echarts.init(riskTrendChartRef.value)
  
  let xData = []
  if (timeRange.value === 'week') {
    xData = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  } else if (timeRange.value === 'month') {
    xData = Array.from({ length: 30 }, (_, i) => `${i + 1}日`)
  } else {
    xData = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  }

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['设备风险', '运行风险', '能效风险', '环境风险', '安全风险', '综合指数'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: xData },
    yAxis: { type: 'value', name: '风险指数', max: 100 },
    series: [
      { name: '设备风险', type: 'line', smooth: true, data: timeRange.value === 'week' ? [65, 68, 72, 70, 75, 78, 72] : Array.from({ length: xData.length }, () => Math.floor(Math.random() * 30 + 60)) },
      { name: '运行风险', type: 'line', smooth: true, data: timeRange.value === 'week' ? [55, 58, 62, 60, 65, 68, 65] : Array.from({ length: xData.length }, () => Math.floor(Math.random() * 30 + 50)) },
      { name: '能效风险', type: 'line', smooth: true, data: timeRange.value === 'week' ? [45, 48, 52, 50, 55, 52, 45] : Array.from({ length: xData.length }, () => Math.floor(Math.random() * 30 + 40)) },
      { name: '环境风险', type: 'line', smooth: true, data: timeRange.value === 'week' ? [35, 38, 42, 40, 45, 42, 38] : Array.from({ length: xData.length }, () => Math.floor(Math.random() * 30 + 30)) },
      { name: '安全风险', type: 'line', smooth: true, data: timeRange.value === 'week' ? [30, 32, 35, 33, 38, 35, 32] : Array.from({ length: xData.length }, () => Math.floor(Math.random() * 20 + 25)) },
      { name: '综合指数', type: 'line', smooth: true, data: timeRange.value === 'week' ? [72, 68, 75, 70, 78, 75, 72] : Array.from({ length: xData.length }, () => Math.floor(Math.random() * 20 + 65)), lineStyle: { width: 3 }, itemStyle: { color: '#f56c6c' } }
    ]
  }
  chart.setOption(option)
}

const initRiskDistChart = () => {
  const chart = echarts.init(riskDistChartRef.value)
  const option = {
    tooltip: { trigger: 'item', formatter: '{b}: {c}项 ({d}%)' },
    legend: { orient: 'vertical', left: 'left', top: 'center' },
    series: [{
      type: 'pie',
      radius: ['30%', '70%'],
      center: ['60%', '50%'],
      data: [
        { value: 5, name: '设备风险', itemStyle: { color: '#409eff' } },
        { value: 3, name: '运行风险', itemStyle: { color: '#e6a23c' } },
        { value: 2, name: '能效风险', itemStyle: { color: '#909399' } },
        { value: 1, name: '环境风险', itemStyle: { color: '#67c23a' } },
        { value: 1, name: '安全风险', itemStyle: { color: '#f56c6c' } }
      ],
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

/**
 * 加载风险指数数据
 */
const loadRiskIndex = async () => {
  try {
    const result = await riskApi.getRiskIndex()
    compositeIndex.value = result.compositeIndex || 0
    riskLevel.value = result.riskLevel === 'MEDIUM' ? '中' : result.riskLevel === 'HIGH' ? '高' : '低'
    pendingCount.value = result.pendingCount || 0
    handledCount.value = result.handledCount || 0
    handledRate.value = result.handledRate || 0
  } catch (error) {
    console.error('加载风险指数失败:', error)
  }
}

/**
 * 加载风险维度数据
 */
const loadRiskDimension = async () => {
  try {
    const result = await riskApi.getRiskDimension()
    riskDimensionData.value = result.data || []
  } catch (error) {
    console.error('加载风险维度失败:', error)
  }
}

/**
 * 加载风险详情数据
 */
const loadRiskDetails = async () => {
  try {
    const result = await riskApi.getRiskDetails()
    riskDetails.value = result.data || []
  } catch (error) {
    console.error('加载风险详情失败:', error)
  }
}

/**
 * 加载风险告警数据
 */
const loadRiskAlerts = async () => {
  try {
    const result = await riskApi.getRiskAlerts()
    alertList.value = result.data || []
  } catch (error) {
    console.error('加载风险告警失败:', error)
  }
}

onMounted(() => {
  loadRiskIndex()
  loadRiskDimension()
  loadRiskDetails()
  loadRiskAlerts()
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
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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

.dimension-card {
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.dimension-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.dimension-content {
  padding: 10px 0;
}

.dimension-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.dimension-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.dimension-score {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dimension-score .el-progress {
  flex: 1;
}

.score-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  min-width: 35px;
}

.dimension-count {
  margin-top: 10px;
  font-size: 13px;
  color: #909399;
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

.alert-list {
  max-height: 300px;
  overflow-y: auto;
}

.alert-item {
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 10px;
  background: #f5f7fa;
  border-left: 3px solid #409eff;
}

.alert-item.alert-critical {
  border-left-color: #f56c6c;
  background: #fef0f0;
}

.alert-item.alert-warn {
  border-left-color: #e6a23c;
  background: #fdf6ec;
}

.alert-item.alert-info {
  border-left-color: #909399;
}

.alert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.alert-time {
  font-size: 12px;
  color: #909399;
}

.alert-content {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
}

.alert-actions {
  display: flex;
  gap: 10px;
}

.quick-actions {
  display: flex;
  flex-direction: column;
}
</style>
