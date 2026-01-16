'use client'

import { useWecom } from '../hooks/useWecom'

export default function Page() {
  const { isSdkReady, handleWecomInternalLogin, handleWecomExternalLogin } =
    useWecom()

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