<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>管网水力仿真系统</span>
          <div>
            <el-button type="primary" @click="startSimulation">运行仿真</el-button>
            <el-button @click="exportResults">导出结果</el-button>
          </div>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card shadow="hover">
            <template #header>
              <span>管网拓扑可视化</span>
            </template>
            <div ref="topologyChartRef" style="height: 400px"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>节点参数</span>
            </template>
            <el-table :data="nodes" style="width: 100%">
              <el-table-column prop="nodeName" label="节点" />
              <el-table-column prop="pressure" label="压力(MPa)">
                <template #default="{ row }">
                  {{ row.pressure.toFixed(3) }}
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" />
            </el-table>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>管段流量分布</span>
            </template>
            <div ref="flowChartRef" style="height: 300px"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>压力分布曲线</span>
            </template>
            <div ref="pressureChartRef" style="height: 300px"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>仿真配置</span>
                <el-button type="primary" size="small" @click="saveConfig">保存配置</el-button>
              </div>
            </template>
            <el-form :model="simConfig" label-width="120px" inline>
              <el-form-item label="仿真类型">
                <el-select v-model="simConfig.type" style="width: 150px">
                  <el-option value="static" label="静态仿真" />
                  <el-option value="dynamic" label="动态仿真" />
                </el-select>
              </el-form-item>
              <el-form-item label="迭代算法">
                <el-select v-model="simConfig.algorithm" style="width: 150px">
                  <el-option value="newton" label="Newton-Raphson" />
                  <el-option value="gradient" label="梯度下降法" />
                </el-select>
              </el-form-item>
              <el-form-item label="收敛精度">
                <el-input-number v-model="simConfig.tolerance" :min="0.0001" :max="0.01" :step="0.0001" />
              </el-form-item>
              <el-form-item label="最大迭代次数">
                <el-input-number v-model="simConfig.maxIterations" :min="50" :max="500" />
              </el-form-item>
              <el-form-item label="动态仿真步长" v-if="simConfig.type === 'dynamic'">
                <el-input-number v-model="simConfig.timeStep" :min="1" :max="60" />
              </el-form-item>
              <el-form-item label="总仿真时长" v-if="simConfig.type === 'dynamic'">
                <el-input-number v-model="simConfig.totalTime" :min="1" :max="24" />
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const topologyChartRef = ref(null)
const flowChartRef = ref(null)
const pressureChartRef = ref(null)

const nodes = ref([
  { nodeName: '热源', pressure: 0.600, type: '热源' },
  { nodeName: '节点1', pressure: 0.550, type: '中继' },
  { nodeName: '节点2', pressure: 0.520, type: '中继' },
  { nodeName: '换热站1', pressure: 0.480, type: '用户' },
  { nodeName: '换热站2', pressure: 0.460, type: '用户' },
  { nodeName: '换热站3', pressure: 0.440, type: '用户' }
])

const simConfig = reactive({
  type: 'static',
  algorithm: 'newton',
  tolerance: 0.001,
  maxIterations: 100,
  timeStep: 5,
  totalTime: 2
})

const initTopologyChart = () => {
  const chart = echarts.init(topologyChartRef.value)
  const option = {
    tooltip: {},
    series: [{
      type: 'graph',
      layout: 'force',
      data: [
        { name: '热源', x: 100, y: 200, symbolSize: 50, itemStyle: { color: '#f56c6c' } },
        { name: '节点1', x: 200, y: 150, symbolSize: 30, itemStyle: { color: '#409eff' } },
        { name: '节点2', x: 200, y: 250, symbolSize: 30, itemStyle: { color: '#409eff' } },
        { name: '换热站1', x: 350, y: 100, symbolSize: 40, itemStyle: { color: '#67c23a' } },
        { name: '换热站2', x: 350, y: 200, symbolSize: 40, itemStyle: { color: '#67c23a' } },
        { name: '换热站3', x: 350, y: 300, symbolSize: 40, itemStyle: { color: '#67c23a' } }
      ],
      links: [
        { source: '热源', target: '节点1' },
        { source: '热源', target: '节点2' },
        { source: '节点1', target: '换热站1' },
        { source: '节点1', target: '换热站2' },
        { source: '节点2', target: '换热站2' },
        { source: '节点2', target: '换热站3' }
      ],
      label: { show: true },
      edgeSymbol: ['circle', 'arrow']
    }]
  }
  chart.setOption(option)
}

const initFlowChart = () => {
  const chart = echarts.init(flowChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['管段1', '管段2', '管段3', '管段4', '管段5', '管段6'] },
    yAxis: { type: 'value', name: '流量(t/h)' },
    series: [{
      type: 'bar',
      data: [120, 85, 95, 75, 60, 55],
      itemStyle: { color: '#409eff' }
    }]
  }
  chart.setOption(option)
}

const initPressureChart = () => {
  const chart = echarts.init(pressureChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['热源', '节点1', '节点2', '换热站1', '换热站2', '换热站3'] },
    yAxis: { type: 'value', name: '压力(MPa)' },
    series: [{
      type: 'line',
      data: [0.600, 0.550, 0.520, 0.480, 0.460, 0.440],
      smooth: true,
      areaStyle: { opacity: 0.3 },
      itemStyle: { color: '#67c23a' }
    }]
  }
  chart.setOption(option)
}

const startSimulation = () => {
  ElMessage.success('仿真计算完成')
}

const exportResults = () => {
  ElMessage.info('正在导出...')
}

const saveConfig = () => {
  ElMessage.success('配置已保存')
}

onMounted(() => {
  initTopologyChart()
  initFlowChart()
  initPressureChart()
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
</style>
