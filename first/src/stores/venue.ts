import { defineStore } from 'pinia'
import type { Venue } from '../types'
import { api } from '../api'

export const useVenueStore = defineStore('venue', {
  state: () => ({
    venues: [] as Venue[],
  }),
  actions: {
    async fetchVenues(typeId?: number) {
      const res = await api.getVenues(typeId)
      this.venues = res.data
    },
  },
})
