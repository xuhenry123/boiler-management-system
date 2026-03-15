<template>
  <div class="network-analysis">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><Connection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ networkInfo.nodeCount }}</div>
              <div class="stat-label">管网节点数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ networkInfo.segmentCount }}</div>
              <div class="stat-label">管段数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><PieChart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ balanceStatus }}%</div>
              <div class="stat-label">平衡度</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: diagnosisStatus === '正常' ? '#67c23a' : '#f56c6c' }">
              <el-icon :size="32"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ diagnosisStatus }}</div>
              <div class="stat-label">诊断状态</div>
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
              <span>管网水力分布图</span>
              <div>
                <el-select v-model="networkView" size="small" style="width: 100px; margin-right: 10px">
                  <el-option label="拓扑图" value="topology" />
                  <el-option label="压力图" value="pressure" />
                  <el-option label="流量图" value="flow" />
                </el-select>
                <el-select v-model="selectedStation" placeholder="选择换热站" size="small" style="width: 120px" @change="loadNetworkData">
                  <el-option v-for="item in stations" :key="item" :label="item" :value="item" />
                </el-select>
              </div>
            </div>
          </template>
          <div ref="networkChartRef" style="height: 400px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span>节点压力分布</span>
          </template>
          <el-table :data="nodePressureData" style="width: 100%" max-height="360">
            <el-table-column prop="nodeName" label="节点" />
            <el-table-column prop="pressure" label="压力(MPa)" width="100">
              <template #default="{ row }">
                <span :style="{ color: row.pressure < 0.35 ? '#f56c6c' : row.pressure > 0.55 ? '#e6a23c' : '#67c23a' }">
                  {{ row.pressure }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="designPressure" label="设计压力" width="80" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === '正常' ? 'success' : 'warning'" size="small">
                  {{ row.status }}
                </el-tag>
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
            <span>流量平衡分析</span>
          </template>
          <div ref="flowBalanceChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>管网诊断结果</span>
              <el-button type="primary" size="small" @click="runDiagnosis">重新诊断</el-button>
            </div>
          </template>
          <el-table :data="diagnosisResults" style="width: 100%">
            <el-table-column prop="type" label="诊断类型" width="100" />
            <el-table-column prop="location" label="位置" />
            <el-table-column prop="severity" label="严重程度" width="100">
              <template #default="{ row }">
                <el-tag :type="row.severity === '轻微' ? 'info' : row.severity === '一般' ? 'warning' : 'danger'">
                  {{ row.severity }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="suggestion" label="处理建议" />
            <el-table-column prop="detectedTime" label="检测时间" width="160" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>优化调节建议</span>
              <div>
                <el-select v-model="optimizeStrategy" size="small" style="width: 120px; margin-right: 10px">
                  <el-option label="自动平衡" value="auto" />
                  <el-option label="快速平衡" value="fast" />
                  <el-option label="渐进平衡" value="gradual" />
                </el-select>
                <el-button type="primary" size="small" @click="executeOptimization">执行优化</el-button>
              </div>
            </div>
          </template>
          <el-table :data="optimizationSuggestions" style="width: 100%">
            <el-table-column prop="valve" label="阀门位置" width="150" />
            <el-table-column prop="currentOpen" label="当前开度(%)" width="120">
              <template #default="{ row }">
                <el-progress :percentage="row.currentOpen" :stroke-width="8" :color="getProgressColor(row.currentOpen, row.suggestedOpen)" />
              </template>
            </el-table-column>
            <el-table-column prop="suggestedOpen" label="建议开度(%)" width="120" />
            <el-table-column prop="adjustment" label="调节幅度(%)" width="100">
              <template #default="{ row }">
                <span :style="{ color: row.adjustment > 0 ? '#67c23a' : row.adjustment < 0 ? '#f56c6c' : '#909399' }">
                  {{ row.adjustment > 0 ? '+' : '' }}{{ row.adjustment }}%
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="expectedEffect" label="预期效果" />
            <el-table-column prop="priority" label="优先级" width="80">
              <template #default="{ row }">
                <el-tag :type="row.priority === '高' ? 'danger' : row.priority === '中' ? 'warning' : 'info'">
                  {{ row.priority }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="executeSingleValve(row)">执行</el-button>
                <el-button size="small" @click="previewEffect(row)">预览</el-button>
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
            <span>管网平衡历史记录</span>
          </template>
          <el-table :data="balanceHistory" style="width: 100%">
            <el-table-column prop="time" label="调节时间" width="180" />
            <el-table-column prop="valve" label="调节阀门" />
            <el-table-column prop="beforeOpen" label="调节前(%)" width="100" />
            <el-table-column prop="afterOpen" label="调节后(%)" width="100" />
            <el-table-column prop="effect" label="调节效果">
              <template #default="{ row }">
                <el-tag :type="row.effect === '良好' ? 'success' : row.effect === '一般' ? 'warning' : 'danger'">
                  {{ row.effect }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="operator" label="操作人" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Connection, DataLine, PieChart, Warning } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const networkChartRef = ref(null)
const flowBalanceChartRef = ref(null)

const selectedStation = ref('')
const networkView = ref('topology')
const optimizeStrategy = ref('auto')
const stations = ref(['换热站1', '换热站2', '换热站3'])

const networkInfo = ref({
  nodeCount: 48,
  segmentCount: 56
})

const balanceStatus = ref(92.5)
const diagnosisStatus = ref('正常')

const nodePressureData = ref([
  { nodeName: '换热站', pressure: 0.52, designPressure: 0.6, status: '正常' },
  { nodeName: '1号楼', pressure: 0.42, designPressure: 0.4, status: '正常' },
  { nodeName: '2号楼', pressure: 0.38, designPressure: 0.4, status: '偏低' },
  { nodeName: '3号楼', pressure: 0.45, designPressure: 0.4, status: '正常' },
  { nodeName: '4号楼', pressure: 0.33, designPressure: 0.4, status: '偏低' },
  { nodeName: '5号楼', pressure: 0.48, designPressure: 0.4, status: '正常' }
])

const diagnosisResults = ref([
  { type: '平衡诊断', location: '2号楼支路', severity: '一般', suggestion: '增大阀门开度5%', detectedTime: '2026-03-15 10:00:00' },
  { type: '阻力诊断', location: '4号楼入口', severity: '严重', suggestion: '检查管道是否堵塞', detectedTime: '2026-03-15 09:30:00' },
  { type: '压力诊断', location: '3号楼三层', severity: '轻微', suggestion: '注意观察', detectedTime: '2026-03-15 09:00:00' }
])

const optimizationSuggestions = ref([
  { valve: '2号楼入口阀', currentOpen: 65, suggestedOpen: 72, adjustment: 7, expectedEffect: '流量增加5%', priority: '高' },
  { valve: '3号楼入口阀', currentOpen: 70, suggestedOpen: 68, adjustment: -2, expectedEffect: '流量减少2%', priority: '中' },
  { valve: '4号楼入口阀', currentOpen: 55, suggestedOpen: 80, adjustment: 25, expectedEffect: '流量增加15%', priority: '高' },
  { valve: '5号楼入口阀', currentOpen: 75, suggestedOpen: 75, adjustment: 0, expectedEffect: '无需调节', priority: '低' }
])

const balanceHistory = ref([
  { time: '2026-03-15 09:00:00', valve: '1号楼入口阀', beforeOpen: 60, afterOpen: 65, effect: '良好', operator: '系统' },
  { time: '2026-03-15 08:00:00', valve: '3号楼入口阀', beforeOpen: 72, afterOpen: 70, effect: '良好', operator: '张三' },
  { time: '2026-03-14 16:00:00', valve: '2号楼入口阀', beforeOpen: 55, afterOpen: 65, effect: '一般', operator: '系统' }
])

const getProgressColor = (current, suggested) => {
  const diff = Math.abs(current - suggested)
  if (diff <= 5) return '#67c23a'
  if (diff <= 15) return '#e6a23c'
  return '#f56c6c'
}

const loadNetworkData = () => {
  initNetworkChart()
}

const runDiagnosis = () => {
  ElMessage.info('正在运行管网诊断...')
  setTimeout(() => {
    ElMessage.success('诊断完成，发现3个问题')
    initFlowBalanceChart()
  }, 1500)
}

const executeOptimization = () => {
  ElMessage.info('正在执行优化调节...')
  setTimeout(() => {
    ElMessage.success('优化调节完成，平衡度提升至96%')
    balanceStatus.value = 96
  }, 2000)
}

const executeSingleValve = (row) => {
  ElMessage.success(`阀门 ${row.valve} 已调节至 ${row.suggestedOpen}%`)
}

const previewEffect = (row) => {
  ElMessage.info(`预计调节后流量将${row.adjustment > 0 ? '增加' : '减少'} ${Math.abs(row.adjustment)}%`)
}

const initNetworkChart = () => {
  const chart = echarts.init(networkChartRef.value)
  
  let nodeColor = '#67c23a'
  if (networkView.value === 'pressure') {
    nodeColor = (params) => {
      const pressures = { '换热站': 0.52, '1号楼': 0.42, '2号楼': 0.38, '3号楼': 0.45, '4号楼': 0.33 }
      const p = pressures[params.name] || 0.4
      if (p < 0.35) return '#f56c6c'
      if (p > 0.55) return '#e6a23c'
      return '#67c23a'
    }
  }
  
  const option = {
    tooltip: {},
    series: [{
      type: 'graph',
      layout: 'force',
      force: { repulsion: 200 },
      nodes: [
        { name: '换热站', x: 100, y: 100, symbolSize: 60, itemStyle: { color: '#409eff' }, value: 0.52 },
        { name: '1号楼', x: 200, y: 50, symbolSize: 40, itemStyle: { color: '#67c23a' }, value: 0.42 },
        { name: '2号楼', x: 200, y: 150, symbolSize: 35, itemStyle: { color: '#f56c6c' }, value: 0.38 },
        { name: '3号楼', x: 300, y: 50, symbolSize: 45, itemStyle: { color: '#67c23a' }, value: 0.45 },
        { name: '4号楼', x: 300, y: 150, symbolSize: 30, itemStyle: { color: '#f56c6c' }, value: 0.33 },
        { name: '5号楼', x: 350, y: 100, symbolSize: 45, itemStyle: { color: '#67c23a' }, value: 0.48 }
      ],
      links: [
        { source: '换热站', target: '1号楼' },
        { source: '换热站', target: '2号楼' },
        { source: '1号楼', target: '3号楼' },
        { source: '2号楼', target: '4号楼' },
        { source: '3号楼', target: '5号楼' }
      ],
      roam: true,
      label: { show: true, formatter: '{b}\n{p}' },
      edgeSymbol: ['circle', 'arrow'],
      edgeSymbolSize: [4, 10]
    }]
  }
  chart.setOption(option)
}

const initFlowBalanceChart = () => {
  const chart = echarts.init(flowBalanceChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['实际流量', '设计流量', '偏差'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['1号楼', '2号楼', '3号楼', '4号楼', '5号楼'] },
    yAxis: [
      { type: 'value', name: '流量(t/h)' },
      { type: 'value', name: '偏差(%)' }
    ],
    series: [
      { name: '实际流量', type: 'bar', data: [45, 38, 52, 33, 48] },
      { name: '设计流量', type: 'bar', data: [50, 50, 50, 50, 50], itemStyle: { opacity: 0.5 } },
      { name: '偏差', type: 'line', yAxisIndex: 1, data: [-10, -24, 4, -34, -4], itemStyle: { color: '#f56c6c' } }
    ]
  }
  chart.setOption(option)
}

onMounted(() => {
  initNetworkChart()
  initFlowBalanceChart()
})
</script>

<style scoped>
.network-analysis {
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
