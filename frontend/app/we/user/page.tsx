'use client'

import { useAppStore } from '@/app/store'
import Link from 'next/link'
import { useRouter } from 'next/navigation'

export default function UserPage() {
  const router = useRouter()
  const userInfo = useAppStore((state) => state.userInfo)
  const clearUserInfo = useAppStore((state) => state.clearUserInfo)

  const handleLogout = () => {
    // 清除用户信息并跳转到登录页面
    router.push('/we')
    clearUserInfo()
  }

  if (!userInfo) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50 p-4">
        <div className="w-full max-w-md rounded-lg bg-white p-6 shadow-md">
          <h1 className="mb-4 text-center text-2xl font-bold text-gray-900">
            没有用户信息
          </h1>
          <p className="mb-6 text-center text-gray-600">请先登录获取用户信息</p>
          <Link
            href="/we"
            className="block w-full rounded-md bg-blue-600 px-4 py-2 text-center text-white transition-colors hover:bg-blue-700"
          >
            返回登录页面
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50 p-4">
      <div className="w-full max-w-md rounded-lg bg-white p-6 shadow-md">
        <h1 className="mb-6 text-center text-2xl font-bold text-gray-900">
          用户信息
        </h1>

        <div className="space-y-4">
          <div className="flex items-center justify-between rounded-md bg-gray-50 p-3">
            <span className="font-medium text-gray-700">姓名：</span>
            <span className="text-gray-900">{userInfo.name}</span>
          </div>

          <div className="flex items-center justify-between rounded-md bg-gray-50 p-3">
            <span className="font-medium text-gray-700">邮箱：</span>
            <span className="text-gray-900">{userInfo.email || '未设置'}</span>
          </div>

          <div className="flex items-center justify-between rounded-md bg-gray-50 p-3">
            <span className="font-medium text-gray-700">手机号：</span>
            <span className="text-gray-900">{userInfo.mobile || '未设置'}</span>
          </div>

          <div className="mt-6 space-y-3">
            <button
              onClick={handleLogout}
              className="w-full rounded-md bg-red-600 px-4 py-2 text-white transition-colors hover:bg-red-700"
            >
              退出登录
            </button>
            <Link
              href="/we"
              className="w-full rounded-md bg-gray-600 px-4 py-2 text-white transition-colors hover:bg-gray-700"
            >
              返回上一页
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
