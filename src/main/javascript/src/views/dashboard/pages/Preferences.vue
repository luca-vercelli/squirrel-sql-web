<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-cog"
      :title="$t('GlobalPreferencesSheet.title')"
      class="px-5 py-3"
    >
      <b>
        <span
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-alert"
        />
        WORK IN PROGRESS
      </b>
      <v-checkbox
        v-model="preferences.showToolTips"
        :label="$t('GeneralPreferencesPanel.showtooltips')"
      />
      <v-btn
        :disabled="!editEnabled"
        color="success"
        @click="savePreferences"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-content-save theme--dark"
        />
        {{ $t('Action.save') }}
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
        preferences: {},
      }
    },

    created: function () {
      this.loadPreferences()
    },

    methods: {
      loadPreferences: function (identifier, boolClone) {
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
            that.preferences = response.value
            that.editEnabled = true
          },
          error: function (response) {
            that.$emit('ajax-error', response)
            that.editEnabled = true
          },
        })
      },
      savePreferences: function (identifier, boolClone) {
        this.editEnabled = false
        this.alias = {}
        var url = this.enableMock ? process.env.BASE_URL + 'mock/Preferences.json' : process.env.BASE_URL + 'ws/Preferences'
        var that = this
        $.ajax({
          url: url,
          method: this.enableMock ? 'GET' : 'PUT',
          dataType: 'json',
          contentType: 'application/json',
          data: this.enableMock ? undefined : JSON.stringify(this.preferences),
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.preferences = response.value
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
