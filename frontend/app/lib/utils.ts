import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

export const isObject = (value: unknown): value is AnyObject => {
  return value !== null && typeof value === 'object'
}

const hasOwnProperty = Object.prototype.hasOwnProperty
export const hasOwn = (
  val: AnyObject,
  key: ObjectKeys
): key is keyof typeof val => hasOwnProperty.call(val, key)

export const formatSearchParams = (params: AnyObject) => {
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
