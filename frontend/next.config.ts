import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "standalone",
  // ブラウザからの /api/* リクエストをバックエンドに転送する
  // → CORS 不要、バックエンドの URL がブラウザに露出しない
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: `${process.env.API_URL ?? "http://localhost:8080"}/:path*`,
      },
    ];
  },
};

export default nextConfig;
