<template>
  <v-container
    id="schema-info"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-database"
      :title="$t('MetaDataTab.title')"
      class="px-5 py-3"
      :closeable="true"
      @close-card="$emit('close-tab')"
    >
      <template>
        <v-tabs
          v-model="currentTab"
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
    name: 'SchemaInfoTab',

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
          { caption: 'MetaDataTab.title', endpoint: 'MetaData' },
          { caption: 'ConnectionStatusTab.title', endpoint: 'ConnectionStatus' },
          { caption: 'CatalogsTab.title', endpoint: 'Catalogs' },
          { caption: 'SchemasTab.title', endpoint: 'Schemas' },
          { caption: 'TableTypesTab.title', endpoint: 'TableTypes' },
          { caption: 'DataTypesTab.title', endpoint: 'DataTypes' },
          { caption: 'NumericFunctionsTab.title', endpoint: 'NumericFunctions' },
          { caption: 'StringFunctionsTab.title', endpoint: 'StringFunctions' },
          { caption: 'SystemFunctionsTab.title', endpoint: 'SystemFunctions' },
          { caption: 'TimeDateFunctionsTab.title', endpoint: 'TimeDateFunctions' },
          { caption: 'KeywordsTab.title', endpoint: 'Keywords' },
        ],
        currentTab: 0,
      }
    },

    computed: {
      dbEndpoint: function () {
        const catalog = this.node.catalog || ''
        const schema = this.node.schemaName || ''
        const name = this.node.simpleName || ''
        const type = this.node.objectType || ''
        return `ws/Session(${this.sessionIdentifier})/Database(${catalog},${schema},${name},${type})/`
      },
    },

    created: function () {
      this.loadDetails(this.tabs[this.currentTab].endpoint)
    },

    methods: {
      loadDetails: function (endpoint) {
        this.editEnabled = false
        this.results = null
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/DataSet.json' : this.dbEndpoint + endpoint,
          type: 'GET',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data) {
            console.log(data)
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
