<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-clipboard-text"
      title="Aliases"
      class="px-5 py-3"
    >
      <v-simple-table>
        <thead>
          <tr>
            <th class="primary--text">
              Name
            </th>
            <th class="text-right primary--text">
              Actions
            </th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="alias in aliases"
            :key="alias.identifier.string"
          >
            <td>{{ alias.name }}</td>
            <td class="text-right">
              <button
                class="v-btn v-size--default success"
                title="Edit"
                @click="connect"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-connection theme--dark"
                />
              </button> &nbsp;
              <button
                class="v-btn v-size--default success"
                title="Edit"
                @click="editAlias"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-pencil theme--dark"
                />
              </button> &nbsp;
              <button
                class="v-btn v-size--default success"
                title="Clone"
                @click="cloneAlias"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-content-copy theme--dark"
                />
              </button> &nbsp;
              <button
                class="v-btn v-size--default error"
                title="Delete"
                @click="deleteAlias"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-delete theme--dark"
                />
              </button>
            </td>
          </tr>
        </tbody>
      </v-simple-table>
    </base-material-card>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'Aliases',

    data () {
      return {
        aliases: [],
        enableMock: true,
      }
    },

    computed: {
      wsUrl: function () {
        return this.enableMock ? process.env.BASE_URL + 'mock/Aliases.json' : '../ws/Aliases'
      },
    },

    created: function () {
      this.loadDrivers()
    },

    methods: {
      loadDrivers: function () {
        var url = this.wsUrl
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.aliases = response.data
            that.aliases.sort(that.cmpNames)
          },
        })
      },
      cmpNames: function (x, y) {
        var name1 = x.name ? x.name.toLowerCase() : ''
        var name2 = y.name ? y.name.toLowerCase() : ''
        return name1 < name2 ? -1 : name1 > name2 ? +1 : 0
      },
      connect: function () {
      },
      editAlias: function () {
      },
      cloneAlias: function () {
      },
      deleteAlias: function () {
      },
    },
  }
</script>
