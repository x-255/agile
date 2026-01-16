import { immer } from 'zustand/middleware/immer'

export interface UserInfo {
  name: string
  email: string
  mobile: string
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
