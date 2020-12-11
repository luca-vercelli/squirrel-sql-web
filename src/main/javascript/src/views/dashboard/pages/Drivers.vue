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
        <v-btn
          color="success"
          class="mr-4"
          title="Create new driver"
          to="/new-driver"
        >
          <i
            aria-hidden="true"
            class="v-icon notranslate mdi mdi-plus-box theme--dark"
          />
        </v-btn>
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
              <v-btn
                color="success"
                class="mr-4"
                title="Edit"
                :to="'/driver/' + driver.identifier.string"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-pencil theme--dark"
                />
              </v-btn> &nbsp;
              <v-btn
                color="success"
                class="mr-4"
                title="Clone"
                :to="'/clone-driver/' + driver.identifier.string"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-content-copy theme--dark"
                />
              </v-btn> &nbsp;
              <v-btn
                color="error"
                class="mr-4"
                title="Delete"
                @click="deletingIdentifier = driver.identifier.string; showDeleteDialog = true"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-delete theme--dark"
                />
                </v-btn>
            </td>
          </tr>
        </tbody>
      </v-simple-table>
    </base-material-card>

    <v-dialog
      v-model="showDeleteDialog"
      persistent
      max-width="290"
    >
      <v-card>
        <v-card-title class="headline">
          Delete driver?
        </v-card-title>
        <v-card-text>This operation cannot be undone.</v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="error"
            @click="deleteDriver(); showDeleteDialog = false"
          >
            OK
          </v-btn>
          <v-btn
            @click="showDeleteDialog = false"
          >
            Undo
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
        showDeleteDialog: false,
        deletingIdentifier: null,
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
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'DELETE',
          url: this.enableMock ? process.env.BASE_URL + 'mock/JustGetOk' : `../ws/Drivers'(${this.deletingIdentifier})`,
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            that.loadDrivers()
            that.deletingIdentifier = null
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.deletingIdentifier = null
          },
        })
      },
    },
  }
</script>
