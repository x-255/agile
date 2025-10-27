import { fetcher } from '.'

export const loginApi = (body: { username: string; password: string }) => {
  return fetcher<{ token: string; username: string }>('/auth/login', {
    method: 'POST',
    body,
  })
}
