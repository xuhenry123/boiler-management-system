<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>热源站管理</span>
          <el-button type="primary" @click="showDialog = true">
            <el-icon><Plus /></el-icon>新增热源站
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="stationCode" label="站点编号" width="120" />
        <el-table-column prop="stationName" label="站点名称" />
        <el-table-column prop="stationType" label="站点类型">
          <template #default="{ row }">
            <el-tag>{{ getTypeLabel(row.stationType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="capacity" label="设计容量(MW)" />
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

    <el-dialog v-model="showDialog" :title="editingId ? '编辑热源站' : '新增热源站'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="站点编号">
          <el-input v-model="form.stationCode" placeholder="请输入站点编号" />
        </el-form-item>
        <el-form-item label="站点名称">
          <el-input v-model="form.stationName" placeholder="请输入站点名称" />
        </el-form-item>
        <el-form-item label="站点类型">
          <el-select v-model="form.stationType" placeholder="请选择站点类型" style="width: 100%">
            <el-option label="锅炉房" value="boiler" />
            <el-option label="热电厂" value="cogeneration" />
            <el-option label="燃气锅炉" value="gas" />
            <el-option label="电锅炉" value="electric" />
            <el-option label="热泵" value="heat_pump" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="设计容量(MW)">
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
  { id: 1, stationCode: 'HR001', stationName: '东区锅炉房', stationType: 'boiler', address: '东区工业园', capacity: 50, deviceCount: 4, status: true },
  { id: 2, stationCode: 'HR002', stationName: '西区热电厂', stationType: 'cogeneration', address: '西区工业园', capacity: 100, deviceCount: 6, status: true },
  { id: 3, stationCode: 'HR003', stationName: '南区燃气锅炉', stationType: 'gas', address: '南区住宅区', capacity: 30, deviceCount: 2, status: true },
  { id: 4, stationCode: 'HR004', stationName: '北区热泵站', stationType: 'heat_pump', address: '北区商业区', capacity: 20, deviceCount: 3, status: false }
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
  designPressure: 1.0,
  commissionDate: null,
  remark: ''
})

const getTypeLabel = (type) => {
  const map = { boiler: '锅炉房', cogeneration: '热电厂', gas: '燃气锅炉', electric: '电锅炉', heat_pump: '热泵' }
  return map[type] || type
}

const handleView = (row) => {
  ElMessage.info(`查看热源站: ${row.stationName}`)
}

const handleEdit = (row) => {
  editingId.value = row.id
  Object.assign(form, row)
  showDialog.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除热源站"${row.stationName}"吗？`, '提示', { type: 'warning' }).then(() => {
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
