import { create } from 'zustand'
import { createJSONStorage, devtools, persist } from 'zustand/middleware'
import { createQuestionSlice, QuestionSlice } from './questions-slice'
import { createUserSlice, UserSlice } from './user-slice'

type AppStore = QuestionSlice & UserSlice

export const useAppStore = create<AppStore>()(
  devtools(
    persist(
      (...a) => ({
        ...createQuestionSlice(...a),
        ...createUserSlice(...a),
      }),
      {
        name: 'app-store',
        storage: createJSONStorage(() => sessionStorage),
      }
    )
  )
)
