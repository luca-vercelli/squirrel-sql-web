<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-connection"
      title="Open sessions"
      class="px-5 py-3"
    >
      <v-simple-table>
        <thead>
          <tr>
            <th class="primary--text">
              Session title
            </th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="session in sessions"
            :key="session.identifier"
            @click="$router.push('/session/' + session.identifier)"
          >
            <td>{{ session.title }}</td>
          </tr>
        </tbody>
      </v-simple-table>
    </base-material-card>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'Sessions',

    data () {
      return {
        sessions: [],
        enableMock: process.env.VUE_APP_MOCK === 'true',
      }
    },

    computed: {
      wsUrl: function () {
        return this.enableMock ? process.env.BASE_URL + 'mock/Sessions.json' : process.env.BASE_URL + 'ws/Session'
      },
    },

    created: function () {
      this.loadSessions()
    },

    methods: {
      loadSessions: function () {
        var that = this
        $.ajax({
          url: this.wsUrl,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.sessions = response.data
            that.sessions.sort(that.cmpNames)
          },
        })
      },
      cmpNames: function (x, y) {
        var name1 = x.name ? x.name.toLowerCase() : ''
        var name2 = y.name ? y.name.toLowerCase() : ''
        return name1 < name2 ? -1 : name1 > name2 ? +1 : 0
      },
    },
  }
</script>
