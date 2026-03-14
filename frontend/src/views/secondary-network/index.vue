<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>二网平衡智能优化</span>
          <div>
            <el-select v-model="selectedStation" placeholder="选择换热站" style="width: 200px; margin-right: 10px">
              <el-option v-for="station in stations" :key="station.id" :label="station.name" :value="station.id" />
            </el-select>
            <el-button type="primary" @click="startOptimization">开始优化</el-button>
          </div>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>管网拓扑结构</span>
            </template>
            <div ref="networkChartRef" style="height: 350px"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>阀门开度状态</span>
            </template>
            <el-table :data="valveData" style="width: 100%">
              <el-table-column prop="valveName" label="阀门名称" />
              <el-table-column prop="buildingName" label="建筑物" />
              <el-table-column prop="currentOpenRatio" label="当前开度">
                <template #default="{ row }">
                  <el-progress :percentage="row.currentOpenRatio * 100" :color="getProgressColor(row.currentOpenRatio)" />
                </template>
              </el-table-column>
              <el-table-column prop="targetOpenRatio" label="目标开度">
                <template #default="{ row }">
                  <el-progress :percentage="row.targetOpenRatio * 100" :color="'#67c23a'" />
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'optimal' ? 'success' : 'warning'">{{ row.statusText }}</el-tag>
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
                <span>优化历史记录</span>
                <el-select v-model="algorithmType" style="width: 150px">
                  <el-option value="ga" label="遗传算法" />
                  <el-option value="pso" label="粒子群优化" />
                </el-select>
              </div>
            </template>
            <div ref="optimizationChartRef" style="height: 300px"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>优化参数配置</span>
            </template>
            <el-form label-width="120px">
              <el-form-item label="种群规模">
                <el-input-number v-model="gaConfig.populationSize" :min="20" :max="100" />
              </el-form-item>
              <el-form-item label="迭代次数">
                <el-input-number v-model="gaConfig.maxGenerations" :min="50" :max="500" />
              </el-form-item>
              <el-form-item label="交叉率">
                <el-input-number v-model="gaConfig.crossoverRate" :min="0.5" :max="1" :step="0.1" />
              </el-form-item>
              <el-form-item label="变异率">
                <el-input-number v-model="gaConfig.mutationRate" :min="0.01" :max="0.2" :step="0.01" />
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
        <el-col :span="16">
          <el-card shadow="hover">
            <template #header>
              <span>优化结果统计</span>
            </template>
            <el-descriptions :column="3" border>
              <el-descriptions-item label="优化算法">{{ algorithmType === 'ga' ? '遗传算法' : '粒子群优化' }}</el-descriptions-item>
              <el-descriptions-item label="迭代次数">100</el-descriptions-item>
              <el-descriptions-item label="执行时间">150ms</el-descriptions-item>
              <el-descriptions-item label="优化前偏差">15.6%</el-descriptions-item>
              <el-descriptions-item label="优化后偏差">2.3%</el-descriptions-item>
              <el-descriptions-item label="节能效果">12.5%</el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage, ElLoading } from 'element-plus'

const networkChartRef = ref(null)
const optimizationChartRef = ref(null)

const selectedStation = ref(1)
const algorithmType = ref('ga')

const stations = ref([
  { id: 1, name: '东城区换热站' },
  { id: 2, name: '西城区换热站' },
  { id: 3, name: '朝阳区换热站' }
])

const valveData = ref([
  { valveName: '1号楼入口调节阀', buildingName: '阳光花园1号楼', currentOpenRatio: 0.75, targetOpenRatio: 0.80, status: 'optimal', statusText: '已优化' },
  { valveName: '2号楼入口调节阀', buildingName: '阳光花园2号楼', currentOpenRatio: 0.80, targetOpenRatio: 0.78, status: 'optimal', statusText: '已优化' },
  { valveName: '3号楼入口调节阀', buildingName: '商业大厦A座', currentOpenRatio: 0.70, targetOpenRatio: 0.72, status: 'optimal', statusText: '已优化' },
  { valveName: '4号楼入口调节阀', buildingName: '科技园区办公楼', currentOpenRatio: 0.65, targetOpenRatio: 0.68, status: 'optimizing', statusText: '待优化' }
])

const gaConfig = reactive({
  populationSize: 50,
  maxGenerations: 100,
  crossoverRate: 0.8,
  mutationRate: 0.05
})

const getProgressColor = (ratio) => {
  if (ratio >= 0.7) return '#67c23a'
  if (ratio >= 0.5) return '#e6a23c'
  return '#f56c6c'
}

const initNetworkChart = () => {
  const chart = echarts.init(networkChartRef.value)
  const option = {
    tooltip: {},
    series: [{
      type: 'graph',
      layout: 'force',
      data: [
        { name: '换热站', x: 100, y: 100, symbolSize: 50, itemStyle: { color: '#409eff' } },
        { name: '1号楼', symbolSize: 30 },
        { name: '2号楼', symbolSize: 30 },
        { name: '3号楼', symbolSize: 30 },
        { name: '4号楼', symbolSize: 30 }
      ],
      links: [
        { source: '换热站', target: '1号楼', label: '80%' },
        { source: '换热站', target: '2号楼', label: '78%' },
        { source: '换热站', target: '3号楼', label: '72%' },
        { source: '换热站', target: '4号楼', label: '68%' }
      ],
      label: { show: true }
    }]
  }
  chart.setOption(option)
}

const initOptimizationChart = () => {
  const chart = echarts.init(optimizationChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: Array.from({ length: 20 }, (_, i) => `第${i + 1}次`) },
    yAxis: { type: 'value', name: '目标函数值' },
    series: [{
      type: 'line',
      data: [100, 85, 72, 60, 52, 45, 40, 35, 32, 28, 25, 22, 20, 18, 16, 14, 13, 12, 11, 10],
      smooth: true,
      areaStyle: { opacity: 0.3 }
    }]
  }
  chart.setOption(option)
}

const startOptimization = async () => {
  const loading = ElLoading.service({ lock: true, text: '优化计算中...' })
  
  setTimeout(() => {
    loading.close()
    ElMessage.success('优化完成！')
    valveData.value.forEach(valve => {
      valve.status = 'optimal'
      valve.statusText = '已优化'
    })
    initNetworkChart()
  }, 1500)
}

onMounted(() => {
  initNetworkChart()
  initOptimizationChart()
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
