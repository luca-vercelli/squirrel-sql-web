<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-tools"
      :title="$t('Tools.title')"
      class="px-5 py-3"
    >
      <v-btn
        class="mr-4"
        target="_blank"
        @click="checkDriverClasses"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-android theme--dark"
        />
        {{ $t('Tools.scanfordrivers') }}
      </v-btn>
    </base-material-card>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'Tools',

    data () {
      return {
        enableMock: process.env.VUE_APP_MOCK === 'true',
      }
    },

    methods: {
      checkDriverClasses: function () {
        this.editEnabled = false
        var url = this.enableMock ? process.env.BASE_URL + 'mock/JustGetOk' : process.env.BASE_URL + 'ws/CheckAllDrivers'
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'POST',
          url: url,
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            that.$emit('notify', { message: 'Done.', type: 'success' })
          },
          error: function (response) {
            that.$emit('ajax-error', response)
          },
        })
      },
    },
  }
</script>
