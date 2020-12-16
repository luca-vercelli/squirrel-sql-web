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
    <sql-results
      v-if="results"
      :data-set="results"
    />
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'SqlQuery',

    components: {
      SqlResults: () => import('./SqlResults'),
    },

    props: {
      sessionIdentifier: {
        type: String,
        default: '',
      },
    },

    data () {
      return {
        enableMock: process.env.VUE_APP_MOCK === 'true',
        editEnabled: false,
        query: '',
        results: null,
      }
    },

    computed: {},

    created: function () {
    },

    methods: {
      executeQuery: function () {
        this.editEnabled = false
        this.results = null
        // TODO hideMessages();
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/ExecuteQuery.json' : '../ws/ExecuteQuery',
          type: this.enableMock ? 'GET' : 'POST',
          data: {
            sessionId: this.sessionIdentifier,
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
