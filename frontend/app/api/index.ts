import { redirect } from 'next/navigation'
import {
  getCookieServer,
  getCurrentPathname,
  setCookieServer,
} from '../lib/serverUtils'
import { AnyObject, formatSearchParams, isObject } from '../lib/utils'

export interface ApiResponse<T> {
  data: T
  message: string
}

export type ApiInit = Omit<RequestInit, 'body'> & {
  body?: Record<string, unknown> | BodyInit
  params?: Record<string, unknown>
}

export const fetcher = async <T>(
  url: string | URL | Request,
  apiInit: ApiInit = {}
): Promise<{
  data: T
  message: string
}> => {
  const { body, params, ...restInit } = apiInit
  const init: RequestInit = { ...restInit }
  if (typeof url === 'string' && !url.startsWith('http')) {
    url = process.env.NEXT_PUBLIC_API_URL + url
  }

  if (!init.headers) {
    init.headers = {
      'Content-Type': 'application/json',
    }
  } else if (isObject(init.headers) && !init.headers['Content-Type']) {
    init.headers['Content-Type'] = 'application/json'
  }

  const token = await getCookieServer('token')

  if (token) {
    ;(init.headers as AnyObject)['Authorization'] = `Bearer ${token.value}`
  }

  if (body) {
    if (body instanceof FormData) {
      ;(init.headers as AnyObject)['Content-Type'] = 'multipart/form-data'
    } else if (init.method === 'POST') {
      if (isObject(body)) {
        init.body = JSON.stringify(body)
      } else {
        init.body = body
      }
    }
  }

  const search = formatSearchParams(params ?? {})

  return fetch(url + search, init)
    .then((res) => {
      if (!res.ok) {
        throw { code: res.status, message: res.statusText }
      }
      return res
    })
    .then((res) => res.json())
    .then(async (res) => {
      const { code, data, message } = res
      if (code !== 200) {
        if (code === 401) {
          setCookieServer('loginHasExpired', 'true')
          const pathname = await getCurrentPathname()
          let redirectPath = '/admin/login'
          if (pathname) {
            redirectPath += `?redirect=${pathname}`
          }
          redirect(redirectPath)
        }
        throw { code, message }
      }
      return { data, message, code: 200, error: '' }
    })
}
