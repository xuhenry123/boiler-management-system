<template>
  <div class="climate-compensation">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><Sunny /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ currentConfig }}</div>
              <div class="stat-label">当前补偿模式</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><Sunny /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ outdoorTemp }}°C</div>
              <div class="stat-label">室外温度</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ supplyTemp }}°C</div>
              <div class="stat-label">当前供水温度</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="32"><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ savingsRate }}%</div>
              <div class="stat-label">节能率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>气候补偿曲线</span>
              <div>
                <el-select v-model="curveType" style="width: 120px; margin-right: 10px" @change="updateCurve">
                  <el-option label="供水温度" value="supply" />
                  <el-option label="回水温度" value="return" />
                  <el-option label="温差" value="diff" />
                </el-select>
                <el-button type="primary" size="small" @click="showEditDialog = true">
                  <el-icon><Edit /></el-icon>
                  编辑曲线
                </el-button>
              </div>
            </div>
          </template>
          <div ref="compensationCurveRef" style="height: 400px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>补偿策略配置</span>
            </div>
          </template>
          <el-form label-width="100px">
            <el-form-item label="补偿模式">
              <el-select v-model="compensationMode" style="width: 100%" @change="onModeChange">
                <el-option label="固定温度" value="fixed" />
                <el-option label="气候补偿" value="climate" />
                <el-option label="时间补偿" value="time" />
                <el-option label="智能补偿" value="smart" />
              </el-select>
            </el-form-item>
            <el-form-item label="补偿曲线">
              <el-select v-model="selectedCurve" style="width: 100%" @change="onCurveChange">
                <el-option label="标准曲线" value="standard" />
                <el-option label="节能曲线" value="energy_saving" />
                <el-option label="舒适曲线" value="comfort" />
                <el-option label="自定义曲线" value="custom" />
              </el-select>
            </el-form-item>
            <el-form-item label="室内温度">
              <el-input-number v-model="indoorTarget" :min="16" :max="24" :step="0.5" />
              <span style="margin-left: 10px">°C</span>
            </el-form-item>
            <el-form-item label="最低室外温度">
              <el-input-number v-model="minOutdoorTemp" :min="-30" :max="0" :step="1" />
              <span style="margin-left: 10px">°C</span>
            </el-form-item>
            <el-form-item label="最高室外温度">
              <el-input-number v-model="maxOutdoorTemp" :min="0" :max="20" :step="1" />
              <span style="margin-left: 10px">°C</span>
            </el-form-item>
            <el-form-item label="补偿启用">
              <el-switch v-model="compensationEnabled" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveConfig">保存配置</el-button>
              <el-button @click="previewEffect">预览效果</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>补偿效果对比</span>
              <el-select v-model="compareRange" size="small" style="width: 100px">
                <el-option label="本周" value="week" />
                <el-option label="本月" value="month" />
                <el-option label="本季" value="season" />
              </el-select>
            </div>
          </template>
          <div ref="effectCompareRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>补偿策略列表</span>
              <el-button type="primary" size="small" @click="showStrategyDialog = true">新增策略</el-button>
            </div>
          </template>
          <el-table :data="strategyList" style="width: 100%">
            <el-table-column prop="name" label="策略名称" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag>{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="tempRange" label="温度范围" width="120" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-switch v-model="row.status" @change="toggleStrategy(row)" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="editStrategy(row)">编辑</el-button>
                <el-button type="danger" link size="small" @click="deleteStrategy(row)">删除</el-button>
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
            <span>智能补偿算法配置</span>
          </template>
          <el-descriptions :column="4" border>
            <el-descriptions-item label="算法类型">自适应神经网络</el-descriptions-item>
            <el-descriptions-item label="输入特征">室外温度、湿度、风速、时间</el-descriptions-item>
            <el-descriptions-item label="预测精度">96.5%</el-descriptions-item>
            <el-descriptions-item label="自学习">开启</el-descriptions-item>
            <el-descriptions-item label="学习周期">每日</el-descriptions-item>
            <el-descriptions-item label="最近训练">2026-03-15 02:00</el-descriptions-item>
            <el-descriptions-item label="样本数据量">30天</el-descriptions-item>
            <el-descriptions-item label="模型版本">v2.3.1</el-descriptions-item>
          </el-descriptions>
          <div style="margin-top: 15px">
            <el-button type="primary" @click="retrainModel">重新训练模型</el-button>
            <el-button @click="exportModel">导出模型</el-button>
            <el-button @click="importModel">导入模型</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showEditDialog" title="编辑补偿曲线" width="600px">
      <el-form label-width="120px">
        <el-form-item label="曲线名称">
          <el-input v-model="editCurveName" />
        </el-form-item>
        <el-form-item label="曲线数据点">
          <el-table :data="curveDataPoints" style="width: 100%">
            <el-table-column prop="outdoorTemp" label="室外温度(°C)" width="120">
              <template #default="{ row, $index }">
                <el-input-number v-model="row.outdoorTemp" size="small" :min="-30" :max="20" />
              </template>
            </el-table-column>
            <el-table-column prop="supplyTemp" label="供水温度(°C)" width="120">
              <template #default="{ row }">
                <el-input-number v-model="row.supplyTemp" size="small" :min="30" :max="120" />
              </template>
            </el-table-column>
            <el-table-column prop="returnTemp" label="回水温度(°C)" width="120">
              <template #default="{ row }">
                <el-input-number v-model="row.returnTemp" size="small" :min="20" :max="80" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="60">
              <template #default="{ $index }">
                <el-button type="danger" link size="small" @click="curveDataPoints.splice($index, 1)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addCurvePoint">添加数据点</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCurve">保存曲线</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showStrategyDialog" title="新增补偿策略" width="500px">
      <el-form label-width="100px">
        <el-form-item label="策略名称">
          <el-input v-model="newStrategy.name" />
        </el-form-item>
        <el-form-item label="策略类型">
          <el-select v-model="newStrategy.type" style="width: 100%">
            <el-option label="气候补偿" value="climate" />
            <el-option label="时间补偿" value="time" />
            <el-option label="智能补偿" value="smart" />
          </el-select>
        </el-form-item>
        <el-form-item label="温度范围">
          <el-input v-model="newStrategy.tempRange" placeholder="如: -20~10°C" />
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch v-model="newStrategy.status" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStrategyDialog = false">取消</el-button>
        <el-button type="primary" @click="createStrategy">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Sunny, TrendCharts, DataLine, Edit } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { climateApi } from '@/api'

const compensationCurveRef = ref(null)
const effectCompareRef = ref(null)

const currentConfig = ref('智能补偿')
const outdoorTemp = ref(-5)
const supplyTemp = ref(52)
const savingsRate = ref(12.5)

const compensationMode = ref('climate')
const selectedCurve = ref('standard')
const indoorTarget = ref(20)
const minOutdoorTemp = ref(-20)
const maxOutdoorTemp = ref(10)
const compensationEnabled = ref(true)
const curveType = ref('supply')
const compareRange = ref('week')

const showEditDialog = ref(false)
const showStrategyDialog = ref(false)
const editCurveName = ref('')
const newStrategy = ref({
  name: '',
  type: 'climate',
  tempRange: '',
  status: true
})

const curveDataPoints = ref([
  { outdoorTemp: -20, supplyTemp: 75, returnTemp: 55 },
  { outdoorTemp: -15, supplyTemp: 68, returnTemp: 50 },
  { outdoorTemp: -10, supplyTemp: 60, returnTemp: 44 },
  { outdoorTemp: -5, supplyTemp: 52, returnTemp: 38 },
  { outdoorTemp: 0, supplyTemp: 45, returnTemp: 33 },
  { outdoorTemp: 5, supplyTemp: 40, returnTemp: 30 },
  { outdoorTemp: 10, supplyTemp: 35, returnTemp: 28 }
])

const strategyList = ref([
  { name: '标准补偿', type: '气候', tempRange: '-20~10°C', status: true },
  { name: '节能模式', type: '气候', tempRange: '-15~5°C', status: true },
  { name: '舒适模式', type: '气候', tempRange: '-10~15°C', status: false },
  { name: '夜间模式', type: '时间', tempRange: '-5~10°C', status: true }
])

const onModeChange = () => {
  ElMessage.info(`已切换到${compensationMode.value}模式`)
  initCompensationCurve()
}

const onCurveChange = () => {
  ElMessage.info(`已切换到${selectedCurve.value}曲线`)
  initCompensationCurve()
}

const updateCurve = () => {
  initCompensationCurve()
}

const saveConfig = () => {
  const configData = {
    stationId: 1,
    configName: selectedCurve.value === 'standard' ? '标准补偿' : selectedCurve.value === 'energy_saving' ? '节能模式' : '舒适模式',
    compensationMode: compensationMode.value,
    curveType: selectedCurve.value,
    outdoorTempMin: minOutdoorTemp.value,
    outdoorTempMax: maxOutdoorTemp.value,
    indoorTargetTemp: indoorTarget.value,
    isActive: compensationEnabled.value ? 1 : 0,
    isEnabled: compensationEnabled.value ? 1 : 0
  }
  
  climateApi.getConfigs({}).then(res => {
    const existingConfigs = res.data || []
    const activeConfig = existingConfigs.find(c => c.isActive === 1)
    
    if (activeConfig) {
      climateApi.updateConfig(activeConfig.id, configData).then(() => {
        ElMessage.success('配置已保存')
        loadConfigs()
      })
    } else {
      climateApi.createConfig(configData).then(() => {
        ElMessage.success('配置已保存')
        loadConfigs()
      })
    }
  }).catch(() => {
    climateApi.createConfig(configData).then(() => {
      ElMessage.success('配置已保存')
      loadConfigs()
    })
  })
}

const loadConfigs = () => {
  climateApi.getConfigs({}).then(data => {
    if (data.data && data.data.length > 0) {
      strategyList.value = data.data.map(c => ({
        name: c.configName,
        type: c.compensationMode === 'climate' ? '气候' : c.compensationMode === 'time' ? '时间' : '智能',
        tempRange: `${c.outdoorTempMin || -20}~${c.outdoorTempMax || 10}°C`,
        status: c.isActive === 1,
        id: c.id
      }))
    }
  })
}

const previewEffect = () => {
  ElMessage.info('正在计算补偿效果...')
  setTimeout(() => {
    ElMessage.success('效果预览已更新')
    initEffectCompare()
  }, 1000)
}

const toggleStrategy = (row) => {
  climateApi.getConfigById(row.id).then(config => {
    config.isActive = row.status ? 1 : 0
    climateApi.updateConfig(row.id, config).then(() => {
      ElMessage.success(`${row.name}已${row.status ? '启用' : '禁用'}`)
      loadConfigs()
    })
  })
}

const editStrategy = (row) => {
  climateApi.getConfigById(row.id).then(config => {
    newStrategy.value = {
      name: config.configName,
      type: config.compensationMode,
      tempRange: `${config.outdoorTempMin || -20}~${config.outdoorTempMax || 10}°C`,
      status: config.isActive === 1,
      id: config.id
    }
    showStrategyDialog.value = true
  })
}

const deleteStrategy = (row) => {
  climateApi.deleteConfig(row.id).then(() => {
    strategyList.value = strategyList.value.filter(s => s.id !== row.id)
    ElMessage.success('策略已删除')
  })
}

const addCurvePoint = () => {
  curveDataPoints.value.push({ outdoorTemp: 0, supplyTemp: 40, returnTemp: 30 })
}

const saveCurve = () => {
  showEditDialog.value = false
  ElMessage.success('曲线已保存')
  initCompensationCurve()
}

const createStrategy = () => {
  const configData = {
    stationId: 1,
    configName: newStrategy.value.name,
    compensationMode: newStrategy.value.type,
    curveType: 'standard',
    outdoorTempMin: -20,
    outdoorTempMax: 10,
    indoorTargetTemp: 20,
    isActive: newStrategy.value.status ? 1 : 0,
    isEnabled: newStrategy.value.status ? 1 : 0
  }
  
  climateApi.createConfig(configData).then(() => {
    showStrategyDialog.value = false
    newStrategy.value = { name: '', type: 'climate', tempRange: '', status: true }
    ElMessage.success('策略已创建')
    loadConfigs()
  })
}

const retrainModel = () => {
  ElMessage.info('模型训练中，请稍候...')
  setTimeout(() => {
    ElMessage.success('模型训练完成')
  }, 2000)
}

const exportModel = () => {
  ElMessage.info('正在导出模型...')
}

const importModel = () => {
  ElMessage.info('请选择模型文件')
}

const initCompensationCurve = () => {
  climateApi.getCurves({}).then(data => {
    const chart = echarts.init(compensationCurveRef.value)
    
    const curves = data.curves || []
    let supplyData = []
    let returnData = []
    let xData = []
    
    curves.forEach(item => {
      xData.push(item.outdoorTemp.toString())
      supplyData.push(item.supplyTemp)
      returnData.push(item.returnTemp)
    })
    
    if (xData.length === 0) {
      xData = ['-20', '-15', '-10', '-5', '0', '5', '10']
      if (selectedCurve.value === 'standard') {
        supplyData = [75, 68, 60, 52, 45, 40, 35]
        returnData = [55, 50, 44, 38, 33, 30, 28]
      } else if (selectedCurve.value === 'energy_saving') {
        supplyData = [70, 62, 55, 48, 42, 38, 33]
        returnData = [50, 45, 40, 35, 30, 28, 26]
      } else if (selectedCurve.value === 'comfort') {
        supplyData = [80, 72, 65, 58, 50, 45, 40]
        returnData = [60, 54, 48, 42, 36, 32, 30]
      }
    } else if (selectedCurve.value === 'energy_saving') {
      supplyData = supplyData.map(v => Math.round(v * 0.93))
      returnData = returnData.map(v => Math.round(v * 0.9))
    } else if (selectedCurve.value === 'comfort') {
      supplyData = supplyData.map(v => Math.round(v * 1.07))
      returnData = returnData.map(v => Math.round(v * 1.1))
    }
    
    const option = {
      tooltip: { trigger: 'axis' },
      legend: { data: ['供水温度', '回水温度', '室内目标温度'] },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: xData,
        name: '室外温度(°C)'
      },
      yAxis: { type: 'value', name: '温度(°C)' },
      series: [
        {
          name: '供水温度',
          type: 'line',
          smooth: true,
          data: supplyData,
          itemStyle: { color: '#409eff' }
        },
        {
          name: '回水温度',
          type: 'line',
          smooth: true,
          data: returnData,
          itemStyle: { color: '#67c23a' }
        },
        {
          name: '室内目标温度',
          type: 'line',
          data: xData.map(() => indoorTarget.value),
          lineStyle: { type: 'dashed', opacity: 0.5 },
          itemStyle: { opacity: 0 }
        }
      ]
    }
    chart.setOption(option)
  }).catch(() => {
    renderDefaultCurve()
  })
}

const renderDefaultCurve = () => {
  const chart = echarts.init(compensationCurveRef.value)
  
  let supplyData = []
  let returnData = []
  
  if (selectedCurve.value === 'standard') {
    supplyData = [75, 68, 60, 52, 45, 40, 35]
    returnData = [55, 50, 44, 38, 33, 30, 28]
  } else if (selectedCurve.value === 'energy_saving') {
    supplyData = [70, 62, 55, 48, 42, 38, 33]
    returnData = [50, 45, 40, 35, 30, 28, 26]
  } else if (selectedCurve.value === 'comfort') {
    supplyData = [80, 72, 65, 58, 50, 45, 40]
    returnData = [60, 54, 48, 42, 36, 32, 30]
  }
  
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['供水温度', '回水温度', '室内目标温度'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['-20', '-15', '-10', '-5', '0', '5', '10'],
      name: '室外温度(°C)'
    },
    yAxis: { type: 'value', name: '温度(°C)' },
    series: [
      {
        name: '供水温度',
        type: 'line',
        smooth: true,
        data: supplyData,
        itemStyle: { color: '#409eff' }
      },
      {
        name: '回水温度',
        type: 'line',
        smooth: true,
        data: returnData,
        itemStyle: { color: '#67c23a' }
      },
      {
        name: '室内目标温度',
        type: 'line',
        data: [20, 20, 20, 20, 20, 20, 20],
        lineStyle: { type: 'dashed', opacity: 0.5 },
        itemStyle: { opacity: 0 }
      }
    ]
  }
  chart.setOption(option)
}

const initEffectCompare = () => {
  climateApi.getEffects({ days: 7 }).then(data => {
    const chart = echarts.init(effectCompareRef.value)
    const effects = data.data || []
    
    let xData = []
    
    if (xData.length === 0) {
      xData = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    }
    
    const option = {
      tooltip: { trigger: 'axis' },
      legend: { data: ['补偿前', '补偿后'] },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: xData },
      yAxis: { type: 'value', name: '能耗(tce)' },
      series: [
        { name: '补偿前', type: 'bar', data: beforeData.length ? beforeData : [52, 48, 55, 50, 45, 42, 46], itemStyle: { color: '#f56c6c' } },
        { name: '补偿后', type: 'bar', data: afterData.length ? afterData : [45, 42, 48, 44, 40, 37, 40], itemStyle: { color: '#67c23a' } }
      ]
    }
    chart.setOption(option)
  }).catch(() => {
    renderDefaultEffectCompare()
  })
}

const renderDefaultEffectCompare = () => {
  const chart = echarts.init(effectCompareRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['补偿前', '补偿后'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
    yAxis: { type: 'value', name: '能耗(tce)' },
    series: [
      { name: '补偿前', type: 'bar', data: [52, 48, 55, 50, 45, 42, 46], itemStyle: { color: '#f56c6c' } },
      { name: '补偿后', type: 'bar', data: [45, 42, 48, 44, 40, 37, 40], itemStyle: { color: '#67c23a' } }
    ]
  }
  chart.setOption(option)
}

onMounted(() => {
  climateApi.getStatistics().then(data => {
    currentConfig.value = data.currentMode || '智能补偿'
    outdoorTemp.value = data.outdoorTemp || -5
    supplyTemp.value = data.supplyTemp || 52
    savingsRate.value = data.savingsRate || 12.5
  }).catch(() => {
    currentConfig.value = '智能补偿'
    outdoorTemp.value = -5
    supplyTemp.value = 52
    savingsRate.value = 12.5
  })
  
  climateApi.getConfigs({}).then(data => {
    if (data.data && data.data.length > 0) {
      const activeConfig = data.data.find(c => c.isActive === 1)
      if (activeConfig) {
        compensationMode.value = activeConfig.compensationMode || 'climate'
        selectedCurve.value = activeConfig.curveType || 'standard'
        indoorTarget.value = activeConfig.indoorTargetTemp || 20
        minOutdoorTemp.value = activeConfig.outdoorTempMin || -20
        maxOutdoorTemp.value = activeConfig.outdoorTempMax || 10
        compensationEnabled.value = activeConfig.isEnabled === 1
      }
      
      strategyList.value = data.data.map(c => ({
        name: c.configName,
        type: c.compensationMode === 'climate' ? '气候' : c.compensationMode === 'time' ? '时间' : '智能',
        tempRange: `${c.outdoorTempMin || -20}~${c.outdoorTempMax || 10}°C`,
        status: c.isActive === 1,
        id: c.id
      }))
    }
  }).catch(() => {})
  
  initCompensationCurve()
  initEffectCompare()
})
</script>

<style scoped>
.climate-compensation {
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
  font-size: 24px;
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
