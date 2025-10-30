'use client'

import { LockOutlined, UserOutlined } from '@ant-design/icons'
import { App, Button, Card, Form, Input, Space, Typography } from 'antd'
import { useCookiesNext } from 'cookies-next'
import { useRouter } from 'next/navigation'
import { use } from 'react'
import useSWRMutation from 'swr/mutation'
import { loginApi } from '../../api/auth'

const { Title, Text } = Typography

interface LoginFormData {
  username: string
  password: string
}

interface LoginPageProps {
  searchParams: Promise<{ redirect?: string }>
}

export default function LoginPage({ searchParams }: LoginPageProps) {
  const { setCookie } = useCookiesNext()
  const router = useRouter()
  const [form] = Form.useForm<LoginFormData>()
  const { message } = App.useApp()
  const { redirect = '/admin/dashboard' } = use(searchParams)
  const { trigger, isMutating } = useSWRMutation('/auth/login', loginApi)

  const handleSubmit = async (values: LoginFormData) => {
    try {
      const { token, username } = await trigger(values)

      if (token && username) {
        setCookie('token', token)

        setCookie('username', username)

        message.success('登录成功')
        router.push(redirect)
      } else {
        message.error('登录失败，请检查账号密码')
      }
    } catch (error) {
      console.error('登录失败:', error)
      message.error('登录失败，请稍后重试')
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-linear-to-r from-gray-100 to-gray-200 p-4">
      <div className="w-full max-w-md">
        <Card
          title={
            <div className="text-center">
              <Title level={2} className="mb-0 text-gray-800">
                系统登录
              </Title>
              <Text className="text-gray-500">请输入账号密码登录</Text>
            </div>
          }
          className="overflow-hidden rounded-xl shadow-lg"
        >
          <Form
            initialValues={{
              username: 'admin',
              password: 'admin',
            }}
            form={form}
            onFinish={handleSubmit}
            layout="vertical"
            className="mt-4"
          >
            <Form.Item
              label="用户名"
              name="username"
              rules={[{ required: true, message: '请输入用户名' }]}
            >
              <Input
                prefix={<UserOutlined className="text-gray-400" />}
                placeholder="请输入用户名"
                size="large"
              />
            </Form.Item>

            <Form.Item
              label="密码"
              name="password"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <Input.Password
                prefix={<LockOutlined className="text-gray-400" />}
                placeholder="请输入密码"
                size="large"
              />
            </Form.Item>

            <Form.Item>
              <Space direction="vertical" className="w-full">
                <Button
                  type="primary"
                  htmlType="submit"
                  size="large"
                  loading={isMutating}
                  className="h-12 w-full text-base"
                >
                  登录
                </Button>
                <div className="text-center text-gray-500">
                  <Text>忘记密码？</Text>
                  <Text className="ml-2 cursor-pointer text-blue-500">
                    联系管理员
                  </Text>
                </div>
              </Space>
            </Form.Item>
          </Form>
        </Card>
      </div>
    </div>
  )
}
