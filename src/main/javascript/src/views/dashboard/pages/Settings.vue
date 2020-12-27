<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-cog"
      title="Global Prefrerences"
      class="px-5 py-3"
    >
      TODO
      <v-checkbox
        v-model="settings.showTooltips"
        label="Show tooltips"
        @change="saveProperties"
      />
      <v-btn
        :disabled="!editEnabled"
        color="success"
        @click="saveSettings"
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
    name: 'SquirrelSettings',

    data () {
      return {
        enableMock: process.env.VUE_APP_MOCK === 'true',
        settings: {},
      }
    },

    created: function () {
      this.loadSettings()
    },

    methods: {
      loadSettings: function (identifier, boolClone) {
        this.editEnabled = false
        this.alias = {}
        var url = this.enableMock ? process.env.BASE_URL + 'mock/Preferences.json' : process.env.BASE_URL + 'ws/Preferences'
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.settings = response.value
            that.editEnabled = true
          },
          error: function (response) {
            that.$emit('ajax-error', response)
            that.editEnabled = true
          },
        })
      },
      saveSettings: function (identifier, boolClone) {
        this.editEnabled = false
        this.alias = {}
        var url = this.enableMock ? process.env.BASE_URL + 'mock/Preferences.json' : process.env.BASE_URL + 'ws/Preferences'
        var that = this
        $.ajax({
          url: url,
          method: this.enableMock ? 'GET' : 'PUT',
          dataType: 'json',
          contentType: 'application/json',
          data: JSON.stringify(this.settings),
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.settings = response.value
            that.editEnabled = true
          },
          error: function (response) {
            that.$emit('ajax-error', response)
            that.editEnabled = true
          },
        })
      },
    },
  }
</script>
