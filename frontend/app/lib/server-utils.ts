'use server'

import { cookies } from 'next/headers'

export const getCookieServer = async (name: string) => {
  return (await cookies()).get(name)
}
