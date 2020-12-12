<template>
  <v-app>
    <dashboard-core-app-bar />

    <dashboard-core-drawer />

    <dashboard-core-view />

    <dashboard-core-settings />
  </v-app>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'DashboardIndex',

    components: {
      DashboardCoreAppBar: () => import('./components/core/AppBar'),
      DashboardCoreDrawer: () => import('./components/core/Drawer'),
      DashboardCoreSettings: () => import('./components/core/Settings'),
      DashboardCoreView: () => import('./components/core/View'),
    },

    data: () => ({
      expandOnHover: false,
      enableMock: true,
    }),

    created: function () {
      this.loadUser()
    },

    methods: {
      loadUser: function () {
        this.editEnabled = false
        var that = this
        var url = this.enableMock ? process.env.BASE_URL + 'mock/CurretUser.json' : '../ws/CurrentUser'
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            console.log('Authentication successful')
          },
          error: function (response) {
            if (response.status === 401 || response.status === 403) {
              that.$router.push('/login')
            } else {
              console.log('Error during authentication', response) // TODO show msg
            }
          },
        })
      },
    },
  }
</script>
