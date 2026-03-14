<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>建筑物管理</span>
          <el-button type="primary" @click="showAddDialog = true">新增</el-button>
        </div>
      </template>
      
      <el-table :data="buildings" style="width: 100%">
        <el-table-column prop="buildingCode" label="建筑编码" width="120" />
        <el-table-column prop="buildingName" label="建筑名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="areaHeated" label="供热面积(㎡)" width="120" />
        <el-table-column prop="buildingType" label="建筑类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.buildingType === 'residential' ? '住宅' : row.buildingType === 'commercial' ? '商业' : '工业' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="heatTransferCoefficient" label="传热系数" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="editBuilding(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteBuilding(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="showAddDialog" title="建筑物信息" width="500px">
      <el-form :model="buildingForm" label-width="100px">
        <el-form-item label="建筑编码">
          <el-input v-model="buildingForm.buildingCode" />
        </el-form-item>
        <el-form-item label="建筑名称">
          <el-input v-model="buildingForm.buildingName" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="buildingForm.address" />
        </el-form-item>
        <el-form-item label="供热面积">
          <el-input-number v-model="buildingForm.areaHeated" />
        </el-form-item>
        <el-form-item label="建筑类型">
          <el-select v-model="buildingForm.buildingType">
            <el-option value="residential" label="住宅" />
            <el-option value="commercial" label="商业" />
            <el-option value="industrial" label="工业" />
          </el-select>
        </el-form-item>
        <el-form-item label="传热系数">
          <el-input-number v-model="buildingForm.heatTransferCoefficient" :step="0.1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveBuilding">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const showAddDialog = ref(false)

const buildings = ref([
  { id: 1, buildingCode: 'BLD001', buildingName: '阳光花园1号楼', address: '东城区阳光路1号', areaHeated: 12000, buildingType: 'residential', heatTransferCoefficient: 1.2, status: 1 },
  { id: 2, buildingCode: 'BLD002', buildingName: '阳光花园2号楼', address: '东城区阳光路2号', areaHeated: 15000, buildingType: 'residential', heatTransferCoefficient: 1.15, status: 1 },
  { id: 3, buildingCode: 'BLD003', buildingName: '商业大厦A座', address: '西城区金融街8号', areaHeated: 20000, buildingType: 'commercial', heatTransferCoefficient: 0.9, status: 1 }
])

const buildingForm = reactive({
  id: null,
  buildingCode: '',
  buildingName: '',
  address: '',
  areaHeated: 0,
  buildingType: 'residential',
  heatTransferCoefficient: 1.0
})

const editBuilding = (row) => {
  Object.assign(buildingForm, row)
  showAddDialog.value = true
}

const deleteBuilding = (row) => {
  ElMessage.success('删除成功')
}

const saveBuilding = () => {
  ElMessage.success('保存成功')
  showAddDialog.value = false
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
