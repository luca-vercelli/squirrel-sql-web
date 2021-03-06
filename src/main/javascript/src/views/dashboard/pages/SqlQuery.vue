<template>
  <v-container
    id="sql-tab"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-database-search"
      :title="$t('SQLTab.title')"
      class="px-5 py-3"
      :closeable="true"
      @close-card="$emit('close-tab')"
    >
      <v-textarea
        v-model="query"
        filled
        auto-grow
        label="SQL Query"
        :placeholder="$t('SQLTab.typeyourquery')"
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
          {{ $t('SQLTab.action.query') }}
        </v-btn>

        <v-select
          v-model="historySelected"
          :items="history"
          item-text="sql"
          item-value="sql"
          :label="$t('SessionSQLPropertiesPanel.sqlhistory')"
          :disabled="!editEnabled"
          @change="selectFromHistory"
        />

        <v-checkbox
          v-model="session.properties.sqllimitRows"
          :label="$t('SQLPanel.limitrowscheckbox.label')"
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
      :pagination="true"
      :no-more-items="noMoreItems"
      @load-more="loadMore()"
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
      defaultQuery: {
        type: String,
        default: null,
      },
    },

    data () {
      return {
        enableMock: process.env.VUE_APP_MOCK === 'true',
        editEnabled: true,
        query: '',
        results: null,
        historySelected: null,
        history: [],
        noMoreItems: false,
      }
    },

    computed: {
    },

    created: function () {
      if (this.defaultQuery) {
        this.query = this.defaultQuery
      }
    },

    beforeMount: function () {
      this.loadHistory()
    },

    methods: {
      loadHistory: function () {
        var url = this.enableMock ? process.env.BASE_URL + 'mock/History.json' : process.env.BASE_URL + `ws/Session(${this.session.identifier})/History`
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.history = response.data
          },
          error: function (response, status) {
            that.editEnabled = true
            that.$emit('ajax-error', response)
          },
        })
      },
      executeQuery: function () {
        this.results = null
        this.loadMore()
      },
      loadMore: function () {
        this.editEnabled = false
        var url = this.enableMock ? process.env.BASE_URL + 'mock/DataSet.json' : process.env.BASE_URL + 'ws/ExecuteQuery'

        if (this.session.properties.sqllimitRows) {
          var skip = (this.results && this.results.allDataForReadOnly) ? this.results.allDataForReadOnly.length : 0
          url += `?$skip=${skip}&$top=${this.session.properties.sqlnbrRowsToShow}`
        } else {
          this.noMoreItems = true
        }
        var that = this
        $.ajax({
          url: url,
          type: this.enableMock ? 'GET' : 'POST',
          data: {
            sessionId: this.session.identifier,
            query: this.query,
          },
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data) {
            that.history.push({ sql: that.query })
            if (data.value == null) {
              // not a SELECT
              that.$emit('notify', { message: 'Success.', type: 'success' })
            } else {
              if (that.session.properties.sqllimitRows) {
                that.noMoreItems = that.isShort(data.value)
              }
              if (that.results === null) {
                that.results = data.value
              } else {
                that.mergeDataSet(that.results, data.value)
              }
            }
            that.editEnabled = true
          },
          error: function (response, status) {
            that.editEnabled = true
            that.$emit('ajax-error', response)
          },
        })
      },
      /**
      Add data from dataSet2 into dataSet1
      */
      mergeDataSet (dataSet1, dataSet2) {
        dataSet1.allDataForReadOnly = dataSet1.allDataForReadOnly.concat(dataSet2.allDataForReadOnly)
      },
      /**
      check if dataSet is not as long es expected
      */
      isShort (dataSet) {
        return !dataSet || !dataSet.allDataForReadOnly || dataSet.allDataForReadOnly.length < this.session.properties.sqlnbrRowsToShow
      },
      saveProperties: function () {
        this.editEnabled = false
        this.results = null
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/SessionProperties.json' : process.env.BASE_URL + `ws/Session(${this.session.identifier})/Properties`,
          type: this.enableMock ? 'GET' : 'PUT',
          contentType: 'application/json',
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
      selectFromHistory: function () {
        if (this.historySelected) {
          this.query = this.historySelected
        }
      },
    },
  }
</script>
