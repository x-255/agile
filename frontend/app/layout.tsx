import { AntdRegistry } from '@ant-design/nextjs-registry'
import type { Metadata } from 'next'
import { Geist, Geist_Mono } from 'next/font/google'
import './globals.css'
import Fix from './lib/fix'

const geistSans = Geist({
  variable: '--font-geist-sans',
  subsets: ['latin'],
})

const geistMono = Geist_Mono({
  variable: '--font-geist-mono',
  subsets: ['latin'],
})

export const metadata: Metadata = {
  title: {
    template: '%s | 敏捷评估系统',
    default: '敏捷评估系统',
  },
  description: '一个基于敏捷理念的团队评估与反馈平台',
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="zh-CN">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
      >
        <Fix />
        <AntdRegistry>{children}</AntdRegistry>
      </body>
    </html>
  )
}
