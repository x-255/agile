import { immer } from 'zustand/middleware/immer'

interface UserInfo {
  name: string
  userid: string
  email?: string
  mobile?: string
  department?: number[]
  position?: string
}

export interface UserSlice {
  userInfo: UserInfo | null
  setUserInfo: (userInfo: UserInfo | null) => void
  clearUserInfo: () => void
}

export const createUserSlice = immer<UserSlice>((set) => ({
  userInfo: null,
  setUserInfo: (userInfo) => set({ userInfo }),
  clearUserInfo: () => set({ userInfo: null }),
}))
