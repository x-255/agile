import { immer } from 'zustand/middleware/immer'

export interface Question {
  content: string
  options: string[]
  choice: string
}

export interface QuestionnaireItem {
  dimension: string
  questions: Question[]
}

export interface QuestionSlice {
  questionnaireData: QuestionnaireItem[]
  setQuestionnaireData: (data: QuestionnaireItem[]) => void
  chooseAnswer: (
    dimensionIndex: number,
    questionIndex: number,
    answer: string
  ) => void
}

export const createQuestionSlice = immer<QuestionSlice>((set) => ({
  questionnaireData: [],
  setQuestionnaireData: (data) => set({ questionnaireData: data }),
  chooseAnswer: (dimensionIndex, questionIndex, answer) =>
    set((state) => {
      const dimension = state.questionnaireData[dimensionIndex]
      if (dimension) {
        const question = dimension.questions[questionIndex]
        if (question) {
          question.choice = answer
        }
      }
    }),
}))
