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
            v-for="(tab, idx) in tabs"
            :key="tab.endpoint"
            @click="activateTab(tab, idx)"
          >
            {{ $t(tab.caption) }}
          </v-tab>
        </v-tabs>
        <sql-results
          v-for="(tab, idx) in tabs"
          :key="idx"
          :visible="currentTab === idx"
          :data-set="tab.results"
          :pagination="tab.pagination"
          :no-more-items="tab.noMoreItems"
          @load-more="loadMore(tab, idx)"
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
        tabs: [
          { caption: 'MetaDataTab.title', endpoint: 'MetaData', results: null },
          { caption: 'ConnectionStatusTab.title', endpoint: 'ConnectionStatus', results: null },
          { caption: 'CatalogsTab.title', endpoint: 'Catalogs', results: null },
          { caption: 'SchemasTab.title', endpoint: 'Schemas', results: null },
          { caption: 'TableTypesTab.title', endpoint: 'TableTypes', results: null },
          { caption: 'DataTypesTab.title', endpoint: 'DataTypes', results: null },
          { caption: 'NumericFunctionsTab.title', endpoint: 'NumericFunctions', results: null },
          { caption: 'StringFunctionsTab.title', endpoint: 'StringFunctions', results: null },
          { caption: 'SystemFunctionsTab.title', endpoint: 'SystemFunctions', results: null },
          { caption: 'TimeDateFunctionsTab.title', endpoint: 'TimeDateFunctions', results: null },
          { caption: 'KeywordsTab.title', endpoint: 'Keywords', results: null },
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
      this.activateTab(this.tabs[this.currentTab], this.currentTab)
    },

    methods: {
      activateTab: function (tab, index) {
        if (tab.results === null) {
          this.loadMore(tab, index)
        }
      },
      loadMore: function (tab, index) {
        console.log('DEBUG: loadMore', tab, index)
        this.editEnabled = false
        var that = this
        var url = this.enableMock ? process.env.BASE_URL + 'mock/DataSet.json' : this.procEndpoint + tab.endpoint
        if (tab.pagination) {
          var skip = (tab.results && tab.results.allDataForReadOnly) ? tab.results.allDataForReadOnly.length : 0
          url += `?$skip=${skip}&$top=${this.rowsPerPage}`
        }
        $.ajax({
          url: url,
          type: 'GET',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data) {
            tab.noMoreItems = that.isShort(data.value)
            if (tab.results === null) {
              tab.results = data.value
            } else {
              that.mergeDataSet(tab.results, data.value)
            }
            that.editEnabled = true
          },
          error: function (response) {
            that.$emit('ajax-error', response)
            that.editEnabled = true
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
        return !dataSet || !dataSet.allDataForReadOnly || dataSet.allDataForReadOnly.length < this.rowsPerPage
      },
    },
  }
</script>
