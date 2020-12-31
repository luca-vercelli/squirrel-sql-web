<template>
  <v-container
    id="table"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-table"
      :title="$t('Table.title', tableName)"
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
          {{ $t('Action.close') }}
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
            {{ $t(tab.caption) }}
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
          { caption: 'ContentsTab.title', endpoint: 'TableContent' },
          { caption: 'ColumnsTab.title', endpoint: 'TableColumns' },
          { caption: 'RowCountTab.title', endpoint: 'TableRowCount' },
          { caption: 'PrimaryKeyTab.title', endpoint: 'TablePk' },
          { caption: 'IndexesTab.title', endpoint: 'TableIndexes' },
          { caption: 'ColumnPriviligesTab.title', endpoint: 'TablePrivileges' },
          { caption: 'ImportedKeysTab.title', endpoint: 'TableImportedFk' },
          { caption: 'ExportedKeysTab.title', endpoint: 'TableExportedFk' },
          { caption: 'RowIDTab.title', endpoint: 'TableRowId' },
          { caption: 'VersionColumnsTab.title', endpoint: 'TableVersionColumns' },
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
