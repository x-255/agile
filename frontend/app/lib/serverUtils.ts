'use server'

import { cookies, headers } from 'next/headers'

export const getCookieServer = async (name: string) => {
  return (await cookies()).get(name)
}

export const setCookieServer = async (name: string, value: string) => {
  ;(await cookies()).set(name, value)
}

export const getCurrentPathname = async () => {
  const referer = (await headers()).get('referer')
  if (!referer) return ''
  const { pathname } = new URL(referer)
  return pathname === '/' ? '' : pathname
}
