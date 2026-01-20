'use client'

import { useEffect, useState, useMemo } from 'react'
import useSWR from 'swr'
import * as ww from '@wecom/jssdk'
import { wecomSignatureApi } from '../api/auth'

export function useWecomInit() {
  const [isSdkReady, setIsSdkReady] = useState(false)

  // 使用useMemo来获取当前URL，避免在effect中调用setState
  const currentUrl = useMemo(() => {
    if (typeof window !== 'undefined') {
      return window.location?.href || ''
    }
    return ''
  }, [])

  const { data: signatureData } = useSWR(
    currentUrl ? wecomSignatureApi(currentUrl) : null,
    {
      revalidateOnFocus: false,
      revalidateOnReconnect: false,
      shouldRetryOnError: false,
    }
  )

  useEffect(() => {
    const initWecomSdk = () => {
      try {
        console.log('开始初始化企业微信SDK')
        console.log('当前window.location.href:', window.location.href)

        // 如果还没有获取到签名数据，先设置为就绪状态，允许用户点击按钮
        if (!signatureData) {
          setIsSdkReady(true)
          return
        }

        // 注册企业微信SDK - 使用从后端获取的有效签名
        ww.register({
          corpId: 'wwbe02f7abf8bd67b8',
          jsApiList: ['checkJsApi', 'getUserInfo', 'login'],
          // 使用从后端获取的有效签名
          getConfigSignature: async () => {
            console.log('使用从后端获取的签名:', signatureData)
            return {
              timestamp: signatureData.timestamp,
              nonceStr: signatureData.nonceStr,
              signature: signatureData.signature,
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
      } catch (error) {
        console.error('初始化企业微信SDK出错:', error)
        // 捕获到错误时也将SDK标记为就绪，允许用户尝试登录
        setIsSdkReady(true)
      }
    }

    if (typeof window !== 'undefined' && currentUrl) {
      initWecomSdk()
    }
  }, [currentUrl, signatureData])

  return { isSdkReady }
}
