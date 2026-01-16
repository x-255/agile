'use client'

import { useEffect, useState } from 'react'
import * as ww from '@wecom/jssdk'

export function useWecomInit() {
  const [isSdkReady, setIsSdkReady] = useState(false)

  useEffect(() => {
    const initWecomSdk = () => {
      try {
        console.log('开始初始化企业微信SDK')
        console.log('当前window.location.href:', window.location.href)

        // 直接设置SDK为就绪状态，先让用户可以点击按钮
        setIsSdkReady(true)

        // 注册企业微信SDK - 使用非async方式，避免可能的Promise问题
        ww.register({
          corpId: 'wwbe02f7abf8bd67b8',
          jsApiList: ['checkJsApi', 'getUserInfo', 'login'],
          // 简化签名获取逻辑，使用本地生成的测试签名
          getConfigSignature: async (url) => {
            console.log('getConfigSignature called with URL:', url)
            // 直接返回测试签名，不依赖后端API
            return {
              timestamp: Math.floor(Date.now() / 1000).toString(),
              nonceStr: 'test_nonce_123',
              signature: 'test_signature_456',
            }
          },
          onConfigSuccess: () => {
            console.log('企业微信SDK初始化成功 - onConfigSuccess')
            setIsSdkReady(true)
          },
          onConfigFail: (err) => {
            console.error('企业微信SDK初始化失败 - onConfigFail:', err)
            // 即使配置失败，也允许用户尝试登录
            setIsSdkReady(true)
          },
        })

        console.log('企业微信SDK register方法调用完成')
        console.log('企业微信SDK注册调用完成')
      } catch (error) {
        console.error('初始化企业微信SDK出错:', error)
        // 捕获到错误时也将SDK标记为就绪，允许用户尝试登录
        setIsSdkReady(true)
      }
    }

    if (typeof window !== 'undefined') {
      initWecomSdk()
    }
  }, [])

  return { isSdkReady }
}
