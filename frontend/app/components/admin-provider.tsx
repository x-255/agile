'use client'

import { App } from 'antd'

export default function AdminProvider({
  children,
}: {
  children: React.ReactNode
}) {
  return <App>{children}</App>
}
