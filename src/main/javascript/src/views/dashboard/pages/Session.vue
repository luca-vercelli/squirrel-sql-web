<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-clipboard-text"
      title="Session"
      class="px-5 py-3"
    >
      <v-textarea
        v-model="query"
        filled
        auto-grow
        label="SQL Query"
        placeholder="Type your query here"
        required
      />

      <v-btn
        :disabled="!query"
        color="success"
        visible="false"
        @click="executeQuery"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-edit theme--dark"
        />
        Query
      </v-btn>
    </base-material-card>
    <sql-results v-if="results" />
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'Session',

    components: {
      SqlResults: () => import('./SqlResults'),
    },

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
        query: '',
        results: null,
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
      executeQuery: function () {
        this.editEnabled = false
        this.results = null
        // TODO hideMessages();
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/ExecuteQuery.json' : '../ws/ExecuteQuery',
          type: this.enableMock ? 'GET' : 'POST',
          data: {
            sessionId: this.session.identifier,
            query: this.query,
          },
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data) {
            if (data.value == null) {
              // not a SELECT
              // TODO showMessage("Success.", "success");
            } else {
              that.results = data.value
            }
            that.editEnabled = true
          },
          error: function (response, status) {
            // TODO showAjaxError(response)
            console.log(response)
            that.editEnabled = true
          },
        })
      },
      /**
      * @see https://stackoverflow.com/a/18197341/5116356
      */
      download: function (filename, text) {
        var element = document.createElement('a')
        element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text))
        element.setAttribute('download', filename)
        element.style.display = 'none'
        document.body.appendChild(element)
        element.click()
        document.body.removeChild(element)
      },
      downloadSql: function () {
        this.download('Query.sql', this.query)
      },
    },
  }
</script>
