<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>换热站管理</span>
          <el-button type="primary" @click="showAddDialog = true">新增</el-button>
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
        <el-table-column label="操作" width="150">
          <template #default>
            <el-button size="small">编辑</el-button>
            <el-button size="small" type="primary">监控</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const showAddDialog = ref(false)

const stations = ref([
  { id: 1, stationCode: 'HS001', stationName: '东城区换热站', address: '北京市东城区', designCapacity: 50, designFlow: 800, status: 1 },
  { id: 2, stationCode: 'HS002', stationName: '西城区换热站', address: '北京市西城区', designCapacity: 40, designFlow: 650, status: 1 },
  { id: 3, stationCode: 'HS003', stationName: '朝阳区换热站', address: '北京市朝阳区', designCapacity: 60, designFlow: 1000, status: 1 }
])
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
