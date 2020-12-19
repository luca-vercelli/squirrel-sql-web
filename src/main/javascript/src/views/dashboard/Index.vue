<template>
  <div>
    <v-app v-if="authenticated">
      <dashboard-core-app-bar @unauthenticated="authenticated=false" />
      <dashboard-core-drawer />
      <dashboard-core-view
        @notify="notify=$event"
        @ajax-error="ajaxErrorResponse=$event"
      />
      <dashboard-core-settings />
    </v-app>
    <login-form
      v-else
      @authenticated="authenticated=true"
    />
    <notify
      :notify="notify"
      :ajax-error-response="ajaxErrorResponse"
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
      Notify: () => import('./component/Notify'),
    },

    data: () => ({
      expandOnHover: false,
      enableMock: process.env.VUE_APP_MOCK === 'true',
      authenticated: false,
      ajaxErrorResponse: null,
      notify: null,
    }),

    watch: {
      notify: function () {
        console.log('DEBUG notify =', this.notify)
      },
    },

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
        var url = this.enableMock ? process.env.BASE_URL + 'mock/CurrentUser.json' : process.env.BASE_URL + 'ws/CurrentUser'
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            console.log('Pre-authentication successful')
            that.authenticated = true
          },
          error: function (response) {
            localStorage.removeItem('authToken')
            that.authenticated = false
            console.log('Pre-authentication failed')
            if (response.status !== 401 && response.status !== 403) {
              console.log(response)
            }
          },
        })
      },
    },
  }
</script>
