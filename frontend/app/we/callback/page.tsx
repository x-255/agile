'use client'

import { wechatCallbackApi } from '@/app/api/wechat'
import { useAppStore } from '@/app/store'
import { useRouter, useSearchParams } from 'next/navigation'
import { useEffect } from 'react'
import useSWR from 'swr'

interface UserInfo {
  name: string
  userid: string
  email?: string
  mobile?: string
  department?: number[]
  position?: string
}

export default function Page() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const code = searchParams.get('code')
  const setUserInfo = useAppStore((state) => state.setUserInfo)

  const {
    data: userInfo,
    error,
    isValidating: loading,
  } = useSWR<UserInfo>(code ? wechatCallbackApi(code) : null)

  useEffect(() => {
    if (!code) {
      throw new Error('No code found in URL')
    }
  }, [code])

  useEffect(() => {
    if (userInfo) {
      // 存储用户信息到缓存
      setUserInfo({
        name: userInfo.name,
        email: userInfo.email || '',
        mobile: userInfo.mobile || '',
      })
      // 跳转到用户信息展示页面
      router.replace(`/we/user`)
    }
  }, [userInfo, setUserInfo, router])

  if (loading) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50">
        <div className="text-center">
          <h1 className="mb-4 text-2xl font-bold text-gray-900">Loading...</h1>
          <p className="text-gray-600">正在获取用户信息，请稍候</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50">
        <div className="text-center">
          <h1 className="mb-4 text-2xl font-bold text-red-600">Error</h1>
          <p className="mb-4 text-gray-600">{error.message || String(error)}</p>
          <button
            onClick={() => router.refresh()}
            className="rounded-md bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
          >
            重试
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50">
      <div className="text-center">
        <h1 className="mb-4 text-2xl font-bold text-gray-900">正在处理...</h1>
        <p className="text-gray-600">获取用户信息成功，正在跳转...</p>
      </div>
    </div>
  )
}
