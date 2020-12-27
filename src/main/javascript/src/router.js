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
        // Pages
        {
          name: 'User Profile',
          path: 'pages/user',
          component: () => import('@/views/dashboard/pages/UserProfile'),
        },
        {
          name: 'Tools',
          path: 'pages/tools',
          component: () => import('@/views/dashboard/pages/Tools'),
        },
        {
          name: 'Global Preferences', // can't we translate this?
          path: 'pages/settings',
          component: () => import('@/views/dashboard/pages/Settings'),
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
          path: '',
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
