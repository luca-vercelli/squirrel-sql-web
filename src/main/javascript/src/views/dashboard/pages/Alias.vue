<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-pencil"
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
          :disabled="!editEnabled"
        />

        <v-select
          v-model="alias.driverIdentifier"
          :items="drivers"
          item-text="name"
          item-value="identifier"
          label="Choose driver"
          required
          :disabled="!editEnabled"
          @change="onChangeDriver"
        />

        <v-text-field
          v-model="alias.url"
          label="URL template"
          required
          :disabled="!editEnabled"
        />

        <v-text-field
          v-model="alias.userName"
          label="Username"
          class="md-4"
          required
          :disabled="!editEnabled"
        />

        <v-text-field
          v-model="alias.password"
          label="Password"
          required
          :disabled="!editEnabled"
        />

        <v-switch
          v-model="alias.autoLogon"
          label="Auto logon"
          :disabled="!editEnabled"
        />

        <v-switch
          v-model="alias.connectAtStartup"
          label="Connect at startup (NOT WORKING)"
          :disabled="!editEnabled"
        />

        <v-switch
          v-model="alias.encryptPassword"
          label="Encrypt password (NOT WORKING)"
          :disabled="!editEnabled"
        />
      </v-form>

      <v-btn
        v-if="creating"
        :disabled="!valid || !editEnabled"
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
        :disabled="!valid || !editEnabled"
        color="success"
        @click="saveAlias"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-content-save theme--dark"
        />
        Save
      </v-btn>

      <v-btn
        :disabled="!editEnabled"
        color="secondary"
        @click="back"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-step-backward theme--dark"
        />
        Back
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
      } else {
        this.editEnabled = true
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
            that.removeUnwantedProperties()
            if (boolClone) {
              that.alias.identifier = null
              that.creating = true
            } else {
              that.creating = false
            }
            that.editEnabled = true
          },
          error: function (response) {
            that.$emit('ajax-error', response)
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
            that.$emit('notify', { message: 'Success.', type: 'success' })
            that.alias = data.value
            that.removeUnwantedProperties()
            that.editEnabled = true
            that.creating = false
          },
          error: function (response) {
            that.$emit('ajax-error', response)
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
            that.$emit('notify', { message: 'Success.', type: 'success' })
            that.alias = data.value
            that.removeUnwantedProperties()
            that.editEnabled = true
            that.creating = false
          },
          error: function (response) {
            that.$emit('ajax-error', response)
            that.editEnabled = true
          },
        })
      },
      /**
      When the user changes the driver, we need to change the template URL accordingly
      */
      onChangeDriver: function () {
        if (this.editEnabled) {
          // editEnabled ensures that the user changed this, and not some other event
          var selectedDrivers = this.drivers.filter(x => x.identifier === this.alias.driverIdentifier)
          if (selectedDrivers.length > 0) {
            this.alias.url = selectedDrivers[0].url
          }
        }
      },
      removeUnwantedProperties: function () {
        // FIXME these should be removed server-side
        this.alias.driverPropertiesClone = undefined
        this.alias.valid = undefined
      },
      back: function () {
        this.$router.push('/')
      },
    },
  }
</script>
