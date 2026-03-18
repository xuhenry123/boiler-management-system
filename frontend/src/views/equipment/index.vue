<template>
  <div class="equipment">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><Cpu /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCount }}</div>
              <div class="stat-label">设备总数</div>
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
              <div class="stat-value">{{ stats.runningCount }}</div>
              <div class="stat-label">运行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><Tools /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.maintenanceCount }}</div>
              <div class="stat-label">维护中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="32"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.faultCount }}</div>
              <div class="stat-label">故障中</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>设备列表</span>
              <div>
                <el-select v-model="equipmentType" placeholder="设备类型" size="small" style="width: 120px; margin-right: 10px">
                  <el-option label="全部类型" value="" />
                  <el-option label="锅炉" value="boiler" />
                  <el-option label="循环泵" value="pump" />
                  <el-option label="换热器" value="heat_exchanger" />
                  <el-option label="调节阀" value="valve" />
                </el-select>
                <el-select v-model="equipmentStatus" placeholder="运行状态" size="small" style="width: 120px; margin-right: 10px">
                  <el-option label="全部状态" value="" />
                  <el-option label="运行中" value="1" />
                  <el-option label="停运" value="0" />
                  <el-option label="维修" value="2" />
                </el-select>
                <el-button type="primary" size="small" @click="handleAdd">
                  <el-icon><Plus /></el-icon>新增设备
                </el-button>
              </div>
            </div>
          </template>
          <el-table :data="equipmentList" style="width: 100%">
            <el-table-column prop="code" label="设备编码" width="120" />
            <el-table-column prop="name" label="设备名称" />
            <el-table-column prop="type" label="设备类型" width="100">
              <template #default="{ row }">
                <el-tag>{{ getTypeName(row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="station" label="所属换热站" width="120" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'danger'">
                  {{ row.status === 1 ? '运行' : row.status === 2 ? '维修' : '停运' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="efficiency" label="效率(%)" width="100">
              <template #default="{ row }">
                <el-progress :percentage="row.efficiency" :stroke-width="8" />
              </template>
            </el-table-column>
            <el-table-column prop="runtime" label="运行时长(h)" width="100" />
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="handleMonitor(row)">监控</el-button>
                <el-button type="primary" link size="small" @click="handleMaintain(row)">维护</el-button>
                <el-button type="primary" link size="small" @click="handleViewDetail(row)">详情</el-button>
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
              <span>设备类型分布</span>
            </div>
          </template>
          <div ref="typeChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>维护提醒</span>
            </div>
          </template>
          <el-table :data="maintenanceReminders" style="width: 100%">
            <el-table-column prop="equipment" label="设备" />
            <el-table-column prop="maintainType" label="维护类型" width="100" />
            <el-table-column prop="dueDate" label="到期日期" width="120" />
            <el-table-column prop="daysLeft" label="剩余天数" width="100">
              <template #default="{ row }">
                <span :style="{ color: row.daysLeft <= 7 ? '#f56c6c' : '#303133' }">
                  {{ row.daysLeft }}天
                </span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="handleMaintainReminder(row)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Cpu, CircleCheck, Tools, Warning, Plus } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { equipmentApi, stationApi } from '@/api'

// 图表DOM引用
const typeChartRef = ref(null)

// 筛选条件
const equipmentType = ref('')
const equipmentStatus = ref('')

// 统计数据
const stats = ref({
  totalCount: 0,
  runningCount: 0,
  maintenanceCount: 0,
  faultCount: 0
})

// 换热站列表
const stationList = ref([])

// 设备列表数据
const equipmentList = ref([])

// 维护提醒列表
const maintenanceReminders = ref([])

/**
 * 加载维护提醒数据
 */
const loadMaintenanceReminders = async () => {
  try {
    const result = await equipmentApi.getMaintenanceReminders()
    maintenanceReminders.value = result || []
  } catch (error) {
    console.error('加载维护提醒失败:', error)
    maintenanceReminders.value = [
      { id: 1, equipment: '1号循环泵', maintainType: '定期保养', dueDate: '2026-03-20', daysLeft: 5 },
      { id: 2, equipment: '2号燃气锅炉', maintainType: '年度检修', dueDate: '2026-03-25', daysLeft: 10 },
      { id: 3, equipment: '1号板式换热器', maintainType: '清洗维护', dueDate: '2026-03-18', daysLeft: 3 }
    ]
  }
}

// 过滤后的设备列表
const filteredEquipment = computed(() => {
  return equipmentList.value.filter(item => {
    const typeMatch = !equipmentType.value || item.type === equipmentType.value
    const statusMatch = !equipmentStatus.value || item.status === parseInt(equipmentStatus.value)
    return typeMatch && statusMatch
  })
})

/**
 * 加载设备数据
 */
const loadEquipment = async () => {
  try {
    const result = await equipmentApi.getEquipment()
    equipmentList.value = result.data || []
    
    // 更新统计数据
    stats.value.totalCount = equipmentList.value.length
    stats.value.runningCount = equipmentList.value.filter(e => e.status === 1).length
    stats.value.maintenanceCount = equipmentList.value.filter(e => e.status === 2).length
    stats.value.faultCount = equipmentList.value.filter(e => e.status === 0).length
  } catch (error) {
    console.error('加载设备数据失败:', error)
  }
}

/**
 * 加载换热站数据
 */
const loadStations = async () => {
  try {
    const result = await stationApi.getStations()
    stationList.value = result.data || []
  } catch (error) {
    console.error('加载换热站数据失败:', error)
  }
}

// 组件挂载完成后加载数据
onMounted(() => {
  loadEquipment()
  loadStations()
  loadMaintenanceReminders()
})

/**
 * 获取设备类型的中文名称
 * @param type 设备类型
 * @returns 中文名称
 */
const getTypeName = (type) => {
  const map = { boiler: '锅炉', pump: '循环泵', heat_exchanger: '换热器', valve: '调节阀' }
  return map[type] || type
}

/**
 * 新增设备
 */
const handleAdd = () => {
  ElMessage.info('打开新增设备对话框')
}

/**
 * 监控设备
 * @param row 设备数据行
 */
const handleMonitor = (row) => {
  ElMessage.info(`监控设备: ${row.name}`)
}

/**
 * 设备维护
 * @param row 设备数据行
 */
const handleMaintain = (row) => {
  ElMessage.info(`设备维护: ${row.name}`)
}

/**
 * 查看设备详情
 * @param row 设备数据行
 */
const handleViewDetail = (row) => {
  ElMessage.info(`查看设备详情: ${row.name}`)
}

/**
 * 处理维护提醒
 * @param row 维护提醒数据行
 */
const handleMaintainReminder = (row) => {
  ElMessageBox.confirm(`确定处理"${row.equipment}"的维护提醒吗？`, '提示', { type: 'info' }).then(() => {
    maintenanceReminders.value = maintenanceReminders.value.filter(r => r.id !== row.id)
    ElMessage.success('已处理维护提醒')
  })
}

/**
 * 初始化设备类型分布图表
 */
const initTypeChart = async () => {
  if (!typeChartRef.value) return
  const chart = echarts.init(typeChartRef.value)
  
  let chartData = []
  try {
    const result = await equipmentApi.getTypeDistribution()
    chartData = result || []
  } catch (error) {
    console.error('获取设备类型分布失败:', error)
    chartData = [
      { value: 12, name: '锅炉' },
      { value: 28, name: '循环泵' },
      { value: 18, name: '换热器' },
      { value: 98, name: '调节阀' }
    ]
  }
  
  const option = {
    title: { text: '设备类型分布', left: 'center' },
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '45%'],
      data: chartData.length > 0 ? chartData : [
        { value: 12, name: '锅炉' },
        { value: 28, name: '循环泵' },
        { value: 18, name: '换热器' },
        { value: 98, name: '调节阀' }
      ]
    }]
  }
  chart.setOption(option)
}

// 组件挂载完成后初始化图表
onMounted(() => {
  initTypeChart()
})
</script>

<style scoped>
.equipment {
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
