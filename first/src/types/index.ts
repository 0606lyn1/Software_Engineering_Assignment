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
