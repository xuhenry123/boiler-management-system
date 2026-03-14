<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>一网智能控制策略</span>
          <el-button type="primary" @click="startOptimization">重新优化</el-button>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card shadow="hover">
            <template #header>
              <span>一次管网流向图</span>
            </template>
            <div ref="flowChartRef" style="height: 380px"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>热源分布</span>
            </template>
            <el-table :data="heatSources" style="width: 100%">
              <el-table-column prop="name" label="热源" />
              <el-table-column prop="capacity" label="容量(MW)" />
              <el-table-column prop="load" label="当前负荷(MW)" />
              <el-table-column prop="status" label="状态">
                <template #default>
                  <el-tag type="success">运行</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>模糊控制规则</span>
            </template>
            <el-table :data="fuzzyRules" style="width: 100%">
              <el-table-column prop="outdoorTemp" label="室外温度" />
              <el-table-column prop="loadChange" label="负荷变化" />
              <el-table-column prop="supplyTemp" label="供水温度调整" />
              <el-table-column prop="flowAdjust" label="流量调整" />
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>控制策略配置</span>
            </template>
            <el-form label-width="120px">
              <el-form-item label="控制模式">
                <el-select v-model="controlConfig.mode">
                  <el-option value="fuzzy" label="模糊控制" />
                  <el-option value="pid" label="PID控制" />
                  <el-option value="adaptive" label="自适应控制" />
                </el-select>
              </el-form-item>
              <el-form-item label="响应周期">
                <el-input-number v-model="controlConfig.cycle" :min="10" :max="300" />
                <span style="margin-left: 10px">秒</span>
              </el-form-item>
              <el-form-item label="负荷预测">
                <el-switch v-model="controlConfig.predict" />
              </el-form-item>
              <el-form-item label="预测时长">
                <el-input-number v-model="controlConfig.predictHours" :min="1" :max="24" :disabled="!controlConfig.predict" />
                <span style="margin-left: 10px">小时</span>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <span>调度优化结果</span>
            </template>
            <div ref="optimizeChartRef" style="height: 300px"></div>
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

const flowChartRef = ref(null)
const optimizeChartRef = ref(null)

const heatSources = ref([
  { name: '1号锅炉', capacity: 10, load: 8.5 },
  { name: '2号锅炉', capacity: 10, load: 7.2 },
  { name: '3号锅炉', capacity: 15, load: 0 }
])

const fuzzyRules = ref([
  { outdoorTemp: '< -10℃', loadChange: '高', supplyTemp: '+5℃', flowAdjust: '+15%' },
  { outdoorTemp: '-10 ~ -5℃', loadChange: '中高', supplyTemp: '+3℃', flowAdjust: '+10%' },
  { outdoorTemp: '-5 ~ 0℃', loadChange: '中', supplyTemp: '不变', flowAdjust: '不变' },
  { outdoorTemp: '0 ~ 5℃', loadChange: '低', supplyTemp: '-2℃', flowAdjust: '-5%' },
  { outdoorTemp: '> 5℃', loadChange: '很低', supplyTemp: '-4℃', flowAdjust: '-10%' }
])

const controlConfig = reactive({
  mode: 'fuzzy',
  cycle: 30,
  predict: true,
  predictHours: 4
})

const initFlowChart = () => {
  const chart = echarts.init(flowChartRef.value)
  const option = {
    tooltip: {},
    series: [{
      type: 'graph',
      layout: 'force',
      data: [
        { name: '热源', x: 50, y: 50, symbolSize: 60, itemStyle: { color: '#f56c6c' } },
        { name: '换热站1', symbolSize: 40, itemStyle: { color: '#409eff' } },
        { name: '换热站2', symbolSize: 40, itemStyle: { color: '#409eff' } },
        { name: '换热站3', symbolSize: 40, itemStyle: { color: '#409eff' } }
      ],
      links: [
        { source: '热源', target: '换热站1', value: '50MW' },
        { source: '热源', target: '换热站2', value: '40MW' },
        { source: '热源', target: '换热站3', value: '60MW' }
      ],
      label: { show: true },
      edgeSymbol: ['circle', 'arrow'],
      edgeSymbolSize: [4, 10]
    }]
  }
  chart.setOption(option)
}

const initOptimizeChart = () => {
  const chart = echarts.init(optimizeChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['实际负荷', '优化负荷', '预测负荷'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'] },
    yAxis: { type: 'value', name: '负荷(MW)' },
    series: [
      { name: '实际负荷', type: 'line', data: [45, 42, 55, 60, 58, 52], smooth: true },
      { name: '优化负荷', type: 'line', data: [43, 41, 53, 58, 56, 50], smooth: true, lineStyle: { type: 'dashed' } },
      { name: '预测负荷', type: 'line', data: [null, null, null, 55, 54, 50], smooth: true, lineStyle: { type: 'dotted' } }
    ]
  }
  chart.setOption(option)
}

const startOptimization = () => {
  ElMessage.success('优化计算完成')
  initOptimizeChart()
}

onMounted(() => {
  initFlowChart()
  initOptimizeChart()
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
