'use client'

import { AntdRegistry } from '@ant-design/nextjs-registry'
import { App } from 'antd'
import SwrProvider from './swr-provider'

export interface AppProviderProps {
  children: React.ReactNode
}

export default function AppProvider({ children }: AppProviderProps) {
  return (
    <AntdRegistry>
      <App>
        <SwrProvider>{children}</SwrProvider>
      </App>
    </AntdRegistry>
  )
}
