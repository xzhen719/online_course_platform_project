
import api from './axios'

export const generateDescription = async (prompt) => {
    const response = await api.post('/api/chat', { prompt })
    return response.data
}