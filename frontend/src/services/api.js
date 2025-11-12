import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Servicios de Habitaciones
export const habitacionesService = {
    getAll: () => api.get('/habitaciones'),
    getById: (id) => api.get(`/habitaciones/${id}`),
    create: (data) => api.post('/habitaciones', data),
    update: (id, data) => api.put(`/habitaciones/${id}`, data),
    delete: (id) => api.delete(`/habitaciones/${id}`),
};

// Servicios de Huéspedes
export const huespedesService = {
    getAll: () => api.get('/huespedes'),
    getById: (id) => api.get(`/huespedes/${id}`),
    create: (data) => api.post('/huespedes', data),
    update: (id, data) => api.put(`/huespedes/${id}`, data),
    delete: (id) => api.delete(`/huespedes/${id}`),
};

// Servicios de Reservas
export const reservasService = {
    getAll: () => api.get('/reservas'),
    getById: (id) => api.get(`/reservas/${id}`),
    create: (data) => api.post('/reservas', data),
    update: (id, data) => api.put(`/reservas/${id}`, data),
    delete: (id) => api.delete(`/reservas/${id}`),
};

// Servicios de Pagos
export const pagosService = {
    getAll: () => api.get('/pagos'),
    getById: (id) => api.get(`/pagos/${id}`),
    create: (data) => api.post('/pagos', data),
    update: (id, data) => api.put(`/pagos/${id}`, data),
    delete: (id) => api.delete(`/pagos/${id}`),
};

export default api;

