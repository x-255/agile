'use client'

import { useWecomInit } from './useWecomInit'
import { useWecomLogin } from './useWecomLogin'

export function useWecom() {
  const initResult = useWecomInit()
  const loginResult = useWecomLogin()
  
  return {
    ...initResult,
    ...loginResult
  }
}
