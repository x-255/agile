import { getCookieServer } from '../lib/server-utils'
import { formatSearchParams, isObject } from '../lib/utils'

export type URLType = string | URL | Request

export type ApiBody = AnyObject | BodyInit

export type ApiInit = Omit<RequestInit, 'body'> & {
  body?: ApiBody
  params?: AnyObject
}

export interface MutationArg<T> {
  arg: T
}

export interface ApiResponse<T = unknown> {
  data: T
  message: string
  code: number
}

export const fetcher = async <R>(
  url: URLType,
  apiInit: ApiInit = {}
): Promise<ApiResponse<R>> => {
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
        throw { code, message }
      }
      return { data, message, code: 200 }
    })
}

export const createMutationFetcher =
  <T, R = ApiResponse>(init: ApiInit = {}) =>
  (key: string, { arg }: MutationArg<T>) =>
    fetcher<R>(key, { ...init, body: arg as ApiBody }).then((res) => res.data)
