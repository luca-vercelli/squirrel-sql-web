<template>
  <v-container
    id="table"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-table"
      :title="'Table ' + tableName"
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
      <template>
        <v-tabs
          center-active
        >
          <v-tab
            v-for="tab in tabs"
            :key="tab.endpoint"
            @click="loadDetails(tab.endpoint)"
          >
            {{ tab.caption }}
          </v-tab>
        </v-tabs>
        <sql-results
          v-if="results"
          :data-set="results"
        />
      </template>
    </base-material-card>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'TableTab',

    components: {
      SqlResults: () => import('./SqlResults'),
    },

    props: {
      sessionIdentifier: {
        type: String,
        default: '',
      },
      node: {
        type: Object,
        default: Object,
      },
    },

    data () {
      return {
        enableMock: process.env.VUE_APP_MOCK === 'true',
        editEnabled: false,
        results: null,
        tabs: [
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

    computed: {
      tableName: function () {
        return this.node.simpleName
      },
    },

    created: function () {
    },

    methods: {
      loadDetails: function (endpoint) {
        this.editEnabled = false
        this.results = null
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/DataSet.json' : process.env.BASE_URL + `ws/Session(${this.sessionIdentifier})/${endpoint}`,
          type: 'GET',
          data: {
            catalog: this.node.catalog,
            schema: this.node.schemaName,
            tableName: this.node.simpleName,
            tableType: this.node.objectType,
          },
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data) {
            that.results = data.value
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
