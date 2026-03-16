<template>
  <div class="climate-compensation">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="监控面板" name="monitor">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon" style="background: #409eff">
                  <el-icon :size="32"><Sunny /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ currentScene }}</div>
                  <div class="stat-label">当前场景模式</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon" style="background: #67c23a">
                  <el-icon :size="32"><Cloudy /></el-icon>
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
                  <div class="stat-value">{{ targetSupplyTemp }}°C</div>
                  <div class="stat-label">目标供水温度</div>
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
                  <span>实时控制状态</span>
                </div>
              </template>
              <el-form label-width="100px">
                <el-form-item label="集成模式">
                  <el-tag :type="integrationMode === 'independent' ? 'success' : 'warning'">
                    {{ integrationMode === 'independent' ? '独立模块' : '一网集成' }}
                  </el-tag>
                </el-form-item>
                <el-form-item label="补偿状态">
                  <el-tag :type="compensationEnabled ? 'success' : 'info'">
                    {{ compensationEnabled ? '运行中' : '已停止' }}
                  </el-tag>
                </el-form-item>
                <el-form-item label="目标温度">
                  <span class="highlight-value">{{ targetSupplyTemp }}°C</span>
                </el-form-item>
                <el-form-item label="实际温度">
                  <span class="highlight-value">{{ actualSupplyTemp }}°C</span>
                </el-form-item>
                <el-form-item label="温度偏差">
                  <span :class="['highlight-value', temperatureDeviation > 0 ? 'text-success' : temperatureDeviation < 0 ? 'text-danger' : '']">
                    {{ temperatureDeviation > 0 ? '+' : '' }}{{ temperatureDeviation }}°C
                  </span>
                </el-form-item>
                <el-form-item label="阀位开度">
                  <el-progress :percentage="valveOpening" :color="valveOpeningColor" style="width: 200px" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="showControlDialog = true">手动控制</el-button>
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
                  <span>温度趋势</span>
                </div>
              </template>
              <div ref="trendRef" style="height: 300px"></div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="PID参数配置" name="pid">
        <el-row :gutter="20">
          <el-col :span="16">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>PID参数模板</span>
                  <el-button type="primary" size="small" @click="showPIDTemplateDialog = true">新建模板</el-button>
                </div>
              </template>
              <el-table :data="pidTemplates" style="width: 100%">
                <el-table-column prop="name" label="模板名称" width="120" />
                <el-table-column prop="sceneTypeName" label="场景类型" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getSceneTypeColor(row.sceneType)">{{ row.sceneTypeName }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="kp" label="Kp" width="60" />
                <el-table-column prop="ki" label="Ki" width="60" />
                <el-table-column prop="kd" label="Kd" width="60" />
                <el-table-column prop="setPoint" label="设定值" width="80" />
                <el-table-column prop="sampleTime" label="采样周期" width="80" />
                <el-table-column prop="isBuiltIn" label="内置" width="60">
                  <template #default="{ row }">
                    <el-tag :type="row.isBuiltIn ? 'info' : 'success'" size="small">{{ row.isBuiltIn ? '是' : '否' }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="120">
                  <template #default="{ row }">
                    <el-button type="primary" link size="small" @click="applyPIDTemplate(row)">应用</el-button>
                    <el-button v-if="!row.isBuiltIn" type="danger" link size="small" @click="deletePIDTemplate(row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <template #header>
                <span>当前PID参数</span>
              </template>
              <el-form label-width="100px">
                <el-form-item label="比例系数Kp">
                  <el-input-number v-model="currentPID.kp" :min="0" :max="10" :step="0.1" />
                </el-form-item>
                <el-form-item label="积分系数Ki">
                  <el-input-number v-model="currentPID.ki" :min="0" :max="5" :step="0.1" />
                </el-form-item>
                <el-form-item label="微分系数Kd">
                  <el-input-number v-model="currentPID.kd" :min="0" :max="5" :step="0.1" />
                </el-form-item>
                <el-form-item label="设定值">
                  <el-input-number v-model="currentPID.setPoint" :min="30" :max="80" :step="1" />
                  <span style="margin-left: 10px">°C</span>
                </el-form-item>
                <el-form-item label="输出范围">
                  <el-input-number v-model="currentPID.minOutput" :min="0" :max="100" style="width: 80px" />
                  <span style="margin: 0 10px">-</span>
                  <el-input-number v-model="currentPID.maxOutput" :min="0" :max="100" style="width: 80px" />
                  <span style="margin-left: 10px">%</span>
                </el-form-item>
                <el-form-item label="采样周期">
                  <el-input-number v-model="currentPID.sampleTime" :min="10" :max="300" :step="10" />
                  <span style="margin-left: 10px">秒</span>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="savePIDParams">保存参数</el-button>
                  <el-button @click="resetPIDParams">重置</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="分时段曲线" name="timesegment">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>分时段补偿曲线配置</span>
              <el-button type="primary" size="small" @click="showTimeSegmentDialog = true">添加时段</el-button>
            </div>
          </template>
          <el-table :data="timeSegments" style="width: 100%">
            <el-table-column prop="segmentName" label="时段名称" width="120" />
            <el-table-column prop="timeRange" label="时间范围" width="180" />
            <el-table-column prop="dayOfWeekName" label="适用日期" width="200" />
            <el-table-column prop="pointCount" label="曲线点数" width="80" />
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="editTimeSegment(row)">编辑</el-button>
                <el-button type="primary" link size="small" @click="viewTimeSegmentCurve(row)">查看曲线</el-button>
                <el-button type="danger" link size="small" @click="deleteTimeSegment(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="集成模式" name="integration">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>集成模式选择</span>
              </template>
              <el-radio-group v-model="integrationMode" @change="onIntegrationModeChange">
                <el-radio value="independent">
                  <div class="mode-option">
                    <div class="mode-title">独立模块模式</div>
                    <div class="mode-desc">气候补偿作为独立模块运行，不与一网调度策略集成</div>
                  </div>
                </el-radio>
                <el-radio value="primary_network">
                  <div class="mode-option">
                    <div class="mode-title">深度集成模式</div>
                    <div class="mode-desc">气候补偿与一网调度策略深度集成，由一网统一协调</div>
                  </div>
                </el-radio>
              </el-radio-group>
              <div style="margin-top: 20px">
                <el-button type="primary" @click="saveIntegrationMode">保存设置</el-button>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>场景模式切换</span>
              </template>
              <el-form label-width="100px">
                <el-form-item label="当前场景">
                  <el-select v-model="currentScene" @change="onSceneChange">
                    <el-option label="严寒模式" value="extreme_cold" />
                    <el-option label="寒冷模式" value="cold" />
                    <el-option label="温和模式" value="mild" />
                    <el-option label="温暖模式" value="warm" />
                    <el-option label="夜间模式" value="night" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="warning" @click="switchSceneManual">手动切换场景</el-button>
                  <el-button @click="enableAutoSwitch = !enableAutoSwitch" :type="enableAutoSwitch ? 'success' : 'info'">
                    {{ enableAutoSwitch ? '自动切换已开启' : '自动切换已关闭' }}
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="换热站配置" name="station">
        <el-card>
          <template #header>
            <span>换热站个性化配置</span>
          </template>
          <el-form :model="stationConfig" label-width="140px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="补偿曲线">
                  <el-select v-model="stationConfig.curveId" style="width: 100%">
                    <el-option v-for="c in curves" :key="c.id" :label="c.name" :value="c.id" />
                  </el-select>
                </el-form-item>
                <el-form-item label="PID模板">
                  <el-select v-model="stationConfig.pidTemplateId" style="width: 100%">
                    <el-option v-for="t in pidTemplates" :key="t.id" :label="t.name" :value="t.id" />
                  </el-select>
                </el-form-item>
                <el-form-item label="自定义设定值">
                  <el-input-number v-model="stationConfig.customSetPoint" :min="30" :max="80" />
                  <span style="margin-left: 10px">°C</span>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="最小供水温度">
                  <el-input-number v-model="stationConfig.minSupplyTemp" :min="20" :max="50" />
                  <span style="margin-left: 10px">°C</span>
                </el-form-item>
                <el-form-item label="最大供水温度">
                  <el-input-number v-model="stationConfig.maxSupplyTemp" :min="50" :max="90" />
                  <span style="margin-left: 10px">°C</span>
                </el-form-item>
                <el-form-item label="建筑热特性系数">
                  <el-slider v-model="stationConfig.buildingHeatCoeff" :min="0.5" :max="2" :step="0.1" show-input />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="启用分时段控制">
              <el-switch v-model="stationConfig.enableTimeSegment" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveStationConfig">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="策略管理" name="strategy">
        <el-card>
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
      </el-tab-pane>
    </el-tabs>

    <!-- 编辑曲线对话框 -->
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

    <!-- 手动控制对话框 -->
    <el-dialog v-model="showControlDialog" title="手动控制" width="400px">
      <el-form label-width="100px">
        <el-form-item label="控制目标">
          <el-select v-model="controlParams.target">
            <el-option label="温控阀" value="valve" />
          </el-select>
        </el-form-item>
        <el-form-item label="控制动作">
          <el-select v-model="controlParams.action">
            <el-option label="设置开度" value="setOpening" />
            <el-option label="增加开度" value="increase" />
            <el-option label="减小开度" value="decrease" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="controlParams.action === 'setOpening'" label="开度值">
          <el-input-number v-model="controlParams.value" :min="0" :max="100" />
          <span style="margin-left: 10px">%</span>
        </el-form-item>
        <el-form-item label="控制模式">
          <el-radio-group v-model="controlParams.mode">
            <el-radio value="manual">手动</el-radio>
            <el-radio value="auto">自动</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showControlDialog = false">取消</el-button>
        <el-button type="primary" @click="executeControl">执行</el-button>
      </template>
    </el-dialog>

    <!-- PID模板对话框 -->
    <el-dialog v-model="showPIDTemplateDialog" title="新建PID模板" width="500px">
      <el-form :model="newPIDTemplate" label-width="100px">
        <el-form-item label="模板名称">
          <el-input v-model="newPIDTemplate.name" />
        </el-form-item>
        <el-form-item label="场景类型">
          <el-select v-model="newPIDTemplate.sceneType" style="width: 100%">
            <el-option label="严寒模式" value="extreme_cold" />
            <el-option label="寒冷模式" value="cold" />
            <el-option label="温和模式" value="mild" />
            <el-option label="温暖模式" value="warm" />
            <el-option label="夜间模式" value="night" />
          </el-select>
        </el-form-item>
        <el-form-item label="Kp">
          <el-input-number v-model="newPIDTemplate.kp" :min="0" :max="10" :step="0.1" />
        </el-form-item>
        <el-form-item label="Ki">
          <el-input-number v-model="newPIDTemplate.ki" :min="0" :max="5" :step="0.1" />
        </el-form-item>
        <el-form-item label="Kd">
          <el-input-number v-model="newPIDTemplate.kd" :min="0" :max="5" :step="0.1" />
        </el-form-item>
        <el-form-item label="设定值">
          <el-input-number v-model="newPIDTemplate.setPoint" :min="30" :max="80" />
        </el-form-item>
        <el-form-item label="采样周期">
          <el-input-number v-model="newPIDTemplate.sampleTime" :min="10" :max="300" :step="10" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPIDTemplateDialog = false">取消</el-button>
        <el-button type="primary" @click="createPIDTemplate">创建</el-button>
      </template>
    </el-dialog>

    <!-- 分时段曲线对话框 -->
    <el-dialog v-model="showTimeSegmentDialog" title="添加时段" width="600px">
      <el-form :model="newTimeSegment" label-width="100px">
        <el-form-item label="时段名称">
          <el-input v-model="newTimeSegment.segmentName" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-picker v-model="newTimeSegment.startTime" format="HH:mm" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-picker v-model="newTimeSegment.endTime" format="HH:mm" />
        </el-form-item>
        <el-form-item label="适用日期">
          <el-checkbox-group v-model="newTimeSegment.daysOfWeek">
            <el-checkbox :label="1">周一</el-checkbox>
            <el-checkbox :label="2">周二</el-checkbox>
            <el-checkbox :label="3">周三</el-checkbox>
            <el-checkbox :label="4">周四</el-checkbox>
            <el-checkbox :label="5">周五</el-checkbox>
            <el-checkbox :label="6">周六</el-checkbox>
            <el-checkbox :label="0">周日</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showTimeSegmentDialog = false">取消</el-button>
        <el-button type="primary" @click="createTimeSegment">创建</el-button>
      </template>
    </el-dialog>

    <!-- 策略对话框 -->
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
import { ref, onMounted, computed } from 'vue'
import { Sunny, TrendCharts, DataLine, Edit, Cloudy } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { climateApi } from '@/api'

const activeTab = ref('monitor')
const compensationCurveRef = ref(null)
const effectCompareRef = ref(null)
const trendRef = ref(null)

// 监控数据
const currentScene = ref('cold')
const currentConfig = ref('智能补偿')
const outdoorTemp = ref(-5)
const targetSupplyTemp = ref(52)
const actualSupplyTemp = ref(51.2)
const temperatureDeviation = ref(-0.8)
const valveOpening = ref(65)
const savingsRate = ref(12.5)
const compensationEnabled = ref(true)
const compensationMode = ref('climate')
const selectedCurve = ref('standard')
const indoorTarget = ref(20)
const minOutdoorTemp = ref(-20)
const maxOutdoorTemp = ref(10)
const curveType = ref('supply')
const compareRange = ref('week')
const integrationMode = ref('independent')
const enableAutoSwitch = ref(true)

// 控制参数
const showControlDialog = ref(false)
const controlParams = ref({
  target: 'valve',
  action: 'setOpening',
  value: 50,
  mode: 'manual'
})

// PID参数
const showPIDTemplateDialog = ref(false)
const currentPID = ref({
  kp: 2.5,
  ki: 0.8,
  kd: 0.3,
  setPoint: 52,
  minOutput: 0,
  maxOutput: 100,
  sampleTime: 30
})
const pidTemplates = ref([
  { id: 'TPL001', name: '严寒模式', sceneType: 'extreme_cold', sceneTypeName: '严寒', kp: 3.0, ki: 1.0, kd: 0.5, setPoint: 55, sampleTime: 30, isBuiltIn: true },
  { id: 'TPL002', name: '寒冷模式', sceneType: 'cold', sceneTypeName: '寒冷', kp: 2.5, ki: 0.8, kd: 0.3, setPoint: 52, sampleTime: 30, isBuiltIn: true },
  { id: 'TPL003', name: '温和模式', sceneType: 'mild', sceneTypeName: '温和', kp: 2.0, ki: 0.5, kd: 0.2, setPoint: 45, sampleTime: 60, isBuiltIn: true },
  { id: 'TPL004', name: '温暖模式', sceneType: 'warm', sceneTypeName: '温暖', kp: 1.5, ki: 0.3, kd: 0.1, setPoint: 40, sampleTime: 60, isBuiltIn: true },
  { id: 'TPL005', name: '夜间模式', sceneType: 'night', sceneTypeName: '夜间', kp: 1.0, ki: 0.2, kd: 0.1, setPoint: 45, sampleTime: 120, isBuiltIn: true }
])
const newPIDTemplate = ref({
  name: '',
  sceneType: 'mild',
  kp: 2.0,
  ki: 0.5,
  kd: 0.2,
  setPoint: 50,
  sampleTime: 60
})

// 分时段曲线
const showTimeSegmentDialog = ref(false)
const timeSegments = ref([
  { id: 1, segmentName: '白天', timeRange: '06:00-22:00', dayOfWeekName: '周一至周日', pointCount: 7 },
  { id: 2, segmentName: '夜间', timeRange: '22:00-06:00', dayOfWeekName: '周一至周日', pointCount: 7 }
])
const newTimeSegment = ref({
  segmentName: '',
  startTime: new Date(2024, 0, 1, 6, 0),
  endTime: new Date(2024, 0, 1, 22, 0),
  daysOfWeek: [1, 2, 3, 4, 5, 6, 0]
})

// 换热站配置
const stationConfig = ref({
  curveId: 'CURVE001',
  pidTemplateId: 'TPL002',
  customSetPoint: 50,
  minSupplyTemp: 35,
  maxSupplyTemp: 70,
  buildingHeatCoeff: 1.2,
  enableTimeSegment: true
})
const curves = ref([
  { id: 'CURVE001', name: '标准补偿曲线' },
  { id: 'CURVE002', name: '节能补偿曲线' },
  { id: 'CURVE003', name: '舒适补偿曲线' }
])

// 策略管理
const showEditDialog = ref(false)
const showStrategyDialog = ref(false)
const editCurveName = ref('')
const strategyList = ref([
  { name: '标准补偿', type: '气候', tempRange: '-20~10°C', status: true },
  { name: '节能模式', type: '气候', tempRange: '-15~5°C', status: true },
  { name: '舒适模式', type: '气候', tempRange: '-10~15°C', status: false },
  { name: '夜间模式', type: '时间', tempRange: '-5~10°C', status: true }
])
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

const valveOpeningColor = computed(() => {
  if (valveOpening.value < 30) return '#f56c6c'
  if (valveOpening.value < 70) return '#e6a23c'
  return '#67c23a'
})

const getSceneTypeColor = (type) => {
  const colors = {
    extreme_cold: 'danger',
    cold: 'warning',
    mild: 'success',
    warm: 'info',
    night: ''
  }
  return colors[type] || 'info'
}

const onModeChange = () => {
  ElMessage.info(`已切换到${compensationMode.value}模式`)
  initCompensationCurve()
}

const onCurveChange = () => {
  ElMessage.info(`已切换到${selectedCurve.value}曲线`)
  initCompensationCurve()
}

const onIntegrationModeChange = (val) => {
  ElMessage.info(`已切换到${val === 'independent' ? '独立模块' : '一网集成'}模式`)
}

const updateCurve = () => {
  initCompensationCurve()
}

const saveConfig = () => {
  ElMessage.success('配置已保存')
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
  ElMessage.success(`${row.name}已${row.status ? '启用' : '禁用'}`)
}

const editStrategy = (row) => {
  newStrategy.value = { ...row }
  showStrategyDialog.value = true
}

const deleteStrategy = (row) => {
  strategyList.value = strategyList.value.filter(s => s.id !== row.id)
  ElMessage.success('策略已删除')
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
  showStrategyDialog.value = false
  newStrategy.value = { name: '', type: 'climate', tempRange: '', status: true }
  ElMessage.success('策略已创建')
  loadConfigs()
}

// PID参数相关
const savePIDParams = () => {
  climateApi.setPIDParams(1, currentPID.value).then(() => {
    ElMessage.success('PID参数已保存')
  }).catch(() => {
    ElMessage.success('PID参数已保存（本地模拟）')
  })
}

const resetPIDParams = () => {
  currentPID.value = { kp: 2.5, ki: 0.8, kd: 0.3, setPoint: 52, minOutput: 0, maxOutput: 100, sampleTime: 30 }
}

const applyPIDTemplate = (template) => {
  currentPID.value = {
    kp: template.kp,
    ki: template.ki,
    kd: template.kd,
    setPoint: template.setPoint,
    minOutput: 0,
    maxOutput: 100,
    sampleTime: template.sampleTime
  }
  ElMessage.success(`已应用${template.name}模板`)
}

const deletePIDTemplate = (row) => {
  pidTemplates.value = pidTemplates.value.filter(t => t.id !== row.id)
  ElMessage.success('模板已删除')
}

const createPIDTemplate = () => {
  showPIDTemplateDialog.value = false
  ElMessage.success('模板已创建')
}

// 分时段曲线相关
const editTimeSegment = (row) => {
  ElMessage.info(`编辑时段: ${row.segmentName}`)
}

const viewTimeSegmentCurve = (row) => {
  ElMessage.info(`查看时段曲线: ${row.segmentName}`)
}

const deleteTimeSegment = (row) => {
  timeSegments.value = timeSegments.value.filter(t => t.id !== row.id)
  ElMessage.success('时段已删除')
}

const createTimeSegment = () => {
  showTimeSegmentDialog.value = false
  ElMessage.success('时段已创建')
}

// 集成模式相关
const saveIntegrationMode = () => {
  ElMessage.success('集成模式已保存')
}

const onSceneChange = (val) => {
  ElMessage.info(`已切换到${val}场景`)
}

const switchSceneManual = () => {
  ElMessage.success('场景切换成功')
}

// 换热站配置相关
const saveStationConfig = () => {
  ElMessage.success('换热站配置已保存')
}

// 控制相关
const executeControl = () => {
  showControlDialog.value = false
  ElMessage.success('控制指令已下发')
}

const initCompensationCurve = () => {
  const chart = echarts.init(compensationCurveRef.value)
  
  let supplyData = []
  let returnData = []
  const xData = ['-20', '-15', '-10', '-5', '0', '5', '10']
  
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
}

const initEffectCompare = () => {
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

const initTrendChart = () => {
  const chart = echarts.init(trendRef.value)
  const hours = []
  const outdoorTemps = []
  const targetTemps = []
  const actualTemps = []
  
  for (let i = 0; i < 24; i++) {
    hours.push(`${i}:00`)
    outdoorTemps.push(Math.round(-10 + Math.random() * 15))
    targetTemps.push(Math.round(50 + Math.random() * 10))
    actualTemps.push(targetTemps[i] + Math.round((Math.random() - 0.5) * 2))
  }
  
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['室外温度', '目标供水温度', '实际供水温度'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: hours },
    yAxis: { type: 'value', name: '温度(°C)' },
    series: [
      { name: '室外温度', type: 'line', data: outdoorTemps, itemStyle: { color: '#909399' } },
      { name: '目标供水温度', type: 'line', data: targetTemps, itemStyle: { color: '#409eff' }, lineStyle: { type: 'dashed' } },
      { name: '实际供水温度', type: 'line', data: actualTemps, itemStyle: { color: '#67c23a' } }
    ]
  }
  chart.setOption(option)
}

onMounted(() => {
  climateApi.getStatistics().then(data => {
    currentScene.value = data.currentScene || 'cold'
    outdoorTemp.value = data.outdoorTemp || -5
    targetSupplyTemp.value = data.targetSupplyTemp || 52
    actualSupplyTemp.value = data.actualSupplyTemp || 51.2
    temperatureDeviation.value = data.temperatureDeviation || -0.8
    valveOpening.value = data.valveOpening || 65
    savingsRate.value = data.savingsRate || 12.5
  }).catch(() => {})
  
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
  initTrendChart()
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

.highlight-value {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.text-success {
  color: #67c23a;
}

.text-danger {
  color: #f56c6c;
}

.mode-option {
  padding: 10px 0;
}

.mode-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.mode-desc {
  font-size: 13px;
  color: #909399;
}

:deep(.el-tabs__content) {
  padding: 20px 0;
}
</style>
