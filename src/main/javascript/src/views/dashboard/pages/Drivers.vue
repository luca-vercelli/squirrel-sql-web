<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-chip"
      :title="$t('DriversToolWindow.windowtitle')"
      class="px-5 py-3"
    >
      <div class="text-right">
        <v-btn
          color="success"
          class="mr-4"
          :title="$t('DriverInternalFrame.adddriver')"
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
              {{ $t('DriverInternalFrame.name') }}
            </th>
            <th class="text-right primary--text">
              {{ $t('Action.actions') }}
            </th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="driver in drivers"
            :key="driver.identifier"
          >
            <td>{{ driver.name }}</td>
            <td class="text-right">
              <v-btn
                color="success"
                class="mr-4"
                :title="$t('Action.edit')"
                :to="'/driver/' + driver.identifier"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-pencil theme--dark"
                />
              </v-btn> &nbsp;
              <v-btn
                color="success"
                class="mr-4"
                :title="$t('Action.clone')"
                :to="'/clone-driver/' + driver.identifier"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-content-copy theme--dark"
                />
              </v-btn> &nbsp;
              <v-btn
                color="error"
                class="mr-4"
                :title="$t('Action.delete')"
                @click="openDeleteDialog(driver)"
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
          {{ $t('Action.confirm') }}
        </v-card-title>
        <v-card-text>{{ deletingMessage }}</v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="error"
            @click="deleteDriver(); showDeleteDialog = false"
          >
            {{ $t('Action.ok') }}
          </v-btn>
          <v-btn
            @click="showDeleteDialog = false"
          >
            {{ $t('Action.cancel') }}
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
        enableMock: process.env.VUE_APP_MOCK === 'true',
        showDeleteDialog: false,
        deletingDriver: null,
        deletingMessage: null,
      }
    },

    computed: {
      wsUrl: function () {
        return this.enableMock ? process.env.BASE_URL + 'mock/Drivers.json' : process.env.BASE_URL + 'ws/Drivers'
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
      openDeleteDialog (driver) {
        this.deletingDriver = driver
        this.deletingMessage = this.$t('DeleteDriverCommand.comfirm', [driver.name])
        // FIXME should honour DeleteDriverCommand.used
        this.showDeleteDialog = true
      },
      deleteDriver: function () {
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'DELETE',
          url: this.enableMock ? process.env.BASE_URL + 'mock/JustGetOk' : process.env.BASE_URL + `ws/Drivers(${this.deletingDriver.identifier})`,
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            that.loadDrivers()
            that.deletingDriver = null
          },
          error: function (response) {
            that.deletingDriver = null
            that.$emit('ajax-error', response)
          },
        })
      },
    },
  }
</script>
