<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-v-component
      heading="Simple Tables"
      link="components/simple-tables"
    />

    <base-material-card
      icon="mdi-clipboard-text"
      title="Simple Table"
      class="px-5 py-3"
    >
      <v-simple-table>
        <thead>
          <tr>
            <th class="primary--text">
              Name
            </th>
            <th class="primary--text">
              Actions
            </th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="driver in drivers"
            :key="driver.identifier"
          >
            <td>{{ driver.name }}</td>
            <td>
              <button class="v-btn v-size--default secondary">
                Connect
              </button> &nbsp;
              <button class="v-btn v-size--default secondary">
                Edit
              </button> &nbsp;
              <button class="v-btn v-size--default secondary">
                Clone
              </button> &nbsp;
              <button class="v-btn v-size--default error">
                Delete
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
      /* driversComputed: function () {
        return this.drivers
      }, */
    },

    watch: {
      drivers: function (val) {
        console.log('here')
        this.driversComputed = val
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
            console.log('loaded drivers: ', that.drivers)
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
