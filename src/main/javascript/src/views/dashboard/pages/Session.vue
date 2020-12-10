<template>
  <v-container />
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'Session',

    props: {
      identifier: {
        type: String,
        default: '',
      },
    },

    data () {
      return {
        session: {},
        enableMock: true,
        editEnabled: false,
      }
    },

    computed: {},

    created: function () {
      this.loadSession(this.$route.params.identifier)
    },

    methods: {
      loadSession: function (identifier) {
        this.editEnabled = false
        this.session = {}
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleSession.json' : '../ws/Session(' + identifier + ')'
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            console.log('response:', response)
            that.session = response.value
            that.editEnabled = true
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
      disconnect: function () {
        // TODO should give some warning
        $.ajax({
          type: this.enableMock ? 'GET' : 'POST',
          url: this.enableMock ? process.env.BASE_URL + 'mock/JustGetOk' : '../ws/Disconnect',
          data: {
            sessionId: this.session.identifier,
          },
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            window.location.replace('..')
          },
          error: function (response, status) {
            console.log(response)
            // TODO showAjaxError(response)
            // TODO disableEdit(false)
          },
        })
      },
      getCatalogs: function () {
        $.ajax({
          type: 'GET',
          url: this.enableMock ? process.env.BASE_URL + 'mock/SchemaInfo.json' : `../ws/Session(${this.session.identifier})/SchemaInfo`,
          data: {
            sessionId: this.session.identifier,
          },
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            var schemaInfo = data.value
            console.log(schemaInfo)
            // ASSOCIATION CATALOGS/SCHEMAS???
          },
          error: function (response, status) {
            console.log(response)
            // TODO showAjaxError(response);
          },
        })
      },
    },
  }
</script>
