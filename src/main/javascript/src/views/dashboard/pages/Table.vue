<template>
  <v-container
    id="table"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-clipboard-text"
      :title="'Table ' + tableName"
      class="px-5 py-3"
    >
      <template>
        <v-btn
          v-for="button in buttons"
          :key="button.endpoint"
          color="success"
          @click="loadDetails"
        >
          <i
            aria-hidden="true"
            class="v-icon notranslate mdi mdi-edit theme--dark"
          />
          {{ button.caption }}
        </v-btn>
        <sql-results
          v-if="results"
          :results="results"
        />
      </template>
    </base-material-card>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'TableTab',

    props: {
      sessionIdentifier: {
        type: String,
        default: '',
      },
      tableNode: {
        type: Object,
        default: Object,
      },
    },

    data () {
      return {
        enableMock: process.env.VUE_APP_MOCK === 'true',
        editEnabled: false,
        results: null,
        buttons: [
          { caption: 'Table content', endpoint: 'TableContent' },
          { caption: 'Columns', endpoint: 'TableColumns' },
          { caption: 'Row count', endpoint: 'TableRowCount' },
          { caption: 'Primary key', endpoint: 'TablePk' },
          { caption: 'Indexes', endpoint: 'TableIndexes' },
          { caption: 'Privileges', endpoint: 'TablePrivileges' },
          { caption: 'Imported keys', endpoint: 'TableImportedFk' },
          { caption: 'Exported keys', endpoint: 'TableExportedFk' },
          { caption: 'Table Row ID', endpoint: 'TableRowId' },
          { caption: 'Version columns', endpoint: 'TableVersionColumns' },
        ],
      }
    },

    computed: {},

    created: function () {
    },

    methods: {
      loadDetails: function () {
        this.editEnabled = false
        this.results = null
        // TODO hideMessages();
        var endpoint = null // TODO
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/ExecuteQuery.json' : `../ws/Session(${this.sessionIdentifier})/${endpoint}`,
          type: 'GET',
          data: {
            catalog: this.tableNode.catalog,
            schema: this.tableNode.schema,
            tableName: this.tableNode.simpleName,
            tableType: this.tableNode.objectType,
          },
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data) {
            that.results = data.value
            that.editEnabled = true
          },
          error: function (response, status) {
            // TODO showAjaxError(response)
            console.log(response)
            that.editEnabled = true
          },
        })
      },
    },
  }
</script>
