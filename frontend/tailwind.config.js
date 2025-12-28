/** @type {import('tailwindcss').Config} */
export default {
  // 这里的 content 非常关键，它告诉 Tailwind 扫描这些文件中的类名
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      // 你可以在这里自定义品牌颜色
      colors: {
        primary: {
          DEFAULT: '#2563eb',
          light: '#60a5fa',
          dark: '#1d4ed8',
        }
      },
      // 增加阴影扩展，让商城更有质感
      boxShadow: {
        'card': '0 2px 15px -3px rgba(0, 0, 0, 0.07), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
        'card-hover': '0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
      }
    },
  },
  plugins: [],
}