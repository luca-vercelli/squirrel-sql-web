<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-clipboard-text"
      title="Alias"
      class="px-5 py-3"
    >
      <v-form
        ref="form"
        v-model="valid"
        lazy-validation
      >
        <v-text-field
          v-model="alias.name"
          label="Name"
          required
        />

        <v-select
          v-model="alias.driverIdentifier"
          :items="drivers"
          item-text="name"
          item-value="identifier"
          label="Choose driver"
          required
          @change="onChangeDriver"
        />

        <v-text-field
          v-model="alias.url"
          label="URL template"
          required
        />

        <v-text-field
          v-model="alias.userName"
          label="Username"
          class="md-4"
          required
        />

        <v-text-field
          v-model="alias.password"
          label="Password"
          required
        />

        <v-switch
          v-model="alias.autoLogon"
          label="Auto logon"
        />

        <v-switch
          v-model="alias.autoConnect"
          label="Connect at startup (NOT WORKING)"
        />

        <v-switch
          v-model="alias.encryptPassword"
          label="Encrypt password (NOT WORKING)"
        />
      </v-form>

      <v-btn
        v-if="creating"
        :disabled="!valid"
        color="success"
        visible="false"
        @click="createAlias"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-content-save theme--dark"
        />
        Create
      </v-btn>

      <v-btn
        v-if="!creating"
        :disabled="!valid"
        color="success"
        @click="saveAlias"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-content-save theme--dark"
        />
        Save
      </v-btn>
    </base-material-card>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'Alias',

    props: {
      identifier: {
        type: String,
        default: '',
      },
      origIdentifier: {
        type: String,
        default: '',
      },
    },

    data () {
      return {
        alias: {},
        enableMock: process.env.VUE_APP_MOCK === 'true',
        valid: true,
        editEnabled: false,
        creating: true,
        drivers: [],
      }
    },

    computed: {
    },

    created: function () {
      this.loadDrivers()
      if (this.$route.params.identifier || this.$route.params.origIdentifier) {
        this.loadAlias(this.$route.params.identifier || this.$route.params.origIdentifier, this.$route.params.origIdentifier)
      }
    },

    methods: {
      loadDrivers: function () {
        var url = this.enableMock ? process.env.BASE_URL + 'mock/Drivers.json' : process.env.BASE_URL + 'ws/Drivers'
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
      loadAlias: function (identifier, boolClone) {
        this.editEnabled = false
        this.alias = {}
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleAlias.json' : process.env.BASE_URL + 'ws/Aliases(' + identifier + ')'
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.alias = response.value
            if (boolClone) {
              that.alias.identifier = null
              that.creating = true
            } else {
              that.creating = false
            }
            if (this.alias.driverPropertiesClone) {
              // what the xxx is this?
              this.alias.driverPropertiesClone = undefined
            }
            that.editEnabled = true
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
      validate: function () {
        this.$refs.form.validate()
      },
      createAlias: function () {
        console.log('create')
        this.validate()
        this.editEnabled = false
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleAlias.json' : process.env.BASE_URL + 'ws/Aliases'
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'POST',
          url: url,
          contentType: 'application/json',
          data: JSON.stringify(this.alias),
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            that.alias = data.value
            that.editEnabled = true
            that.creating = false
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
      saveAlias: function () {
        console.log('save')
        this.validate()
        this.editEnabled = false
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleAlias.json' : process.env.BASE_URL + `ws/Aliases(${this.alias.identifier})`
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'PUT',
          url: url,
          contentType: 'application/json',
          data: JSON.stringify(this.alias),
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            that.alias = data.value
            that.editEnabled = true
            that.creating = false
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
      onChangeDriver: function () {
        if (this.editEnabled) {
          // editEnabled ensures that the user changed this, and not some other event
          this.alias.url = this.alias.driver.url
        }
      },
    },
  }
</script>
