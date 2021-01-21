<template>
  <v-container
    id="table"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-table"
      :title="$t('Table.title', [tableName])"
      class="px-5 py-3"
      :closeable="true"
      @close-card="$emit('close-tab')"
    >
      <v-row>
        <v-col>
          <v-menu offset-y>
            <template v-slot:activator="{ on, attrs }">
              <v-btn
                color="primary"
                dark
                v-bind="attrs"
                v-on="on"
              >
                {{ $t('Scripts.title') }}
              </v-btn>
            </template>
            <v-list>
              <v-list-item
                v-for="(item, index) in scriptMenuVoices"
                :key="index"
              >
                <v-list-item-title
                  @click="loadScript(item.endpoint)"
                >
                  {{ $t(item.title) }}
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </v-col>
      </v-row>
      <template>
        <v-tabs
          v-model="currentTab"
          center-active
        >
          <v-tab
            v-for="tab in tabs"
            :key="tab.endpoint"
            @click="loadDetails(tab)"
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
          { caption: 'ContentsTab.title', endpoint: 'Content', pagination: true },
          { caption: 'ColumnsTab.title', endpoint: 'Columns' },
          { caption: 'RowCountTab.title', endpoint: 'RowCount' },
          { caption: 'PrimaryKeyTab.title', endpoint: 'Pk' },
          { caption: 'IndexesTab.title', endpoint: 'Indexes' },
          { caption: 'ColumnPriviligesTab.title', endpoint: 'Privileges' },
          { caption: 'ImportedKeysTab.title', endpoint: 'ImportedFk' },
          { caption: 'ExportedKeysTab.title', endpoint: 'ExportedFk' },
          { caption: 'RowIDTab.title', endpoint: 'RowId' },
          { caption: 'VersionColumnsTab.title', endpoint: 'VersionColumns' },
        ],
        scriptMenuVoices: [
          { title: 'Scripts.CreateTable', endpoint: 'Ddl' },
          { title: 'Scripts.Select', endpoint: 'ScriptSelect' },
          { title: 'Scripts.Insert', endpoint: 'ScriptInsert' },
          { title: 'Scripts.Update', endpoint: 'ScriptUpdate' },
          { title: 'Scripts.Delete', endpoint: 'ScriptDelete' },
        ],
        currentTab: 1,
      }
    },

    computed: {
      tableName: function () {
        return this.node.simpleName
      },
      tableEndpoint: function () {
        const catalog = this.node.catalog || ''
        const schema = this.node.schemaName || ''
        const name = this.node.simpleName || ''
        const type = this.node.objectType || ''
        return `ws/Session(${this.sessionIdentifier})/Table(${catalog},${schema},${name},${type})/`
      },
    },

    created: function () {
      this.loadDetails(this.tabs[this.currentTab])
    },

    methods: {
      loadDetails: function (tab) {
        this.editEnabled = false
        this.results = null
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/DataSet.json' : this.tableEndpoint + tab.endpoint,
          type: 'GET',
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
      loadScript: function (endpoint) {
        this.editEnabled = false
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/TableDdl.json' : this.tableEndpoint + endpoint,
          type: 'GET',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data) {
            that.$emit('sql-script', data.value)
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
