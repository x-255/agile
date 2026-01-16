export const wechatCallbackApi = (code: string) =>
  `/auth/wechat-callback?code=${code}`
