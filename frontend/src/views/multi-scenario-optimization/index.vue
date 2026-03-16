<template>
  <div class="multi-scenario-optimization">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><Grid /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ scenarioStats.totalScenarios }}</div>
              <div class="stat-label">场景总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ scenarioStats.activeScenarios }}</div>
              <div class="stat-label">激活场景</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><Aim /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ scenarioStats.totalObjectives }}</div>
              <div class="stat-label">优化目标数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="32"><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ scenarioStats.lastOptimize }}</div>
              <div class="stat-label">上次优化</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>场景管理</span>
              <el-button type="primary" size="small" @click="showScenarioDialog = true">
                <el-icon><Plus /></el-icon>新建场景
              </el-button>
            </div>
          </template>
          <el-table :data="scenarios" style="width: 100%">
            <el-table-column prop="name" label="场景名称" />
            <el-table-column prop="type" label="场景类型">
              <template #default="{ row }">
                <el-tag>{{ getScenarioTypeLabel(row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-switch v-model="row.status" @change="handleStatusChange(row)" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button size="small" @click="editScenario(row)">编辑</el-button>
                <el-button size="small" type="primary" @click="runScenario(row)">运行</el-button>
                <el-button size="small" type="danger" @click="deleteScenario(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>优化目标配置</span>
              <el-button type="primary" size="small" @click="showObjectiveDialog = true">
                <el-icon><Plus /></el-icon>添加目标
              </el-button>
            </div>
          </template>
          <el-table :data="objectives" style="width: 100%">
            <el-table-column prop="name" label="目标名称" />
            <el-table-column prop="type" label="目标类型">
              <template #default="{ row }">
                <el-tag :type="getObjectiveTypeColor(row.type)">{{ getObjectiveTypeLabel(row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="weight" label="权重(%)" />
            <el-table-column prop="target" label="目标值" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" @click="editObjective(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="deleteObjective(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>多目标优化结果</span>
              <el-radio-group v-model="optimizeResult.timeRange" size="small">
                <el-radio-button label="today">今日</el-radio-button>
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="16">
              <div ref="paretoChartRef" style="height: 400px"></div>
            </el-col>
            <el-col :span="8">
              <el-descriptions title="优化结果摘要" :column="1" border>
                <el-descriptions-item label="最优解成本">{{ optimizeResult.bestCost }}元</el-descriptions-item>
                <el-descriptions-item label="最优解能耗">{{ optimizeResult.bestEnergy }}tce</el-descriptions-item>
                <el-descriptions-item label="碳排放">{{ optimizeResult.bestEmissions }}t</el-descriptions-item>
                <el-descriptions-item label="Pareto前沿解数">{{ optimizeResult.paretoCount }}</el-descriptions-item>
                <el-descriptions-item label="优化耗时">{{ optimizeResult.optimizeTime }}s</el-descriptions-item>
              </el-descriptions>
              <el-divider />
              <el-button type="primary" style="width: 100%" @click="applyOptimization">应用优化方案</el-button>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>目标权衡分析</span>
          </template>
          <div ref="tradeoffChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>场景运行历史</span>
          </template>
          <div ref="historyChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showScenarioDialog" :title="editingScenario ? '编辑场景' : '新建场景'" width="600px">
      <el-form :model="scenarioForm" label-width="100px">
        <el-form-item label="场景名称">
          <el-input v-model="scenarioForm.name" placeholder="请输入场景名称" />
        </el-form-item>
        <el-form-item label="场景类型">
          <el-select v-model="scenarioForm.type" placeholder="请选择场景类型" style="width: 100%">
            <el-option label="日常运行" value="normal" />
            <el-option label="极端天气" value="extreme" />
            <el-option label="节能模式" value="energy-saving" />
            <el-option label="峰值负荷" value="peak-load" />
            <el-option label="故障应对" value="fault" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="scenarioForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="气象条件">
          <el-row :gutter="10">
            <el-col :span="8">
              <el-input-number v-model="scenarioForm.weather.outdoorTemp" :step="1" placeholder="室外温度°C" style="width: 100%" />
            </el-col>
            <el-col :span="8">
              <el-input-number v-model="scenarioForm.weather.humidity" :step="5" placeholder="湿度%" style="width: 100%" />
            </el-col>
            <el-col :span="8">
              <el-input-number v-model="scenarioForm.weather.windSpeed" :step="1" placeholder="风速m/s" style="width: 100%" />
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="负荷条件">
          <el-row :gutter="10">
            <el-col :span="12">
              <el-input-number v-model="scenarioForm.load.heatLoad" :step="10" placeholder="热负荷MW" style="width: 100%" />
            </el-col>
            <el-col :span="12">
              <el-input-number v-model="scenarioForm.load.peakFactor" :step="0.1" :min="1" :max="2" placeholder="峰值系数" style="width: 100%" />
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="约束条件">
          <el-checkbox-group v-model="scenarioForm.constraints">
            <el-checkbox label="minTemp">室内温度≥18°C</el-checkbox>
            <el-checkbox label="maxCost">运行成本≤预算</el-checkbox>
            <el-checkbox label="maxEmissions">碳排放≤限额</el-checkbox>
            <el-checkbox label="reliability">供能可靠性≥99%</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showScenarioDialog = false">取消</el-button>
        <el-button type="primary" @click="saveScenario">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showObjectiveDialog" :title="editingObjective ? '编辑目标' : '添加目标'" width="500px">
      <el-form :model="objectiveForm" label-width="100px">
        <el-form-item label="目标名称">
          <el-input v-model="objectiveForm.name" placeholder="请输入目标名称" />
        </el-form-item>
        <el-form-item label="目标类型">
          <el-select v-model="objectiveForm.type" placeholder="请选择目标类型" style="width: 100%">
            <el-option label="经济性" value="economy" />
            <el-option label="能效" value="efficiency" />
            <el-option label="环保" value="environment" />
            <el-option label="可靠性" value="reliability" />
            <el-option label="舒适性" value="comfort" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标方向">
          <el-radio-group v-model="objectiveForm.direction">
            <el-radio label="min">最小化</el-radio>
            <el-radio label="max">最大化</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="权重(%)">
          <el-slider v-model="objectiveForm.weight" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item label="目标值">
          <el-input-number v-model="objectiveForm.target" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-rate v-model="objectiveForm.priority" :max="5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showObjectiveDialog = false">取消</el-button>
        <el-button type="primary" @click="saveObjective">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage, ElMessageBox } from 'element-plus'

const scenarioStats = ref({
  totalScenarios: 8,
  activeScenarios: 3,
  totalObjectives: 5,
  lastOptimize: '10:30'
})

const scenarios = ref([
  { id: 1, name: '日常运行场景', type: 'normal', description: '常规运行模式', status: true },
  { id: 2, name: '冬季极寒', type: 'extreme', description: '极端低温天气', status: true },
  { id: 3, name: '节能模式', type: 'energy-saving', description: '低能耗运行', status: false },
  { id: 4, name: '峰值负荷', type: 'peak-load', description: '高峰时段运行', status: false }
])

const objectives = ref([
  { id: 1, name: '运行成本', type: 'economy', weight: 30, target: '最小化' },
  { id: 2, name: '能源消耗', type: 'efficiency', weight: 25, target: '最小化' },
  { id: 3, name: '碳排放', type: 'environment', weight: 20, target: '最小化' },
  { id: 4, name: '供能可靠性', type: 'reliability', weight: 15, target: '≥99%' },
  { id: 5, name: '室温舒适度', type: 'comfort', weight: 10, target: '≥95%' }
])

const optimizeResult = ref({
  bestCost: '125,680',
  bestEnergy: '1,256',
  bestEmissions: '328',
  paretoCount: 15,
  optimizeTime: '2.3',
  timeRange: 'today'
})

const showScenarioDialog = ref(false)
const showObjectiveDialog = ref(false)
const editingScenario = ref(null)
const editingObjective = ref(null)

const scenarioForm = reactive({
  name: '',
  type: 'normal',
  description: '',
  weather: { outdoorTemp: -5, humidity: 60, windSpeed: 3 },
  load: { heatLoad: 100, peakFactor: 1.2 },
  constraints: ['minTemp']
})

const objectiveForm = reactive({
  name: '',
  type: 'economy',
  direction: 'min',
  weight: 50,
  target: 0,
  priority: 3
})

const paretoChartRef = ref(null)
const tradeoffChartRef = ref(null)
const historyChartRef = ref(null)

let paretoChart = null
let tradeoffChart = null
let historyChart = null

const getScenarioTypeLabel = (type) => {
  const map = { normal: '日常运行', extreme: '极端天气', 'energy-saving': '节能模式', 'peak-load': '峰值负荷', fault: '故障应对' }
  return map[type] || type
}

const getObjectiveTypeLabel = (type) => {
  const map = { economy: '经济性', efficiency: '能效', environment: '环保', reliability: '可靠性', comfort: '舒适性' }
  return map[type] || type
}

const getObjectiveTypeColor = (type) => {
  const map = { economy: '', efficiency: 'success', environment: 'warning', reliability: 'danger', comfort: 'info' }
  return map[type] || ''
}

const editScenario = (row) => {
  editingScenario.value = row
  Object.assign(scenarioForm, row)
  showScenarioDialog.value = true
}

const deleteScenario = (row) => {
  ElMessageBox.confirm('确定删除该场景吗？', '提示', { type: 'warning' }).then(() => {
    scenarios.value = scenarios.value.filter(s => s.id !== row.id)
    ElMessage.success('删除成功')
  })
}

const handleStatusChange = (row) => {
  ElMessage.success(`场景"${row.name}"已${row.status ? '激活' : '停用'}`)
}

const saveScenario = () => {
  ElMessage.success(editingScenario.value ? '保存成功' : '创建成功')
  showScenarioDialog.value = false
  editingScenario.value = null
}

const runScenario = (row) => {
  ElMessage.info(`正在运行场景"${row.name}"...`)
  setTimeout(() => {
    ElMessage.success('场景运行完成')
    initCharts()
  }, 1500)
}

const editObjective = (row) => {
  editingObjective.value = row
  Object.assign(objectiveForm, row)
  showObjectiveDialog.value = true
}

const deleteObjective = (row) => {
  ElMessageBox.confirm('确定删除该目标吗？', '提示', { type: 'warning' }).then(() => {
    objectives.value = objectives.value.filter(o => o.id !== row.id)
    ElMessage.success('删除成功')
  })
}

const saveObjective = () => {
  ElMessage.success(editingObjective.value ? '保存成功' : '添加成功')
  showObjectiveDialog.value = false
  editingObjective.value = null
}

const applyOptimization = () => {
  ElMessageBox.confirm('确定应用当前最优方案吗？', '确认', { type: 'info' }).then(() => {
    ElMessage.success('优化方案已应用')
  })
}

const initCharts = () => {
  initParetoChart()
  initTradeoffChart()
  initHistoryChart()
}

const initParetoChart = () => {
  if (!paretoChartRef.value) return
  if (paretoChart) paretoChart.dispose()
  paretoChart = echarts.init(paretoChartRef.value)
  paretoChart.setOption({
    title: { text: 'Pareto前沿', left: 'center' },
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    xAxis: { type: 'value', name: '成本(万元)' },
    yAxis: { type: 'value', name: '能耗(tce)' },
    series: [
      { name: 'Pareto解', type: 'scatter', symbolSize: 12, data: [[120, 280], [135, 250], [150, 230], [165, 215], [180, 200], [195, 190]] },
      { name: '最优解', type: 'scatter', symbolSize: 18, data: [[150, 230]], itemStyle: { color: '#f56c6c' } }
    ]
  })
}

const initTradeoffChart = () => {
  if (!tradeoffChartRef.value) return
  if (tradeoffChart) tradeoffChart.dispose()
  tradeoffChart = echarts.init(tradeoffChartRef.value)
  tradeoffChart.setOption({
    title: { text: '目标权衡关系', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0 },
    radar: {
      indicator: [
        { name: '成本', max: 100 },
        { name: '能耗', max: 100 },
        { name: '排放', max: 100 },
        { name: '可靠', max: 100 },
        { name: '舒适', max: 100 }
      ]
    },
    series: [{
      type: 'radar',
      data: [
        { value: [85, 70, 65, 90, 88], name: '当前方案' },
        { value: [70, 85, 80, 95, 92], name: '优化方案' }
      ]
    }]
  })
}

const initHistoryChart = () => {
  if (!historyChartRef.value) return
  if (historyChart) historyChart.dispose()
  historyChart = echarts.init(historyChartRef.value)
  historyChart.setOption({
    title: { text: '场景运行历史', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
    yAxis: { type: 'value', name: '运行次数' },
    series: [
      { name: '日常运行', type: 'bar', stack: 'total', data: [5, 6, 5, 6, 5, 3, 3] },
      { name: '极端天气', type: 'bar', stack: 'total', data: [1, 1, 2, 1, 1, 0, 0] },
      { name: '节能模式', type: 'bar', stack: 'total', data: [1, 0, 0, 0, 1, 2, 2] }
    ]
  })
}

onMounted(() => {
  initCharts()
})
</script>

<style scoped>
.multi-scenario-optimization {
  padding: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 15px;
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
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
