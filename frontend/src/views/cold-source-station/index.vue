<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>冷源站管理</span>
          <el-button type="primary" @click="showDialog = true">
            <el-icon><Plus /></el-icon>新增冷源站
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="stationCode" label="站点编号" width="120" />
        <el-table-column prop="stationName" label="站点名称" />
        <el-table-column prop="stationType" label="站点类型">
          <template #default="{ row }">
            <el-tag type="info">{{ getTypeLabel(row.stationType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="capacity" label="设计冷量(MW)" />
        <el-table-column prop="deviceCount" label="设备数量" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-switch v-model="row.status" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="showDialog" :title="editingId ? '编辑冷源站' : '新增冷源站'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="站点编号">
          <el-input v-model="form.stationCode" placeholder="请输入站点编号" />
        </el-form-item>
        <el-form-item label="站点名称">
          <el-input v-model="form.stationName" placeholder="请输入站点名称" />
        </el-form-item>
        <el-form-item label="站点类型">
          <el-select v-model="form.stationType" placeholder="请选择站点类型" style="width: 100%">
            <el-option label="冷水机组" value="chiller" />
            <el-option label="溴化锂机组" value="absorption" />
            <el-option label="冷却塔" value="cooling_tower" />
            <el-option label="热泵机组" value="heat_pump" />
            <el-option label="冰蓄冷" value="ice_storage" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="设计冷量(MW)">
          <el-input-number v-model="form.capacity" :min="0" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="设计压力(MPa)">
          <el-input-number v-model="form.designPressure" :min="0" :step="0.1" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="投运日期">
          <el-date-picker v-model="form.commissionDate" type="date" placeholder="选择投运日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([
  { id: 1, stationCode: 'CL001', stationName: '东区冷站', stationType: 'chiller', address: '东区工业园', capacity: 40, deviceCount: 3, status: true },
  { id: 2, stationCode: 'CL002', stationName: '西区溴化锂站', stationType: 'absorption', address: '西区工业园', capacity: 30, deviceCount: 2, status: true },
  { id: 3, stationCode: 'CL003', stationName: '南区冷却塔', stationType: 'cooling_tower', address: '南区商业区', capacity: 25, deviceCount: 4, status: true },
  { id: 4, stationCode: 'CL004', stationName: '北区冰蓄冷站', stationType: 'ice_storage', address: '北区住宅区', capacity: 15, deviceCount: 2, status: false }
])

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(4)
const showDialog = ref(false)
const editingId = ref(null)

const form = reactive({
  stationCode: '',
  stationName: '',
  stationType: '',
  address: '',
  capacity: 0,
  designPressure: 0.8,
  commissionDate: null,
  remark: ''
})

const getTypeLabel = (type) => {
  const map = { 
    chiller: '冷水机组', 
    absorption: '溴化锂机组', 
    cooling_tower: '冷却塔', 
    heat_pump: '热泵机组',
    ice_storage: '冰蓄冷'
  }
  return map[type] || type
}

const handleView = (row) => {
  ElMessage.info(`查看冷源站: ${row.stationName}`)
}

const handleEdit = (row) => {
  editingId.value = row.id
  Object.assign(form, row)
  showDialog.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除冷源站"${row.stationName}"吗？`, '提示', { type: 'warning' }).then(() => {
    tableData.value = tableData.value.filter(t => t.id !== row.id)
    total.value--
    ElMessage.success('删除成功')
  })
}

const handleSave = () => {
  ElMessage.success(editingId.value ? '保存成功' : '新增成功')
  showDialog.value = false
  editingId.value = null
}
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
