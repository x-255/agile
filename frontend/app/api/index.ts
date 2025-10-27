import { getCookie } from 'cookies-next'
import { AnyObject, formatSearchParams, isObject } from '../lib/utils'

export type ApiInit = Omit<RequestInit, 'body'> & {
  body?: Record<string, unknown> | BodyInit
  params?: Record<string, unknown>
}

export const fetcher = async <T>(
  url: string | URL | Request,
  apiInit: ApiInit = {}
): Promise<T> => {
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

  const token = getCookie('token')

  if (token) {
    ;(init.headers as AnyObject)['Authorization'] = `Bearer ${token}`
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
        throw new Error(res.statusText)
      }
      return res
    })
    .then((res) => res.json())
    .then((res) => {
      if (res.code !== 200) {
        throw new Error(res.message)
      }
      return res.data
    })
}
