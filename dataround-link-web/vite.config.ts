/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const baseUrl = '/datalink';

export default defineConfig({
  base: baseUrl,
  build: {
    outDir: '../mydp-datalink-svc/src/main/resources/static',
  },
  server: {
    proxy: {
      '/datalink/api': {
        target: 'http://localhost:5600',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/datalink\/api/, "/datalink/api/"),
        configure: (proxy, options) => {
          proxy.on('proxyReq', (proxyReq, req, res, options) => {
            proxyReq.setHeader('X-Forwarded-Port', '5173');
          });
        }
      }
     },
  },
  plugins: [react()],
})
