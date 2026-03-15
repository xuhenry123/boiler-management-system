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
                <el-button type="primary" size="small">新增设备</el-button>
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
                <el-button type="primary" link size="small">监控</el-button>
                <el-button type="primary" link size="small">维护</el-button>
                <el-button type="danger" link size="small">详情</el-button>
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
              <template #default>
                <el-button type="primary" link size="small">处理</el-button>
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
import { Cpu, CircleCheck, Tools, Warning } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const typeChartRef = ref(null)

const equipmentType = ref('')
const equipmentStatus = ref('')

const stats = ref({
  totalCount: 156,
  runningCount: 142,
  maintenanceCount: 8,
  faultCount: 6
})

const equipmentList = ref([
  { code: 'BLR-001', name: '1号燃气锅炉', type: 'boiler', station: '换热站1', status: 1, efficiency: 92, runtime: 2150 },
  { code: 'BLR-002', name: '2号燃气锅炉', type: 'boiler', station: '换热站1', status: 1, efficiency: 88, runtime: 1980 },
  { code: 'PUMP-001', name: '1号循环泵', type: 'pump', station: '换热站1', status: 1, efficiency: 85, runtime: 3200 },
  { code: 'HE-001', name: '1号板式换热器', type: 'heat_exchanger', station: '换热站1', status: 1, efficiency: 95, runtime: 2800 },
  { code: 'VALVE-001', name: '1号楼调节阀', type: 'valve', station: '换热站1', status: 1, efficiency: 100, runtime: 4500 },
  { code: 'BLR-003', name: '3号燃煤锅炉', type: 'boiler', station: '换热站2', status: 2, efficiency: 75, runtime: 1500 }
])

const maintenanceReminders = ref([
  { equipment: '1号循环泵', maintainType: '定期保养', dueDate: '2026-03-20', daysLeft: 5 },
  { equipment: '2号燃气锅炉', maintainType: '年度检修', dueDate: '2026-03-25', daysLeft: 10 },
  { equipment: '1号板式换热器', maintainType: '清洗维护', dueDate: '2026-03-18', daysLeft: 3 }
])

const getTypeName = (type) => {
  const map = { boiler: '锅炉', pump: '循环泵', heat_exchanger: '换热器', valve: '调节阀' }
  return map[type] || type
}

const initTypeChart = () => {
  const chart = echarts.init(typeChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 12, name: '锅炉' },
        { value: 28, name: '循环泵' },
        { value: 18, name: '换热器' },
        { value: 98, name: '调节阀' }
      ]
    }]
  }
  chart.setOption(option)
}

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
