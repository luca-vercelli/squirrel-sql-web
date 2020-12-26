<template>
  <v-container
    id="sql-tab"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-database-search"
      title="SQL"
      class="px-5 py-3"
    >
      <v-col class="text-right">
        <v-btn
          color="light"
          @click="$emit('close-tab')"
        >
          <i
            aria-hidden="true"
            class="v-icon notranslate mdi mdi-close-circle theme--dark"
          />
          Close
        </v-btn>
      </v-col>
      <v-textarea
        v-model="query"
        filled
        auto-grow
        label="SQL Query"
        placeholder="Type your query here"
        required
      />

      <template v-slot:actions>
        <v-btn
          :disabled="!query"
          color="success"
          @click="executeQuery"
        >
          <i
            aria-hidden="true"
            class="v-icon notranslate mdi mdi-edit theme--dark"
          />
          Query
        </v-btn>
        <v-checkbox
          v-model="session.properties.sqllimitRows"
          label="Limit rows:"
          @change="saveProperties"
        />
        <v-text-field
          v-model="session.properties.sqlnbrRowsToShow"
          :disabled="!session.properties.sqllimitRows"
          @change="saveProperties"
        />
      </template>
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
      session: {
        type: Object,
        default: Object,
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
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/DataSet.json' : process.env.BASE_URL + 'ws/ExecuteQuery',
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
              that.$emit('notify', { message: 'Success.', type: 'success' })
            } else {
              that.results = data.value
            }
            that.editEnabled = true
          },
          error: function (response, status) {
            that.editEnabled = true
            that.$emit('ajax-error', response)
          },
        })
      },
      saveProperties: function () {
        this.editEnabled = false
        this.results = null
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/JustGetOk' : process.env.BASE_URL + `ws/Session(${this.session.identifier})/Properties`,
          type: this.enableMock ? 'GET' : 'PUT',
          data: JSON.stringify(this.session.properties),
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data) {
            that.editEnabled = true
          },
          error: function (response) {
            that.editEnabled = true
            that.$emit('ajax-error', response)
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
