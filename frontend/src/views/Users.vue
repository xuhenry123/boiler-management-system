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
        <el-table-column label="操作" width="150">
          <template #default>
            <el-button size="small">编辑</el-button>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const showAddDialog = ref(false)

const users = ref([
  { id: 1, userCode: 'U001', userName: '张三', buildingName: '阳光花园1号楼', unitNo: '1', roomNo: '101', area: 120, targetTemp: 20, status: 1 },
  { id: 2, userCode: 'U002', userName: '李四', buildingName: '阳光花园1号楼', unitNo: '1', roomNo: '102', area: 120, targetTemp: 21, status: 1 },
  { id: 3, userCode: 'U003', userName: '王五', buildingName: '阳光花园1号楼', unitNo: '1', roomNo: '201', area: 130, targetTemp: 20, status: 1 },
  { id: 4, userCode: 'U004', userName: '赵六', buildingName: '阳光花园2号楼', unitNo: '2', roomNo: '301', area: 140, targetTemp: 22, status: 1 }
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
