'use client'

import { useEffect } from 'react'
import type { ResultStatusType } from 'antd/lib/result'
import { Result, Button } from 'antd'

const getStatus = (code: ResultStatusType): ResultStatusType => {
  if (typeof code === 'string') return code
  if (code === 404) return '404'
  if (code >= 500) return '500'
  if (code >= 400) return '403'
  return 'error'
}

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string }
  reset: () => void
}) {
  let code: ResultStatusType = 'error'
  let message = error.message ?? '系统开小差了，请稍后再试'
  try {
    const parsed = JSON.parse(error.message)
    code = parsed.code || 500
    message = parsed.message || message
  } catch {}

  useEffect(() => {
    console.log(`error page====`, error)
  }, [error])

  return (
    <div className="flex h-full items-center justify-center">
      <Result
        status={getStatus(code)}
        title={String(code)}
        subTitle={message}
        extra={
          <Button type="primary" onClick={reset}>
            刷新重试
          </Button>
        }
      />
    </div>
  )
}
