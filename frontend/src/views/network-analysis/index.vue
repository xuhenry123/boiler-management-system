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
              <el-select v-model="selectedStation" placeholder="选择换热站" size="small">
                <el-option v-for="item in stations" :key="item" :label="item" :value="item" />
              </el-select>
            </div>
          </template>
          <div ref="networkChartRef" style="height: 400px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>节点压力分布</span>
            </div>
          </template>
          <el-table :data="nodePressureData" style="width: 100%">
            <el-table-column prop="nodeName" label="节点" />
            <el-table-column prop="pressure" label="压力(MPa)" width="100">
              <template #default="{ row }">
                <span :style="{ color: row.pressure < 0.35 ? '#f56c6c' : '#303133' }">
                  {{ row.pressure }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === '正常' ? 'success' : 'danger'" size="small">
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
            <div class="card-header">
              <span>流量平衡分析</span>
            </div>
          </template>
          <div ref="flowBalanceChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>管网诊断结果</span>
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
              <el-button type="primary" size="small">执行优化</el-button>
            </div>
          </template>
          <el-table :data="optimizationSuggestions" style="width: 100%">
            <el-table-column prop="valve" label="阀门位置" width="150" />
            <el-table-column prop="currentOpen" label="当前开度(%)" width="120" />
            <el-table-column prop="suggestedOpen" label="建议开度(%)" width="120" />
            <el-table-column prop="adjustment" label="调节幅度(%)" width="100" />
            <el-table-column prop="expectedEffect" label="预期效果" />
            <el-table-column prop="priority" label="优先级" width="80">
              <template #default="{ row }">
                <el-tag :type="row.priority === '高' ? 'danger' : row.priority === '中' ? 'warning' : 'info'">
                  {{ row.priority }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default>
                <el-button type="primary" link size="small">执行</el-button>
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
import { Connection, DataLine, PieChart, Warning } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const networkChartRef = ref(null)
const flowBalanceChartRef = ref(null)

const selectedStation = ref('')
const stations = ref(['换热站1', '换热站2', '换热站3'])

const networkInfo = ref({
  nodeCount: 48,
  segmentCount: 56
})

const balanceStatus = ref(92.5)
const diagnosisStatus = ref('正常')

const nodePressureData = ref([
  { nodeName: '换热站', pressure: 0.52, status: '正常' },
  { nodeName: '1号楼', pressure: 0.42, status: '正常' },
  { nodeName: '2号楼', pressure: 0.38, status: '偏低' },
  { nodeName: '3号楼', pressure: 0.45, status: '正常' },
  { nodeName: '4号楼', pressure: 0.33, status: '偏低' }
])

const diagnosisResults = ref([
  { type: '平衡诊断', location: '2号楼支路', severity: '一般', suggestion: '增大阀门开度5%' },
  { type: '阻力诊断', location: '4号楼入口', severity: '严重', suggestion: '检查管道是否堵塞' }
])

const optimizationSuggestions = ref([
  { valve: '2号楼入口阀', currentOpen: 65, suggestedOpen: 72, adjustment: 7, expectedEffect: '流量增加5%', priority: '高' },
  { valve: '3号楼入口阀', currentOpen: 70, suggestedOpen: 68, adjustment: -2, expectedEffect: '流量减少2%', priority: '中' },
  { valve: '4号楼入口阀', currentOpen: 55, suggestedOpen: 80, adjustment: 25, expectedEffect: '流量增加15%', priority: '高' }
])

const initNetworkChart = () => {
  const chart = echarts.init(networkChartRef.value)
  const option = {
    tooltip: {},
    series: [{
      type: 'graph',
      layout: 'force',
      nodes: [
        { name: '换热站', x: 100, y: 100, symbolSize: 50, itemStyle: { color: '#409eff' } },
        { name: '1号楼', x: 200, y: 50, symbolSize: 30 },
        { name: '2号楼', x: 200, y: 150, symbolSize: 30 },
        { name: '3号楼', x: 300, y: 50, symbolSize: 30 },
        { name: '4号楼', x: 300, y: 150, symbolSize: 30 }
      ],
      links: [
        { source: '换热站', target: '1号楼' },
        { source: '换热站', target: '2号楼' },
        { source: '1号楼', target: '3号楼' },
        { source: '2号楼', target: '4号楼' }
      ],
      roam: true,
      label: { show: true }
    }]
  }
  chart.setOption(option)
}

const initFlowBalanceChart = () => {
  const chart = echarts.init(flowBalanceChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['实际流量', '设计流量'] },
    xAxis: { type: 'category', data: ['1号楼', '2号楼', '3号楼', '4号楼'] },
    yAxis: { type: 'value', name: '流量(t/h)' },
    series: [
      { name: '实际流量', type: 'bar', data: [45, 38, 52, 33] },
      { name: '设计流量', type: 'bar', data: [50, 50, 50, 50], itemStyle: { opacity: 0.5 } }
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
