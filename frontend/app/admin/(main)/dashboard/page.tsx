'use client'

import { fetcher } from '@/app/api'
import { throwJSONError } from '@/app/lib/utils'
import useSWR from 'swr'

export default function Page() {
  const { data } = useSWR('/auth/test', fetcher)
  console.log(`data====`, data)

  return (
    <>
      <div
        onClick={async () => {
          await fetcher('/auth/error').catch(throwJSONError)
        }}
      >
        仪表盘
      </div>
    </>
  )
}
