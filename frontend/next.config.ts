import type { NextConfig } from 'next'

const nextConfig: NextConfig = {
  allowedDevOrigins: ['wecom.perficientus.com.cn'],
  basePath: process.env.BASE_PATH ?? '',
  trailingSlash: true,
}

export default nextConfig