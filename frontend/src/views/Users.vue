<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>热用户管理</span>
          <el-button type="primary" @click="showAddDialog = true">新增</el-button>
        </div>
      </template>
      
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="userCode" label="用户编码" width="100" />
        <el-table-column prop="userName" label="用户姓名" width="100" />
        <el-table-column prop="buildingName" label="建筑物" />
        <el-table-column prop="unitNo" label="单元" width="80" />
        <el-table-column prop="roomNo" label="房间号" width="100" />
        <el-table-column prop="area" label="面积(㎡)" width="100" />
        <el-table-column prop="targetTemp" label="目标温度(℃)" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
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

    <!-- 新增/编辑用户对话框 -->
    <el-dialog v-model="showAddDialog" :title="editingId ? '编辑用户' : '新增用户'" width="600px">
      <el-form :model="userForm" label-width="100px">
        <el-form-item label="用户编码">
          <el-input v-model="userForm.userCode" placeholder="请输入用户编码" />
        </el-form-item>
        <el-form-item label="用户姓名">
          <el-input v-model="userForm.userName" placeholder="请输入用户姓名" />
        </el-form-item>
        <el-form-item label="建筑物">
          <el-select v-model="userForm.buildingName" placeholder="请选择建筑物" style="width: 100%">
            <el-option label="阳光花园1号楼" value="阳光花园1号楼" />
            <el-option label="阳光花园2号楼" value="阳光花园2号楼" />
            <el-option label="商业大厦A座" value="商业大厦A座" />
          </el-select>
        </el-form-item>
        <el-form-item label="单元">
          <el-input v-model="userForm.unitNo" placeholder="请输入单元号" />
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="userForm.roomNo" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="面积(㎡)">
          <el-input-number v-model="userForm.area" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="目标温度">
          <el-input-number v-model="userForm.targetTemp" :min="10" :max="30" :step="0.5" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="userForm.status" :active-value="1" :inactive-value="0" active-text="正常" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { heatUserApi } from '@/api'

// 控制新增/编辑对话框显示
const showAddDialog = ref(false)
// 编辑时的用户ID
const editingId = ref(null)
// 当前页码
const currentPage = ref(1)
// 每页条数
const pageSize = ref(10)
// 总记录数
const total = ref(0)

// 热用户列表数据
const users = ref([])

// 用户表单数据
const userForm = reactive({
  id: null,
  userCode: '',
  userName: '',
  buildingName: '',
  unitNo: '',
  roomNo: '',
  area: 0,
  targetTemp: 20,
  status: 1
})

/**
 * 加载用户列表数据
 */
const loadUsers = async () => {
  try {
    const result = await heatUserApi.getUsers()
    users.value = result.data || []
    total.value = result.total || 0
  } catch (error) {
    console.error('加载用户数据失败:', error)
  }
}

/**
 * 编辑用户信息
 * @param row 用户数据行
 */
const handleEdit = (row) => {
  editingId.value = row.id
  Object.assign(userForm, row)
  showAddDialog.value = true
}

/**
 * 删除用户
 * @param row 用户数据行
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除热用户"${row.userName}"吗？`, '提示', { type: 'warning' }).then(async () => {
    await heatUserApi.deleteUser(row.id)
    await loadUsers()
    ElMessage.success('删除成功')
  })
}

/**
 * 保存用户信息
 */
const handleSave = async () => {
  try {
    if (editingId.value) {
      await heatUserApi.updateUser(editingId.value, userForm)
      ElMessage.success('保存成功')
    } else {
      await heatUserApi.createUser(userForm)
      ElMessage.success('新增成功')
    }
    showAddDialog.value = false
    editingId.value = null
    resetForm()
    await loadUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

/**
 * 重置表单
 */
const resetForm = () => {
  userForm.id = null
  userForm.userCode = ''
  userForm.userName = ''
  userForm.buildingName = ''
  userForm.unitNo = ''
  userForm.roomNo = ''
  userForm.area = 0
  userForm.targetTemp = 20
  userForm.status = 1
}

// 组件挂载完成后加载数据
onMounted(() => {
  loadUsers()
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
