<template>
  <v-container
    id="procedure"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-table"
      :title="$t('Procedure.title', procName)"
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
        results: null,
        tabs: [
          { caption: 'ProcedureColumnsTab.title', endpoint: 'ProcedureColumns' },
        ],
      }
    },

    computed: {
      procName: function () {
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
            procedureName: this.node.simpleName,
            procedureType: this.node.procedureType,
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
