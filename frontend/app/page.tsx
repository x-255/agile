'use client'

import { useEffect } from 'react'
import useSWR from 'swr'
import { getQuestionsApi } from './api/ai'
import { useAppStore } from './store'

export default function Page() {
  const questionnaireData = useAppStore((state) => state.questionnaireData)
  const setQuestionnaireData = useAppStore(
    (state) => state.setQuestionnaireData
  )
  const { error, isLoading, data } = useSWR(
    questionnaireData.length > 0 ? null : getQuestionsApi
  )

  useEffect(() => {
    if (data) {
      setQuestionnaireData(data)
    }
  }, [data, setQuestionnaireData])

  if (isLoading) {
    return <>loading...</>
  }

  if (error) {
    return <>error: {error.message}</>
  }

  return <>{JSON.stringify(questionnaireData, null, 2)}</>
}
