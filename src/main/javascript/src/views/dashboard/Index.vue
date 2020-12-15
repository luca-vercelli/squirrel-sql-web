<template>
  <div>
    <v-app v-if="authenticated">
      <dashboard-core-app-bar />
      <dashboard-core-drawer />
      <dashboard-core-view />
      <dashboard-core-settings />
    </v-app>
    <login-form
      v-else
      @authenticated="authenticated=true"
    />
  </div>
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
      LoginForm: () => import('./Login'),
    },

    data: () => ({
      expandOnHover: false,
      enableMock: true,
      authenticated: false,
    }),

    created: function () {
      if (!localStorage.getItem('authToken')) {
        this.authenticated = false
      } else {
        this.checkTokenIsValid()
      }
    },

    methods: {
      checkTokenIsValid: function () {
        // One random endpoint just to check token is valid
        this.editEnabled = false
        var that = this
        var url = this.enableMock ? process.env.BASE_URL + 'mock/CurrentUser.json' : '../ws/CurrentUser'
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
            localStorage.removeItem('authToken')
            this.authenticated = false
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
