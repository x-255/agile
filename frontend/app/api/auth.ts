import { createMutationFetcher } from '.'

export const loginApi = createMutationFetcher<
  {
    username: string
    password: string
  },
  { token: string; username: string }
>({
  method: 'POST',
})

export const testApi = '/auth/test'
