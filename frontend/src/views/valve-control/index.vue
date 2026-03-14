<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>智能电动调节系统</span>
          <el-button type="primary" @click="startAutoControl">启动自动控制</el-button>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>阀门列表</span>
            </template>
            <el-table :data="valves" style="width: 100%" @row-click="selectValve">
              <el-table-column prop="valveName" label="阀门名称" />
              <el-table-column prop="currentOpenRatio" label="当前开度">
                <template #default="{ row }">
                  {{ (row.currentOpenRatio * 100).toFixed(1) }}%
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'running' ? 'success' : 'warning'">
                    {{ row.status === 'running' ? '运行中' : '待机' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="16">
          <el-card shadow="hover">
            <template #header>
              <span>实时监控</span>
            </template>
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="valve-visual">
                  <div class="valve-circle" :style="{ background: getValveColor(selectedValve.currentOpenRatio) }">
                    <span class="valve-value">{{ (selectedValve.currentOpenRatio * 100).toFixed(1) }}%</span>
                  </div>
                  <div class="valve-label">当前开度</div>
                </div>
              </el-col>
              <el-col :span="12">
                <el-descriptions :column="1" border>
                  <el-descriptions-item label="阀门名称">{{ selectedValve.valveName }}</el-descriptions-item>
                  <el-descriptions-item label="控制协议">{{ selectedValve.protocol }}</el-descriptions-item>
                  <el-descriptions-item label="目标开度">{{ (selectedValve.targetOpenRatio * 100).toFixed(1) }}%</el-descriptions-item>
                  <el-descriptions-item label="响应时间">{{ selectedValve.responseTime }}s</el-descriptions-item>
                </el-descriptions>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>模糊PID控制配置</span>
            </template>
            <el-form label-width="100px">
              <el-form-item label="Kp">
                <el-slider v-model="fuzzyPID.kp" :min="0.1" :max="5" :step="0.1" show-input />
              </el-form-item>
              <el-form-item label="Ki">
                <el-slider v-model="fuzzyPID.ki" :min="0.01" :max="1" :step="0.01" show-input />
              </el-form-item>
              <el-form-item label="Kd">
                <el-slider v-model="fuzzyPID.kd" :min="0.01" :max="1" :step="0.01" show-input />
              </el-form-item>
              <el-form-item label="控制周期">
                <el-input-number v-model="fuzzyPID.cycle" :min="100" :max="2000" :step="100" />
                <span style="margin-left: 10px">ms</span>
              </el-form-item>
              <el-form-item label="自学习">
                <el-switch v-model="fuzzyPID.selfLearning" />
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>模糊PID调节曲线</span>
            </template>
            <div ref="pidControlChartRef" style="height: 280px"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <span>控制效果评估</span>
            </template>
            <el-descriptions :column="4" border>
              <el-descriptions-item label="响应时间">2.5s</el-descriptions-item>
              <el-descriptions-item label="超调量">3.2%</el-descriptions-item>
              <el-descriptions-item label="稳态误差">0.1℃</el-descriptions-item>
              <el-descriptions-item label="调节品质">优良</el-descriptions-item>
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
import { ElMessage } from 'element-plus'

const pidControlChartRef = ref(null)

const valves = ref([
  { id: 1, valveName: '1号楼入口调节阀', currentOpenRatio: 0.75, targetOpenRatio: 0.80, status: 'running', protocol: 'Modbus', responseTime: 3.5 },
  { id: 2, valveName: '2号楼入口调节阀', currentOpenRatio: 0.80, targetOpenRatio: 0.78, status: 'running', protocol: 'Modbus', responseTime: 3.5 },
  { id: 3, valveName: '3号楼入口调节阀', currentOpenRatio: 0.70, targetOpenRatio: 0.72, status: 'running', protocol: 'MQTT', responseTime: 2.0 },
  { id: 4, valveName: '4号楼入口调节阀', currentOpenRatio: 0.65, targetOpenRatio: 0.68, status: 'standby', protocol: 'MQTT', responseTime: 2.0 }
])

const selectedValve = ref(valves.value[0])

const fuzzyPID = reactive({
  kp: 1.2,
  ki: 0.3,
  kd: 0.1,
  cycle: 100,
  selfLearning: true
})

const selectValve = (valve) => {
  selectedValve.value = valve
}

const getValveColor = (ratio) => {
  if (ratio >= 0.7) return '#67c23a'
  if (ratio >= 0.5) return '#e6a23c'
  return '#f56c6c'
}

const initPIDControlChart = () => {
  const chart = echarts.init(pidControlChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['测量值', '目标值', '输出'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: Array.from({ length: 30 }, (_, i) => `${i * 100}ms`) },
    yAxis: { type: 'value' },
    series: [
      { name: '测量值', type: 'line', data: [0, 20, 35, 48, 55, 62, 68, 72, 75, 77, 79, 80, 79, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80], smooth: true },
      { name: '目标值', type: 'line', data: Array(30).fill(80), lineStyle: { type: 'dashed' } },
      { name: '输出', type: 'line', data: [100, 95, 85, 70, 60, 50, 45, 40, 35, 30, 25, 20, 15, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], smooth: true }
    ]
  }
  chart.setOption(option)
}

const startAutoControl = () => {
  ElMessage.success('自动控制已启动')
}

onMounted(() => {
  initPIDControlChart()
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

.valve-visual {
  text-align: center;
  padding: 20px;
}

.valve-circle {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  transition: background-color 0.3s;
}

.valve-value {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
}

.valve-label {
  margin-top: 15px;
  font-size: 16px;
  color: #909399;
}
</style>
