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
  emailReminderEnabled?: boolean
  createdAt: string
}

export interface Venue {
  id: number
  name: string
  typeId: number
  price: number
  description: string
  notes: string
  managerUserId?: number | null
}

export interface VenuePayload {
  name: string
  typeId?: number
  price?: number
  description?: string
  notes?: string
  managerUserId?: number | null
}

export interface Reservation {
  id: number
  userId: number
  venueId: number
  startTime: string
  endTime: string
  status: string
  checkinCode?: string
  checkedInAt?: string
  cancelDeadline?: string
  cancelReason?: string
  appealReason?: string
  appealStatus?: string
  reminderStatuses?: string[]
  createdAt?: string
}

export interface ReservationSlot {
  startTime: string
  endTime: string
  status: string
  label: string
}

export interface ReservationRule {
  id?: number
  venueId?: number
  advanceDays: number
  cancelBeforeHours: number
  maxHoursPerBooking: number
  dailyLimit: number
  weeklyLimit: number
  openTime: string
  closeTime: string
  slotMinutes: number
}

export interface Announcement {
  id: number
  title: string
  content: string
  level: string
  createdAt: string
}

export interface UserNotification {
  id: number
  userId: number
  title: string
  content: string
  type: string
  readFlag: boolean
  createdAt: string
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
