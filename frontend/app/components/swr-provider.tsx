'use client'

import { App } from 'antd'
import { useCookiesNext } from 'cookies-next'
import { redirect, usePathname } from 'next/navigation'
import { Middleware, SWRConfig } from 'swr'
import { fetcher } from '../api'
import { hasOwn } from '../lib/utils'

export interface SwrProviderProps {
  children: React.ReactNode
}

const spreadData: Middleware = (useSWRNext) => (key, fetcher, config) => {
  const enhancedFetcher = fetcher
    ? (((...args: Parameters<typeof fetcher>) => {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        return Promise.resolve(fetcher(...args)).then((res: any) => {
          if (hasOwn(res as AnyObject, 'data')) {
            return res.data
          }
          return res
        })
      }) as typeof fetcher)
    : fetcher

  return useSWRNext(key, enhancedFetcher, config)
}

export default function SwrProvider({ children }: SwrProviderProps) {
  const { message } = App.useApp()
  const { deleteCookie } = useCookiesNext()
  const pathname = usePathname()

  return (
    <SWRConfig
      value={{
        fetcher,
        use: [spreadData],
        onError(err) {
          const msg = err?.message
          if (msg) {
            message.error(msg)
          }

          const code = err.code
          if (code === 401) {
            deleteCookie('token')
            deleteCookie('username')
            let redirectUrl = '/admin/login'
            if (pathname !== '/') {
              redirectUrl += `?redirect=${pathname}`
            }
            redirect(redirectUrl)
          }
        },
      }}
    >
      {children}
    </SWRConfig>
  )
}
