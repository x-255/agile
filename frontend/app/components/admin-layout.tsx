'use client'

import { DownOutlined } from '@ant-design/icons'
import { Dropdown, Layout, MenuProps, Space } from 'antd'
import { useCookiesNext } from 'cookies-next'
import { useRouter } from 'next/navigation'
import AdminMenu from './admin-menu'

const { Header, Sider, Content } = Layout

export interface AdminLayoutProps {
  children: React.ReactNode
}

const layoutStyle = {
  overflow: 'hidden',
  height: '100vh',
}

const siderStyle: React.CSSProperties = {
  color: '#fff',
  paddingRight: 4,
  paddingTop: 64,
}

const headerStyle: React.CSSProperties = {
  color: '#fff',
  textAlign: 'right',
}

const contentStyle: React.CSSProperties = {
  minHeight: 120,
  maxHeight: 'calc(100vh - 64px)',
  overflow: 'auto',
  padding: 24,
}

export default function AdminLayout({ children }: AdminLayoutProps) {
  const { getCookie, deleteCookie } = useCookiesNext()

  const username = getCookie('username') || ''

  const items: MenuProps['items'] = [
    {
      key: 'logout',
      label: '退出登录',
    },
  ]

  const router = useRouter()

  const onClick: MenuProps['onClick'] = ({ key }) => {
    if (key === 'logout') {
      deleteCookie('username')
      deleteCookie('token')
      router.push('/admin/login')
    }
  }

  return (
    <Layout style={layoutStyle}>
      <Sider width="25%" style={siderStyle}>
        <AdminMenu />
      </Sider>
      <Layout>
        <Header style={headerStyle}>
          <Dropdown menu={{ items, onClick }}>
            <span className="cursor-pointer">
              <Space>
                {username}
                <DownOutlined />
              </Space>
            </span>
          </Dropdown>
        </Header>
        <Content style={contentStyle}>{children}</Content>
      </Layout>
    </Layout>
  )
}
