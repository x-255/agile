'use client'

import { useState, useEffect, useRef } from 'react'
import * as ww from '@wecom/jssdk'

export default function Page() {
  const loginPanelRef = useRef<unknown>(null)
  const [isSdkReady, setIsSdkReady] = useState(false)

  useEffect(() => {
    // 仅在客户端环境下初始化VConsole
    let vconsole: unknown = null
    if (typeof window !== 'undefined') {
      // 动态导入VConsole，避免服务器端渲染错误
      import('vconsole').then((VConsoleModule) => {
        vconsole = new VConsoleModule.default()
        console.log('VConsole初始化成功')
      })
    }
    const currentLoginPanel = loginPanelRef.current

    // 初始化企业微信SDK
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

    return () => {
      if (vconsole && typeof vconsole === 'object' && 'destroy' in vconsole) {
        ;(vconsole as { destroy: () => void }).destroy()
      }
      if (
        currentLoginPanel &&
        typeof currentLoginPanel === 'object' &&
        'destroy' in currentLoginPanel
      ) {
        ;(currentLoginPanel as { destroy: () => void }).destroy()
      }
    }
  }, [])

  const handleWecomInternalLogin = () => {
    console.log('企业微信内部浏览器登录按钮点击')
    console.log('当前isSdkReady状态:', isSdkReady)

    // 内部浏览器登录 - 构造企业微信OAuth授权URL
    const corpId = 'wwbe02f7abf8bd67b8'
    const redirectUri = encodeURIComponent(
      'http://wecom.perficientus.com.cn/we/callback'
    )
    const state = 'loginState' // 示例state，实际可用于防止CSRF攻击

    // 构造授权URL - 内部浏览器使用snsapi_base静默授权
    const authUrl = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${corpId}&redirect_uri=${redirectUri}&response_type=code&scope=snsapi_base&state=${state}#wechat_redirect`

    console.log('内部浏览器登录，跳转到授权页面:', authUrl)
    // 跳转到企业微信授权页面
    window.location.href = authUrl
  }

  const handleWecomExternalLogin = () => {
    console.log('企业微信外部浏览器登录按钮点击')
    console.log('当前isSdkReady状态:', isSdkReady)

    // 外部浏览器登录 - 构造企业微信扫码登录URL
    const corpId = 'wwbe02f7abf8bd67b8'
    const agentId = '1000002' // 企业微信应用的AgentID，必填参数
    const redirectUri = encodeURIComponent(
      'http://wecom.perficientus.com.cn/we/callback'
    )
    const state = 'loginState' // 示例state，实际可用于防止CSRF攻击

    // 构造扫码登录URL - 外部浏览器使用企业微信扫码登录，包含必填的agentid参数
    const authUrl = `https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=${corpId}&agentid=${agentId}&redirect_uri=${redirectUri}&state=${state}`

    console.log('外部浏览器登录，跳转到扫码页面:', authUrl)
    // 跳转到企业微信扫码登录页面
    window.location.href = authUrl
  }

  return (
    <>
      <div>
        <h1>We</h1>
        <div style={{ marginTop: '20px' }}>
          <button
            onClick={handleWecomInternalLogin}
            disabled={!isSdkReady}
            style={{
              padding: '10px 20px',
              marginRight: '10px',
              backgroundColor: isSdkReady ? '#0078d4' : '#ccc',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: isSdkReady ? 'pointer' : 'not-allowed',
            }}
          >
            {isSdkReady ? '企业微信内部浏览器登录' : '企业微信SDK初始化中...'}
          </button>
          <button
            onClick={handleWecomExternalLogin}
            style={{
              padding: '10px 20px',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
            }}
          >
            外部浏览器登录
          </button>
        </div>
      </div>
    </>
  )
}