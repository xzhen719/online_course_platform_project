import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router/router'

// Bootstrap
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
// Nordic Theme (Minimalist with soft rounded feel)
import './assets/nordic.css'


const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')

