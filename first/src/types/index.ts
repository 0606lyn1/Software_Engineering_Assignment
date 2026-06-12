export interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export interface UserInfo {
  id: number
  username: string
  email: string
  role: string
  createdAt: string
}

export interface Venue {
  id: number
  name: string
  typeId: number
  price: number
  description: string
  notes: string
}

export interface Reservation {
  id: number
  userId: number
  venueId: number
  startTime: string
  endTime: string
  status: string
}

export interface CommentItem {
  id: number
  userId: number
  venueId: number
  content: string
  createdAt: string
}

export interface VenueType {
  id: number
  name: string
}

export interface VenueOps {
  id: number
  venueId: number
  maintenanceStatus: string
  cleaningStatus: string
  lightingStatus: string
  equipmentStatus: string
  responsiblePerson?: string
  contactPhone?: string
  lastInspector?: string
  lastCheckedAt: string
  remark?: string
}

export interface VenueOpsPayload {
  maintenanceStatus: string
  cleaningStatus: string
  lightingStatus: string
  equipmentStatus: string
  responsiblePerson?: string
  contactPhone?: string
  remark?: string
}

export interface VenueAvailability {
  venueId: number
  available: boolean
  status: string
  reason: string
}
