import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

// 创建Vue应用实例
const app = createApp(App)
// 创建Pinia状态管理实例
const pinia = createPinia()

// 注册Pinia状态管理
app.use(pinia)
// 注册Vue Router路由管理
app.use(router)
// 注册Element Plus UI组件库
app.use(ElementPlus)

// 注册所有Element Plus图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 挂载应用到DOM
app.mount('#app')
