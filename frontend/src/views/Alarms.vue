<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>告警管理</span>
          <el-radio-group v-model="alarmLevel">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="critical">严重</el-radio-button>
            <el-radio-button label="warning">警告</el-radio-button>
            <el-radio-button label="info">提示</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      
      <el-table :data="alarms" style="width: 100%">
        <el-table-column prop="alarmType" label="告警类型" width="120" />
        <el-table-column prop="alarmMessage" label="告警信息" />
        <el-table-column prop="alarmLevel" label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.alarmLevel)">{{ row.alarmLevelText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="buildingName" label="建筑物" width="150" />
        <el-table-column prop="alarmValue" label="告警值" width="100" />
        <el-table-column prop="thresholdValue" label="阈值" width="100" />
        <el-table-column prop="createTime" label="发生时间" width="180" />
        <el-table-column prop="acknowledged" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.acknowledged ? 'success' : 'warning'">
              {{ row.acknowledged ? '已确认' : '未确认' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" :disabled="row.acknowledged" @click="acknowledge(row)">确认</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const alarmLevel = ref('all')

const alarms = ref([
  { id: 1, alarmType: 'low_temp', alarmMessage: '1号楼101室温度过低', alarmLevel: 'critical', alarmLevelText: '严重', buildingName: '阳光花园1号楼', alarmValue: 16.5, thresholdValue: 18, createTime: '2026-03-14 10:30:00', acknowledged: false },
  { id: 2, alarmType: 'valve_fail', alarmMessage: '2号阀门通讯中断', alarmLevel: 'warning', alarmLevelText: '警告', buildingName: '阳光花园2号楼', alarmValue: 0, thresholdValue: 1, createTime: '2026-03-14 10:25:00', acknowledged: true },
  { id: 3, alarmType: 'high_temp', alarmMessage: '3号楼501室温度过高', alarmLevel: 'info', alarmMessage: '提示', buildingName: '商业大厦A座', alarmValue: 25, thresholdValue: 24, createTime: '2026-03-14 09:15:00', acknowledged: false }
])

const getLevelType = (level) => {
  const map = { critical: 'danger', warning: 'warning', info: 'info' }
  return map[level] || 'info'
}

const acknowledge = (row) => {
  row.acknowledged = true
  ElMessage.success('告警已确认')
}
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
