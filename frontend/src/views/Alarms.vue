<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>告警管理</span>
          <el-radio-group v-model="alarmLevel" @change="handleLevelChange">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="critical">严重</el-radio-button>
            <el-radio-button label="warning">警告</el-radio-button>
            <el-radio-button label="info">提示</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      
      <el-table :data="filteredAlarms" style="width: 100%">
        <el-table-column prop="alarmType" label="告警类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getTypeName(row.alarmType) }}</el-tag>
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" :disabled="row.acknowledged" @click="handleAcknowledge(row)">确认</el-button>
            <el-button size="small" type="danger" :disabled="row.acknowledged" @click="handleProcess(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 告警处理对话框 -->
    <el-dialog v-model="showProcessDialog" title="处理告警" width="500px">
      <el-form :model="processForm" label-width="100px">
        <el-form-item label="告警信息">
          <div>{{ processForm.alarmMessage }}</div>
        </el-form-item>
        <el-form-item label="处理方式">
          <el-select v-model="processForm.handleType" style="width: 100%">
            <el-option label="已修复" value="fixed" />
            <el-option label="忽略" value="ignored" />
            <el-option label="转交处理" value="transferred" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input v-model="processForm.remark" type="textarea" :rows="3" placeholder="请输入处理说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showProcessDialog = false">取消</el-button>
        <el-button type="primary" @click="submitProcess">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 当前告警级别筛选
const alarmLevel = ref('all')
// 当前页码
const currentPage = ref(1)
// 每页条数
const pageSize = ref(10)
// 总记录数
const total = ref(3)
// 控制处理对话框显示
const showProcessDialog = ref(false)

// 告警列表数据
const alarms = ref([
  { id: 1, alarmType: 'low_temp', alarmMessage: '1号楼101室温度过低', alarmLevel: 'critical', alarmLevelText: '严重', buildingName: '阳光花园1号楼', alarmValue: 16.5, thresholdValue: 18, createTime: '2026-03-14 10:30:00', acknowledged: false },
  { id: 2, alarmType: 'valve_fail', alarmMessage: '2号阀门通讯中断', alarmLevel: 'warning', alarmLevelText: '警告', buildingName: '阳光花园2号楼', alarmValue: 0, thresholdValue: 1, createTime: '2026-03-14 10:25:00', acknowledged: true },
  { id: 3, alarmType: 'high_temp', alarmMessage: '3号楼501室温度过高', alarmLevel: 'info', alarmLevelText: '提示', buildingName: '商业大厦A座', alarmValue: 25, thresholdValue: 24, createTime: '2026-03-14 09:15:00', acknowledged: false }
])

// 处理表单数据
const processForm = reactive({
  id: null,
  alarmMessage: '',
  handleType: 'fixed',
  remark: ''
})

// 过滤后的告警列表
const filteredAlarms = computed(() => {
  if (alarmLevel.value === 'all') {
    return alarms.value
  }
  return alarms.value.filter(alarm => alarm.alarmLevel === alarmLevel.value)
})

/**
 * 获取告警类型的中文名称
 * @param type 告警类型
 * @returns 中文名称
 */
const getTypeName = (type) => {
  const map = { low_temp: '低温告警', valve_fail: '故障告警', high_temp: '高温告警' }
  return map[type] || type
}

/**
 * 获取告警级别的类型映射
 * @param level 告警级别
 * @return 对应的Element Plus标签类型
 */
const getLevelType = (level) => {
  const map = { critical: 'danger', warning: 'warning', info: 'info' }
  return map[level] || 'info'
}

/**
 * 告警级别筛选变化
 * @param value 选中的级别
 */
const handleLevelChange = (value) => {
  ElMessage.info(`已筛选${value === 'all' ? '全部' : value}级别告警`)
}

/**
 * 确认告警
 * @param row 告警数据行
 */
const handleAcknowledge = (row) => {
  row.acknowledged = true
  ElMessage.success('告警已确认')
}

/**
 * 处理告警
 * @param row 告警数据行
 */
const handleProcess = (row) => {
  processForm.id = row.id
  processForm.alarmMessage = row.alarmMessage
  processForm.handleType = 'fixed'
  processForm.remark = ''
  showProcessDialog.value = true
}

/**
 * 提交告警处理
 */
const submitProcess = () => {
  const alarm = alarms.value.find(a => a.id === processForm.id)
  if (alarm) {
    alarm.acknowledged = true
  }
  showProcessDialog.value = false
  ElMessage.success('告警已处理')
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
