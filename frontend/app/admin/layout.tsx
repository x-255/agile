import { Metadata } from 'next'
import AdminProvider from '../components/admin-provider'

export const metadata: Metadata = {
  title: {
    template: '%s | 敏捷评估系统后台管理',
    default: '敏捷评估系统后台管理',
  },
}

export default async function Layout({
  children,
}: {
  children: React.ReactNode
}) {
  return <AdminProvider>{children}</AdminProvider>
}
