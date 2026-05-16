import http from './http'

export const api = {
  login: (data: { username: string; password: string }) => http.post('/auth/login', data),
  register: (data: { username: string; password: string; email: string }) => http.post('/auth/register', data),
  getVenues: (typeId?: number) => http.get('/venues', { params: { typeId } }),
  getVenueTypes: () => http.get('/venue-types'),
  createReservation: (data: { venueId: number; startTime: string; endTime: string }) => http.post('/reservations', data),
  getMyReservations: () => http.get('/reservations'),
  deleteReservation: (id: number) => http.delete(`/reservations/${id}`),
  getComments: (venueId: number) => http.get('/comments', { params: { venueId } }),
  createComment: (data: { venueId: number; content: string }) => http.post('/comments', data),
  getUsers: () => http.get('/users'),
  getUserById: (id: number) => http.get(`/users/${id}`),
  updateUser: (id: number, data: { username: string; email: string; role?: string }) => http.put(`/users/${id}`, data),
}
