<template>
  <v-app-bar
    id="app-bar"
    absolute
    app
    color="transparent"
    flat
    height="75"
  >
    <v-btn
      class="mr-3"
      elevation="1"
      fab
      small
      @click="setDrawer(!drawer)"
    >
      <v-icon v-if="value">
        mdi-view-quilt
      </v-icon>

      <v-icon v-else>
        mdi-dots-vertical
      </v-icon>
    </v-btn>

    <v-toolbar-title
      class="hidden-sm-and-down font-weight-light"
      v-text="$route.name"
    />

    <v-spacer />

    <div class="mx-3" />

    <v-btn
      class="ml-2"
      min-width="0"
      text
      :title="$t('AliasesToolWindow.windowtitle')"
      to="/"
    >
      <v-icon>mdi-database</v-icon>
    </v-btn>
    <v-btn
      class="ml-2"
      min-width="0"
      text
      :title="$t('UserProfile.title')"
      to="/pages/user"
    >
      <v-icon>mdi-account</v-icon>
    </v-btn>
    <v-btn
      class="ml-2"
      min-width="0"
      text
      :title="$t('Action.logout')"
      @click="logout"
    >
      <v-icon>mdi-logout</v-icon>
    </v-btn>
  </v-app-bar>
</template>

<script>

  // Utilities
  import { mapState, mapMutations } from 'vuex'
  var $ = require('jquery')

  export default {
    name: 'DashboardCoreAppBar',

    components: {
    },

    props: {
      value: {
        type: Boolean,
        default: false,
      },
    },

    data: () => ({
      enableMock: process.env.VUE_APP_MOCK === 'true',
    }),

    computed: {
      ...mapState(['drawer']),
    },

    created: function () {
      // FIXME intended meaning: if user closes the window, perform logout. It does not work.
      document.addEventListener('beforeunload', this.logout)
    },

    methods: {
      ...mapMutations({
        setDrawer: 'SET_DRAWER',
      }),
      logout () {
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/JustGetOk' : process.env.BASE_URL + 'ws/DisconnectAllSessions',
          type: this.enableMock ? 'GET' : 'POST',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            localStorage.removeItem('authToken')
            that.$emit('unauthenticated')
            that.$router.push('/')
          },
          error: function (response) {
            console.log(response)
            localStorage.removeItem('authToken')
            that.$emit('unauthenticated')
            that.$router.push('/')
          },
        })
      },
    },
  }
</script>
