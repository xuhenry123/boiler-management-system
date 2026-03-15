<template>
  <div class="climate-compensation">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><Sunny /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ currentConfig }}</div>
              <div class="stat-label">当前补偿模式</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><Temperature /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ outdoorTemp }}°C</div>
              <div class="stat-label">室外温度</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ supplyTemp }}°C</div>
              <div class="stat-label">当前供水温度</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="32"><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ savingsRate }}%</div>
              <div class="stat-label">节能率</div>
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
              <span>气候补偿曲线</span>
              <el-button type="primary" size="small">编辑曲线</el-button>
            </div>
          </template>
          <div ref="compensationCurveRef" style="height: 400px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>补偿策略配置</span>
            </div>
          </template>
          <el-form label-width="100px">
            <el-form-item label="补偿模式">
              <el-select v-model="compensationMode" style="width: 100%">
                <el-option label="固定温度" value="fixed" />
                <el-option label="气候补偿" value="climate" />
                <el-option label="时间补偿" value="time" />
                <el-option label="智能补偿" value="smart" />
              </el-select>
            </el-form-item>
            <el-form-item label="补偿曲线">
              <el-select v-model="selectedCurve" style="width: 100%">
                <el-option label="标准曲线" value="standard" />
                <el-option label="节能曲线" value="energy_saving" />
                <el-option label="舒适曲线" value="comfort" />
              </el-select>
            </el-form-item>
            <el-form-item label="室内温度">
              <el-input-number v-model="indoorTarget" :min="16" :max="24" :step="0.5" />
              <span style="margin-left: 10px">°C</span>
            </el-form-item>
            <el-form-item label="最低室外温度">
              <el-input-number v-model="minOutdoorTemp" :min="-30" :max="0" :step="1" />
              <span style="margin-left: 10px">°C</span>
            </el-form-item>
            <el-form-item label="最高室外温度">
              <el-input-number v-model="maxOutdoorTemp" :min="0" :max="20" :step="1" />
              <span style="margin-left: 10px">°C</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveConfig">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>补偿效果对比</span>
            </div>
          </template>
          <div ref="effectCompareRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>补偿策略列表</span>
              <el-button type="primary" size="small">新增策略</el-button>
            </div>
          </template>
          <el-table :data="strategyList" style="width: 100%">
            <el-table-column prop="name" label="策略名称" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag>{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="tempRange" label="温度范围" width="120" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-switch v-model="row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default>
                <el-button type="primary" link size="small">编辑</el-button>
                <el-button type="danger" link size="small">删除</el-button>
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
import { Sunny, Temperature, TrendCharts, DataLine } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const compensationCurveRef = ref(null)
const effectCompareRef = ref(null)

const currentConfig = ref('智能补偿')
const outdoorTemp = ref(-5)
const supplyTemp = ref(52)
const savingsRate = ref(12.5)

const compensationMode = ref('climate')
const selectedCurve = ref('standard')
const indoorTarget = ref(20)
const minOutdoorTemp = ref(-20)
const maxOutdoorTemp = ref(10)

const strategyList = ref([
  { name: '标准补偿', type: '气候', tempRange: '-20~10°C', status: true },
  { name: '节能模式', type: '气候', tempRange: '-15~5°C', status: true },
  { name: '舒适模式', type: '气候', tempRange: '-10~15°C', status: false }
])

const saveConfig = () => {
  console.log('保存配置')
}

const initCompensationCurve = () => {
  const chart = echarts.init(compensationCurveRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['供水温度', '回水温度'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['-20', '-15', '-10', '-5', '0', '5', '10'],
      name: '室外温度(°C)'
    },
    yAxis: { type: 'value', name: '温度(°C)' },
    series: [
      {
        name: '供水温度',
        type: 'line',
        smooth: true,
        data: [75, 68, 60, 52, 45, 40, 35]
      },
      {
        name: '回水温度',
        type: 'line',
        smooth: true,
        data: [55, 50, 44, 38, 33, 30, 28]
      }
    ]
  }
  chart.setOption(option)
}

const initEffectCompare = () => {
  const chart = echarts.init(effectCompareRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['补偿前', '补偿后'] },
    xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
    yAxis: { type: 'value', name: '能耗(tce)' },
    series: [
      { name: '补偿前', type: 'bar', data: [52, 48, 55, 50, 45, 42, 46] },
      { name: '补偿后', type: 'bar', data: [45, 42, 48, 44, 40, 37, 40] }
    ]
  }
  chart.setOption(option)
}

onMounted(() => {
  initCompensationCurve()
  initEffectCompare()
})
</script>

<style scoped>
.climate-compensation {
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
