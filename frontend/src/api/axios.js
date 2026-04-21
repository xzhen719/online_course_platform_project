//統一管理API
import axios from 'axios'

//自定義axios物件
const api = axios.create({

    //每個api的prefix
    baseURL: 'http://localhost:8080',

    headers: {
        'Content-Type': 'application/json',
    },
})

//發送請求前的預處理-> 自動抓取token, 並添加到header中
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token')

        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }

        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

//發送回應前的預處理 
api.interceptors.response.use(
    //成功
    (response) => response,

    // Error handler
    (error) => {
        //401 未授權
        if (error.response?.status === 401) {
            //token無效或過期
            //清除token及user
            localStorage.removeItem('token')
            localStorage.removeItem('user')

            //重新導向login頁 (reload)
            window.location.href = '/login'
        }

        console.error('API Error:', error.response?.data || error.message)

        return Promise.reject(error)
    }
)

export default api
