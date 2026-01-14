'use client'

import { useEffect } from 'react'

export default function Page() {
  useEffect(() => {
    // 仅在客户端环境下初始化VConsole
    let vconsole: unknown = null
    if (typeof window !== 'undefined') {
      // 动态导入VConsole，避免服务器端渲染错误
      import('vconsole').then((VConsoleModule) => {
        vconsole = new VConsoleModule.default()
        console.log('VConsole initialized in callback page')
        console.log(window.location)
      })
    }

    return () => {
      // 销毁VConsole
      if (vconsole && typeof vconsole === 'object' && 'destroy' in vconsole) {
        ;(vconsole as { destroy: () => void }).destroy()
      }
    }
  }, [])

  return (
    <>
      <h1>Callback</h1>
    </>
  )
}
