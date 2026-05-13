import axios from 'axios'

export const briefingApi = {
  list: (page = 1, size = 10) => axios.get('/api/briefings', { params: { page, size } }).then(res => res.data),
  getToday: () => axios.get('/api/briefings/today').then(res => res.data),
  getById: (id) => axios.get(`/api/briefings/${id}`).then(res => res.data),
  generate: (token) => axios.post('/api/briefings/generate', null, {
    headers: { 'X-Trigger-Token': token }
  }).then(res => res.data)
}
