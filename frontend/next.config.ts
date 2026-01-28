import type { NextConfig } from 'next'

const nextConfig: NextConfig = {
  output: 'standalone',
  allowedDevOrigins: ['wecom.perficientus.com.cn'],
  basePath: process.env.BASE_PATH ?? '',
}

export default nextConfig