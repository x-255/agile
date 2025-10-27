import AdminLayout from '@/app/components/admin-layout'

export interface LayoutProps {
  children: React.ReactNode
}

export default function Layout({ children }: LayoutProps) {
  return <AdminLayout>{children}</AdminLayout>
}
