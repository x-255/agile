'use client'

import { useEffect, useRef } from 'react'

interface WeLayoutProps {
  children: React.ReactNode
}

export default function WeLayout({ children }: WeLayoutProps) {
  const vconsoleRef = useRef<unknown>(null)

  useEffect(() => {
    // 仅在客户端环境下初始化VConsole
    if (typeof window !== 'undefined') {
      // 动态导入VConsole，避免服务器端渲染错误
      import('vconsole').then((VConsoleModule) => {
        vconsoleRef.current = new VConsoleModule.default()
        console.log('VConsole初始化成功')
      })
    }

    return () => {
      // 销毁VConsole
      if (vconsoleRef.current && typeof vconsoleRef.current === 'object' && 'destroy' in vconsoleRef.current) {
        ;(vconsoleRef.current as { destroy: () => void }).destroy()
      }
    }
  }, [])

  return <div>{children}</div>
}
