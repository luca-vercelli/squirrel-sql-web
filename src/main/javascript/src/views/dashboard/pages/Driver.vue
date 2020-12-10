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
      <v-form
        ref="form"
        v-model="valid"
        lazy-validation
      >
        <v-text-field
          v-model="driver.name"
          label="Name"
          required
        />

        <v-text-field
          v-model="driver.url"
          label="Example URL"
          required
        />

        <v-text-field
          v-model="driver.webSiteUrl"
          label="Website URL"
          class="md-4"
          required
        />

        <v-text-field
          v-model="driver.driverClassName"
          label="Driver class name"
          required
        />
      </v-form>

      <v-btn
        v-if="creating"
        :disabled="!valid"
        color="success"
        visible="false"
        @click="createDriver"
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
        @click="saveDriver"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-content-save theme--dark"
        />
        Save
      </v-btn>
      <v-btn
        class="mr-4"
        :disabled="driver.webSiteUrl == ''"
        target="_blank"
        :href="driver.webSiteUrl"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-call-made theme--dark"
        />
        Visit website
      </v-btn>
      <v-btn
        class="mr-4"
        :disabled="driver.driverClassName == ''"
        target="_blank"
        @click="checkDriverClasses"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-android theme--dark"
        />
        Load drivers
      </v-btn>
    </base-material-card>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'Driver',

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
        driver: {},
        enableMock: true,
        valid: true,
        editEnabled: false,
        creating: true,
      }
    },

    computed: {
      wsUrl: function () {
        return this.enableMock ? process.env.BASE_URL + 'mock/SingleDriver.json' : '../ws/Drivers(' + this.identifier + ')'
      },
    },

    created: function () {
      if (this.$route.params.identifier || this.$route.params.origIdentifier) {
        this.loadDriver(this.$route.params.identifier || this.$route.params.origIdentifier, this.$route.params.origIdentifier)
      }
    },

    methods: {
      loadDriver: function (identifier, boolClone) {
        this.editEnabled = false
        this.driver = {}
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleDriver.json' : '../ws/Drivers(' + identifier + ')'
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            console.log('response:', response)
            that.driver = response.value
            if (boolClone) {
              that.driver.identifier = {}
              that.creating = true
            } else {
              that.creating = false
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
      createDriver: function () {
        console.log('create')
        this.validate()
        this.editEnabled = false
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleDriver.json' : '../ws/Drivers'
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'POST',
          url: url,
          contentType: 'application/json',
          data: JSON.stringify(this.driver),
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            that.driver = data.value
            that.editEnabled = true
            that.creating = false
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
      saveDriver: function () {
        console.log('save')
        this.validate()
        this.editEnabled = false
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleDriver.json' : `../ws/Drivers(${this.driver.identifier})`
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'PUT',
          url: url,
          contentType: 'application/json',
          data: JSON.stringify(this.driver),
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            that.driver = data.value
            that.editEnabled = true
            that.creating = false
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
      checkDriverClasses: function () {
        this.editEnabled = false
        var url = this.enableMock ? process.env.BASE_URL + 'mock/JustGetOk' : '../ws/CheckAllDrivers'
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'POST',
          url: url,
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            window.location.reload()
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
    },
  }
</script>
