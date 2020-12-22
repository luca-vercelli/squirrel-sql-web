<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-pencil"
      title="Driver"
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
          :disabled="!editEnabled"
        />

        <v-text-field
          v-model="driver.url"
          label="Example URL"
          required
          :disabled="!editEnabled"
        />

        <v-text-field
          v-model="driver.webSiteUrl"
          label="Website URL"
          class="md-4"
          :disabled="!editEnabled"
        />

        <v-text-field
          v-model="driver.driverClassName"
          label="Driver class name"
          required
          :disabled="!editEnabled"
        />
      </v-form>

      <v-btn
        v-if="creating"
        :disabled="!valid || !editEnabled"
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
        :disabled="!valid || !editEnabled"
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
        enableMock: process.env.VUE_APP_MOCK === 'true',
        valid: true,
        editEnabled: false,
        creating: true,
      }
    },

    computed: {
      wsUrl: function () {
        return this.enableMock ? process.env.BASE_URL + 'mock/SingleDriver.json' : process.env.BASE_URL + 'ws/Drivers(' + this.identifier + ')'
      },
    },

    created: function () {
      if (this.$route.params.identifier || this.$route.params.origIdentifier) {
        this.loadDriver(this.$route.params.identifier || this.$route.params.origIdentifier, this.$route.params.origIdentifier)
      } else {
        this.editEnabled = true
      }
    },

    methods: {
      loadDriver: function (identifier, boolClone) {
        this.editEnabled = false
        this.driver = {}
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleDriver.json' : process.env.BASE_URL + 'ws/Drivers(' + identifier + ')'
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
              that.driver.identifier = null
              that.creating = true
            } else {
              that.creating = false
            }
            that.editEnabled = true
          },
          error: function (response) {
            that.$emit('ajax-error', response)
          },
        })
      },
      validate: function () {
        this.$refs.form.validate()
      },
      createDriver: function () {
        this.validate()
        this.editEnabled = false
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleDriver.json' : process.env.BASE_URL + 'ws/Drivers'
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
            that.$emit('notify', { message: 'Success.', type: 'success' })
            that.driver = data.value
            that.editEnabled = true
            that.creating = false
          },
          error: function (response) {
            that.$emit('ajax-error', response)
          },
        })
      },
      saveDriver: function () {
        this.validate()
        this.editEnabled = false
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleDriver.json' : process.env.BASE_URL + `ws/Drivers(${this.driver.identifier})`
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
            that.$emit('notify', { message: 'Success.', type: 'success' })
            that.driver = data.value
            that.editEnabled = true
            that.creating = false
          },
          error: function (response) {
            that.$emit('ajax-error', response)
          },
        })
      },
      back: function () {
        this.$router.push('/drivers')
      },
    },
  }
</script>
