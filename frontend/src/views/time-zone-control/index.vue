<template>
  <div class="time-zone-control">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><Grid /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ zoneInfo.zoneCount }}</div>
              <div class="stat-label">控制区域数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ zoneInfo.scheduleCount }}</div>
              <div class="stat-label">时段策略数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><Sunrise /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ zoneInfo.currentPeriod }}</div>
              <div class="stat-label">当前时段</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="32"><Moon /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ zoneInfo.nightMode }}</div>
              <div class="stat-label">夜间模式</div>
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
              <span>区域配置</span>
              <el-button type="primary" size="small">新增区域</el-button>
            </div>
          </template>
          <el-table :data="zoneList" style="width: 100%">
            <el-table-column prop="name" label="区域名称" />
            <el-table-column prop="type" label="区域类型" width="100">
              <template #default="{ row }">
                <el-tag>{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="buildings" label="包含楼栋" width="100" />
            <el-table-column prop="targetTemp" label="目标温度(°C)" width="100" />
            <el-table-column prop="priority" label="优先级" width="80" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-switch v-model="row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default>
                <el-button type="primary" link size="small">时段</el-button>
                <el-button type="primary" link size="small">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>时段策略配置</span>
              <el-button type="primary" size="small">新增时段</el-button>
            </div>
          </template>
          <el-table :data="scheduleList" style="width: 100%">
            <el-table-column prop="name" label="时段名称" />
            <el-table-column prop="period" label="时间段" width="150" />
            <el-table-column prop="targetTemp" label="目标温度(°C)" width="100" />
            <el-table-column prop="loadFactor" label="负荷系数(%)" width="100" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-switch v-model="row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default>
                <el-button type="primary" link size="small">编辑</el-button>
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
              <span>24小时时段温度曲线</span>
            </div>
          </template>
          <div ref="scheduleCurveRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>特殊日期配置</span>
            </div>
          </template>
          <el-table :data="specialDayList" style="width: 100%">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="strategy" label="使用策略" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-switch v-model="row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default>
                <el-button type="primary" link size="small">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>区域联动控制</span>
            </div>
          </template>
          <el-form label-width="100px">
            <el-form-item label="联动模式">
              <el-radio-group v-model="linkageConfig.mode">
                <el-radio label="sequential">顺序启动</el-radio>
                <el-radio label="parallel">并行启动</el-radio>
                <el-radio label="priority">优先保障</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="启动间隔">
              <el-input-number v-model="linkageConfig.interval" :min="0" :max="30" />
              <span style="margin-left: 10px">分钟</span>
            </el-form-item>
            <el-form-item label="启用联动">
              <el-switch v-model="linkageConfig.enabled" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Grid, Timer, Sunrise, Moon } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const scheduleCurveRef = ref(null)

const zoneInfo = ref({
  zoneCount: 8,
  scheduleCount: 5,
  currentPeriod: '上午正常段',
  nightMode: '开启'
})

const zoneList = ref([
  { name: '住宅区A', type: '住宅', buildings: '1-3#', targetTemp: 20, priority: 1, status: true },
  { name: '住宅区B', type: '住宅', buildings: '4-6#', targetTemp: 20, priority: 2, status: true },
  { name: '商业区', type: '商业', buildings: '7#', targetTemp: 18, priority: 3, status: true }
])

const scheduleList = ref([
  { name: '早高峰', period: '06:00-09:00', targetTemp: 20, loadFactor: 100, status: true },
  { name: '上午正常', period: '09:00-12:00', targetTemp: 18, loadFactor: 80, status: true },
  { name: '下午正常', period: '12:00-17:00', targetTemp: 18, loadFactor: 80, status: true },
  { name: '晚高峰', period: '17:00-21:00', targetTemp: 20, loadFactor: 100, status: true },
  { name: '夜间低谷', period: '21:00-06:00', targetTemp: 16, loadFactor: 60, status: true }
])

const specialDayList = ref([
  { date: '2026-01-01', type: '元旦', strategy: '节假日模式', status: false },
  { date: '2026-02-11', type: '春节', strategy: '春节模式', status: false },
  { date: '2026-12-25', type: '元旦', strategy: '节假日模式', status: false }
])

const linkageConfig = ref({
  mode: 'sequential',
  interval: 5,
  enabled: true
})

const initScheduleCurve = () => {
  const chart = echarts.init(scheduleCurveRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00']
    },
    yAxis: { type: 'value', name: '温度(°C)' },
    series: [{
      type: 'line',
      smooth: true,
      data: [16, 16, 20, 18, 20, 20, 16],
      areaStyle: { opacity: 0.3 }
    }]
  }
  chart.setOption(option)
}

onMounted(() => {
  initScheduleCurve()
})
</script>

<style scoped>
.time-zone-control {
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
