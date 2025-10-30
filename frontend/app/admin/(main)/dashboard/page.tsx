'use client'

import { createMutationFetcher } from '@/app/api'
import { testApi } from '@/app/api/auth'
import useSWR from 'swr'
import useSWRMutation from 'swr/mutation'

export default function Page() {
  const { data, error } = useSWR<string>(testApi)
  const { trigger } = useSWRMutation(
    '/auth/error',
    createMutationFetcher({ method: 'POST' })
  )

  if (error) throw error

  return (
    <>
      <div
        onClick={async () => {
          trigger({ method: 'POST' })
        }}
      >
        仪表盘
        <h1>{data}</h1>
      </div>
    </>
  )
}
