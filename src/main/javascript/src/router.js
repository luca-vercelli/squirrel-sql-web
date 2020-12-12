import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

export default new Router({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      component: () => import('@/views/dashboard/Index'),
      children: [
        // Dashboard
        {
          name: 'Dashboard',
          path: '',
          component: () => import('@/views/dashboard/Dashboard'),
        },
        // Login
        {
          name: 'Login',
          path: '/login',
          component: () => import('@/views/dashboard/Login'),
        },
        // Pages
        {
          name: 'User Profile',
          path: 'pages/user',
          component: () => import('@/views/dashboard/pages/UserProfile'),
        },
        {
          name: 'Notifications',
          path: 'components/notifications',
          component: () => import('@/views/dashboard/component/Notifications'),
        },
        {
          name: 'Icons',
          path: 'components/icons',
          component: () => import('@/views/dashboard/component/Icons'),
        },
        {
          name: 'Typography',
          path: 'components/typography',
          component: () => import('@/views/dashboard/component/Typography'),
        },
        // Tables
        {
          name: 'Regular Tables',
          path: 'tables/regular-tables',
          component: () => import('@/views/dashboard/tables/RegularTables'),
        },
        // Drivers
        {
          name: 'Drivers',
          path: 'drivers',
          component: () => import('@/views/dashboard/pages/Drivers'),
        },
        {
          name: 'New Driver',
          path: 'new-driver',
          component: () => import('@/views/dashboard/pages/Driver'),
        },
        {
          name: 'Driver',
          path: 'driver/:identifier',
          component: () => import('@/views/dashboard/pages/Driver'),
        },
        {
          name: 'Clone Driver',
          path: 'clone-driver/:origIdentifier',
          component: () => import('@/views/dashboard/pages/Driver'),
        },
        // Aliases
        {
          name: 'Aliases',
          path: 'aliases',
          component: () => import('@/views/dashboard/pages/Aliases'),
        },
        {
          name: 'New Alias',
          path: 'new-alias',
          component: () => import('@/views/dashboard/pages/Alias'),
        },
        {
          name: 'Alias',
          path: 'alias/:identifier',
          component: () => import('@/views/dashboard/pages/Alias'),
        },
        {
          name: 'Clone Alias',
          path: 'clone-alias/:origIdentifier',
          component: () => import('@/views/dashboard/pages/Alias'),
        },
        // Sessions
        {
          name: 'Open sessions',
          path: 'sessions',
          component: () => import('@/views/dashboard/pages/Sessions'),
        },
        {
          name: 'Session',
          path: 'session/:identifier',
          component: () => import('@/views/dashboard/pages/Session'),
        },
      ],
    },
  ],
})
