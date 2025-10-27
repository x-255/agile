'use client'

import { DashboardOutlined } from '@ant-design/icons'
import { Menu, MenuProps } from 'antd'

type MenuItem = Required<MenuProps>['items'][number]

export default function AdminMenu() {
  const items: MenuItem[] = [
    {
      key: '/admin/dashboard',
      label: '仪表盘',
      icon: <DashboardOutlined />,
    },
  ]

  return (
    <Menu
      defaultSelectedKeys={['1']}
      defaultOpenKeys={['sub1']}
      mode="inline"
      theme="dark"
      items={items}
    />
  )
}
