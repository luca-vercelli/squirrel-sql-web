<template>
  <v-container
    id="procedure"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-table"
      :title="$t('Procedure.title', [procName])"
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
    name: 'ProcedureTab',

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
          { caption: 'ProcedureColumnsTab.title', endpoint: 'Columns', results: null },
        ],
        scriptMenuVoices: [
          { title: 'Scripts.CreateProcedure', endpoint: 'CreateProcedure' },
          { title: 'Scripts.RunProcedure', endpoint: 'RunProcedure' },
        ],
        currentTab: 0,
      }
    },

    computed: {
      procName: function () {
        return this.node.simpleName
      },
      procEndpoint: function () {
        // Remember, procedureType is not objectType
        const catalog = this.node.catalog || ''
        const schema = this.node.schemaName || ''
        const name = this.node.simpleName || ''
        const type = this.node.objectType || ''
        const procType = this.node.procedureType || ''
        return `ws/Session(${this.sessionIdentifier})/Procedure(${catalog},${schema},${name},${type},${procType})/`
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
      loadScript: function (endpoint) {
        this.editEnabled = false
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/TableDdl.json' : this.procEndpoint + endpoint,
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
