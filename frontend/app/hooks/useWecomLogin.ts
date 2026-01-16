'use client'

import { WECOM_CONFIG } from '@/app/config/wecom'

export function useWecomLogin() {
  const handleWecomInternalLogin = () => {
    console.log('企业微信内部浏览器登录按钮点击')

    // 内部浏览器登录 - 构造企业微信OAuth授权URL
    const { corpId, agentId, redirectUri } = WECOM_CONFIG
    const state = 'loginState' // 示例state，实际可用于防止CSRF攻击

    // 构造授权URL - 内部浏览器使用snsapi_privateinfo获取详细信息
    const authUrl = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${corpId}&agentid=${agentId}&redirect_uri=${encodeURIComponent(redirectUri)}&response_type=code&scope=snsapi_privateinfo&state=${state}#wechat_redirect`

    console.log('内部浏览器登录，跳转到授权页面:', authUrl)
    // 跳转到企业微信授权页面
    window.location.href = authUrl
  }

  const handleWecomExternalLogin = () => {
    console.log('企业微信外部浏览器登录按钮点击')

    // 外部浏览器登录 - 构造企业微信扫码登录URL
    const { corpId, agentId, redirectUri } = WECOM_CONFIG
    const state = 'loginState' // 示例state，实际可用于防止CSRF攻击

    // 构造扫码登录URL - 外部浏览器使用企业微信扫码登录，包含必填的agentid参数
    const authUrl = `https://login.work.weixin.qq.com/wwlogin/sso/login?appid=${corpId}&agentid=${agentId}&redirect_uri=${encodeURIComponent(redirectUri)}&scope=snsapi_privateinfo&state=${state}#wechat_redirect`

    console.log('外部浏览器登录，跳转到扫码页面:', authUrl)
    // 跳转到企业微信扫码登录页面
    window.location.href = authUrl
  }

  return {
    handleWecomInternalLogin,
    handleWecomExternalLogin,
  }
}
