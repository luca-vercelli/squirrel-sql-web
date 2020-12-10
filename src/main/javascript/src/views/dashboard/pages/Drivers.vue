<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-clipboard-text"
      title="Drivers"
      class="px-5 py-3"
    >
      <div class="text-right">
        <router-link
          class="v-btn v-size--default success"
          title="Create new driver"
          to="/new-driver"
        >
          <i
            aria-hidden="true"
            class="v-icon notranslate mdi mdi-plus-box theme--dark"
          />
        </router-link> &nbsp;
      </div>

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
            v-for="driver in drivers"
            :key="driver.identifier.string"
          >
            <td>{{ driver.name }}</td>
            <td class="text-right">
              <router-link
                class="v-btn v-size--default success"
                title="Edit"
                :to="'/driver/' + driver.identifier.string"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-pencil theme--dark"
                />
              </router-link> &nbsp;
              <router-link
                class="v-btn v-size--default success"
                title="Clone"
                :to="'/clone-driver/' + driver.identifier.string"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-content-copy theme--dark"
                />
              </router-link> &nbsp;
              <button
                class="v-btn v-size--default error"
                title="Delete"
                @click="deleteDriver"
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
    name: 'Drivers',

    data () {
      return {
        drivers: [],
        enableMock: true,
      }
    },

    computed: {
      wsUrl: function () {
        return this.enableMock ? process.env.BASE_URL + 'mock/Drivers.json' : '../ws/Drivers'
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
            that.drivers = response.data
            that.drivers.sort(that.cmpNames)
          },
        })
      },
      cmpNames: function (x, y) {
        var name1 = x.name ? x.name.toLowerCase() : ''
        var name2 = y.name ? y.name.toLowerCase() : ''
        return name1 < name2 ? -1 : name1 > name2 ? +1 : 0
      },
      deleteDriver: function () {
        // TODO
        alert('TODO')
      },
    },
  }
</script>
