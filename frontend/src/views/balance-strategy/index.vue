<template>
  <div class="balance-strategy">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><Operation /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ balanceStrategy.strategyName }}</div>
              <div class="stat-label">当前策略</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><PieChart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ balanceStrategy.balanceRate }}%</div>
              <div class="stat-label">平衡度</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ balanceStrategy.autoRate }}%</div>
              <div class="stat-label">自动调节率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="32"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ balanceStrategy.lastAdjust }}</div>
              <div class="stat-label">上次调节</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>平衡策略配置</span>
            </div>
          </template>
          <el-form label-width="120px">
            <el-form-item label="策略名称">
              <el-input v-model="strategyConfig.name" />
            </el-form-item>
            <el-form-item label="调节方式">
              <el-radio-group v-model="strategyConfig.adjustMode">
                <el-radio label="auto">自动调节</el-radio>
                <el-radio label="manual">手动调节</el-radio>
                <el-radio label="hybrid">混合调节</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="调节频率">
              <el-select v-model="strategyConfig.frequency" style="width: 100%">
                <el-option label="实时调节(15分钟)" value="realtime" />
                <el-option label="定时调节(每小时)" value="hourly" />
                <el-option label="每日调节" value="daily" />
              </el-select>
            </el-form-item>
            <el-form-item label="平衡目标(%)">
              <el-slider v-model="strategyConfig.targetBalance" :min="80" :max="100" show-input />
            </el-form-item>
            <el-form-item label="安全限制">
              <el-input-number v-model="strategyConfig.maxAdjust" :min="1" :max="20" />
              <span style="margin-left: 10px">% 单次最大调节幅度</span>
            </el-form-item>
            <el-form-item label="启用状态">
              <el-switch v-model="strategyConfig.enabled" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveStrategy">保存策略</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>策略列表</span>
              <el-button type="primary" size="small">新增策略</el-button>
            </div>
          </template>
          <el-table :data="strategyList" style="width: 100%">
            <el-table-column prop="name" label="策略名称" />
            <el-table-column prop="mode" label="调节方式" width="100" />
            <el-table-column prop="frequency" label="调节频率" width="120" />
            <el-table-column prop="targetBalance" label="平衡目标(%)" width="100" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-switch v-model="row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default>
                <el-button type="primary" link size="small">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>策略执行记录</span>
            </div>
          </template>
          <el-table :data="executionRecords" style="width: 100%">
            <el-table-column prop="time" label="执行时间" width="180" />
            <el-table-column prop="strategy" label="执行策略" />
            <el-table-column prop="target" label="调节目标" />
            <el-table-column prop="adjustment" label="调节幅度" width="100" />
            <el-table-column prop="result" label="执行结果" width="100">
              <template #default="{ row }">
                <el-tag :type="row.result === '成功' ? 'success' : 'danger'">{{ row.result }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="balanceAfter" label="调节后平衡度(%)" width="140" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Operation, PieChart, Timer, Clock } from '@element-plus/icons-vue'

const balanceStrategy = ref({
  strategyName: '智能平衡策略',
  balanceRate: 95.2,
  autoRate: 88,
  lastAdjust: '30分钟前'
})

const strategyConfig = ref({
  name: '智能平衡策略',
  adjustMode: 'auto',
  frequency: 'realtime',
  targetBalance: 95,
  maxAdjust: 10,
  enabled: true
})

const strategyList = ref([
  { name: '智能平衡策略', mode: '自动', frequency: '实时', targetBalance: 95, status: true },
  { name: '保守策略', mode: '手动', frequency: '每日', targetBalance: 90, status: false },
  { name: '快速平衡', mode: '混合', frequency: '每小时', targetBalance: 98, status: false }
])

const executionRecords = ref([
  { time: '2026-03-15 10:30:00', strategy: '智能平衡策略', target: '2号楼阀门', adjustment: '5%', result: '成功', balanceAfter: 96.5 },
  { time: '2026-03-15 10:15:00', strategy: '智能平衡策略', target: '3号楼阀门', adjustment: '-3%', result: '成功', balanceAfter: 95.8 }
])

const saveStrategy = () => {
  console.log('保存策略')
}
</script>

<style scoped>
.balance-strategy {
  padding: 0;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.chart-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>
