<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>建筑物管理</span>
          <el-button type="primary" @click="handleAdd">新增</el-button>
        </div>
      </template>
      
      <el-table :data="buildings" style="width: 100%">
        <el-table-column prop="buildingCode" label="建筑编码" width="120" />
        <el-table-column prop="buildingName" label="建筑名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="areaHeated" label="供热面积(㎡)" width="120" />
        <el-table-column prop="buildingType" label="建筑类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getTypeName(row.buildingType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="heatTransferCoefficient" label="传热系数" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="primary" @click="handleView(row)">查看</el-button>
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
    
    <!-- 新增/编辑建筑物对话框 -->
    <el-dialog v-model="showDialog" :title="editingId ? '编辑建筑物' : '新增建筑物'" width="600px">
      <el-form :model="buildingForm" label-width="100px">
        <el-form-item label="建筑编码">
          <el-input v-model="buildingForm.buildingCode" placeholder="请输入建筑编码" />
        </el-form-item>
        <el-form-item label="建筑名称">
          <el-input v-model="buildingForm.buildingName" placeholder="请输入建筑名称" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="buildingForm.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="供热面积">
          <el-input-number v-model="buildingForm.areaHeated" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="建筑类型">
          <el-select v-model="buildingForm.buildingType" style="width: 100%">
            <el-option value="residential" label="住宅" />
            <el-option value="commercial" label="商业" />
            <el-option value="industrial" label="工业" />
          </el-select>
        </el-form-item>
        <el-form-item label="传热系数">
          <el-input-number v-model="buildingForm.heatTransferCoefficient" :step="0.1" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="buildingForm.status" :active-value="1" :inactive-value="0" active-text="正常" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { buildingApi } from '@/api'

// 控制对话框显示
const showDialog = ref(false)
// 编辑时的建筑物ID
const editingId = ref(null)
// 当前页码
const currentPage = ref(1)
// 每页条数
const pageSize = ref(10)
// 总记录数
const total = ref(0)

// 建筑物列表数据
const buildings = ref([])

// 建筑物表单数据
const buildingForm = reactive({
  id: null,
  buildingCode: '',
  buildingName: '',
  address: '',
  areaHeated: 0,
  buildingType: 'residential',
  heatTransferCoefficient: 1.0,
  status: 1
})

/**
 * 获取建筑类型的中文名称
 * @param type 建筑类型
 * @returns 中文名称
 */
const getTypeName = (type) => {
  const map = { residential: '住宅', commercial: '商业', industrial: '工业', public: '公共建筑' }
  return map[type] || type
}

/**
 * 加载建筑物列表数据
 */
const loadBuildings = async () => {
  try {
    const result = await buildingApi.getBuildings()
    buildings.value = result.data || []
    total.value = result.total || 0
  } catch (error) {
    console.error('加载建筑物数据失败:', error)
  }
}

/**
 * 新增建筑物
 */
const handleAdd = () => {
  editingId.value = null
  resetForm()
  showDialog.value = true
}

/**
 * 编辑建筑物
 * @param row 建筑物数据行
 */
const handleEdit = (row) => {
  editingId.value = row.id
  Object.assign(buildingForm, row)
  showDialog.value = true
}

/**
 * 查看建筑物详情
 * @param row 建筑物数据行
 */
const handleView = (row) => {
  ElMessage.info(`查看建筑物: ${row.buildingName}`)
}

/**
 * 删除建筑物
 * @param row 建筑物数据行
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除建筑物"${row.buildingName}"吗？`, '提示', { type: 'warning' }).then(async () => {
    await buildingApi.deleteBuilding(row.id)
    await loadBuildings()
    ElMessage.success('删除成功')
  })
}

/**
 * 保存建筑物信息
 */
const handleSave = async () => {
  try {
    if (editingId.value) {
      await buildingApi.updateBuilding(editingId.value, buildingForm)
      ElMessage.success('保存成功')
    } else {
      await buildingApi.createBuilding(buildingForm)
      ElMessage.success('新增成功')
    }
    showDialog.value = false
    editingId.value = null
    resetForm()
    await loadBuildings()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

/**
 * 重置表单
 */
const resetForm = () => {
  buildingForm.id = null
  buildingForm.buildingCode = ''
  buildingForm.buildingName = ''
  buildingForm.address = ''
  buildingForm.areaHeated = 0
  buildingForm.buildingType = 'residential'
  buildingForm.heatTransferCoefficient = 1.0
  buildingForm.status = 1
}

// 组件挂载完成后加载数据
onMounted(() => {
  loadBuildings()
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
