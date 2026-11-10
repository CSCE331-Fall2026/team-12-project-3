import react from '@vitejs/plugin-react'
import { defineConfig, loadEnv } from 'vite'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [react()],
    define: {
      'import.meta.env.VITE_BACKEND_URL': JSON.stringify(env.BACKEND_URL),
    },
    ...(env.BACKEND_URL ? {
      server: {
        proxy: {
          '/api': {
            target: env.BACKEND_URL,
            changeOrigin: true,
            rewrite: (path: string) => path.replace(/^\/api/, ''),
          },
        },
      },
    } : {}),
  }
})
