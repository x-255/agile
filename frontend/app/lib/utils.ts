import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

export type AnyObject = Record<string, unknown>

export const isObject = (value: unknown): value is AnyObject => {
  return value !== null && typeof value === 'object'
}

export const formatSearchParams = (params: Record<string, unknown>) => {
  const searchParams = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null) return
    if (Array.isArray(value)) {
      value.forEach((item) => {
        searchParams.append(`${key}[]`, String(item))
      })
    } else {
      searchParams.append(key, String(value))
    }
  })
  const result = searchParams.toString()
  return result ? `?${result}` : ''
}

export const cn = (...inputs: ClassValue[]) => twMerge(clsx(inputs))

export const throwJSONError = (err: unknown) => {
  throw err instanceof Error
    ? err
    : isObject(err)
      ? new Error(JSON.stringify(err))
      : err
}
