import { create } from 'zustand'
import { createJSONStorage, devtools, persist } from 'zustand/middleware'
import { createQuestionSlice, QuestionSlice } from './questions-slice'

type AppStore = QuestionSlice

export const useAppStore = create<AppStore>()(
  devtools(
    persist(
      (...a) => ({
        ...createQuestionSlice(...a),
      }),
      {
        name: 'app-store',
        storage: createJSONStorage(() => sessionStorage),
      }
    )
  )
)
