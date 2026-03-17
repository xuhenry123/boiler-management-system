<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>换热站管理</span>
          <el-button type="primary" @click="handleAdd">新增</el-button>
        </div>
      </template>
      
      <el-table :data="stations" style="width: 100%">
        <el-table-column prop="stationCode" label="换热站编码" width="120" />
        <el-table-column prop="stationName" label="换热站名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="designCapacity" label="设计能力(MW)" width="120" />
        <el-table-column prop="designFlow" label="设计流量(t/h)" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'info' : 'warning'">
              {{ row.status === 1 ? '运行' : row.status === 0 ? '停运' : '调试' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="primary" @click="handleMonitor(row)">监控</el-button>
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

    <!-- 新增/编辑换热站对话框 -->
    <el-dialog v-model="showDialog" :title="editingId ? '编辑换热站' : '新增换热站'" width="600px">
      <el-form :model="stationForm" label-width="120px">
        <el-form-item label="换热站编码">
          <el-input v-model="stationForm.stationCode" placeholder="请输入换热站编码" />
        </el-form-item>
        <el-form-item label="换热站名称">
          <el-input v-model="stationForm.stationName" placeholder="请输入换热站名称" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="stationForm.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="设计能力(MW)">
          <el-input-number v-model="stationForm.designCapacity" :min="0" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="设计流量(t/h)">
          <el-input-number v-model="stationForm.designFlow" :min="0" :step="50" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="stationForm.status" style="width: 100%">
            <el-option label="运行" :value="1" />
            <el-option label="停运" :value="0" />
            <el-option label="调试" :value="2" />
          </el-select>
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
import { useRouter } from 'vue-router'
import { stationApi } from '@/api'

const router = useRouter()

// 控制对话框显示
const showDialog = ref(false)
// 编辑时的换热站ID
const editingId = ref(null)
// 当前页码
const currentPage = ref(1)
// 每页条数
const pageSize = ref(10)
// 总记录数
const total = ref(0)

// 换热站列表数据
const stations = ref([])

// 换热站表单数据
const stationForm = reactive({
  id: null,
  stationCode: '',
  stationName: '',
  address: '',
  designCapacity: 0,
  designFlow: 0,
  status: 1
})

/**
 * 加载换热站列表数据
 */
const loadStations = async () => {
  try {
    const result = await stationApi.getStations()
    stations.value = result.data || []
    total.value = result.total || 0
  } catch (error) {
    console.error('加载换热站数据失败:', error)
  }
}

/**
 * 新增换热站
 */
const handleAdd = () => {
  editingId.value = null
  resetForm()
  showDialog.value = true
}

/**
 * 编辑换热站
 * @param row 换热站数据行
 */
const handleEdit = (row) => {
  editingId.value = row.id
  Object.assign(stationForm, row)
  showDialog.value = true
}

/**
 * 监控换热站
 * @param row 换热站数据行
 */
const handleMonitor = (row) => {
  router.push(`/heat-station?id=${row.id}`)
}

/**
 * 删除换热站
 * @param row 换热站数据行
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除换热站"${row.stationName}"吗？`, '提示', { type: 'warning' }).then(async () => {
    await stationApi.deleteStation(row.id)
    await loadStations()
    ElMessage.success('删除成功')
  })
}

/**
 * 保存换热站信息
 */
const handleSave = async () => {
  try {
    if (editingId.value) {
      await stationApi.updateStation(editingId.value, stationForm)
      ElMessage.success('保存成功')
    } else {
      await stationApi.createStation(stationForm)
      ElMessage.success('新增成功')
    }
    showDialog.value = false
    editingId.value = null
    resetForm()
    await loadStations()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

/**
 * 重置表单
 */
const resetForm = () => {
  stationForm.id = null
  stationForm.stationCode = ''
  stationForm.stationName = ''
  stationForm.address = ''
  stationForm.designCapacity = 0
  stationForm.designFlow = 0
  stationForm.status = 1
}

// 组件挂载完成后加载数据
onMounted(() => {
  loadStations()
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
