<template>
  <v-card>
    <v-tabs
      v-model="tab"
      center-active
    >
      <v-tab
        v-for="item in items"
        :key="item.tab"
      >
        {{ item.tab }}
      </v-tab>
    </v-tabs>

    <v-tabs-items v-model="tab">
      <v-tab-item
        v-for="item in items"
        :key="item.tab"
      >
        <v-card flat>
          <objects-tree
            v-if="item.type=='objects'"
            :session-identifier="session.identifier"
            @open-table="addTable(event)"
          />
          <sql-query
            v-if="item.type=='query'"
            :session-identifier="session.identifier"
          />
          <table-tab
            v-if="item.type=='table'"
            :session-identifier="session.identifier"
          />
        </v-card>
      </v-tab-item>
    </v-tabs-items>
  </v-card>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'Session',

    components: {
      SqlQuery: () => import('./SqlQuery'),
      ObjectsTree: () => import('./ObjectsTree'),
      TableTab: () => import('./Table'),
    },

    props: {
      identifier: {
        type: String,
        default: '',
      },
    },

    data () {
      return {
        session: {},
        enableMock: true,
        editEnabled: false,
        tab: null,
        items: [
          { tab: 'Objects tree', type: 'objects' },
          { tab: 'SQL Query', type: 'query' },
          { tab: 'GOOFY', type: 'table' },
          { tab: 'MICKEY', type: 'table' },
        ],
      }
    },

    computed: {},

    created: function () {
      this.loadSession(this.$route.params.identifier)
    },

    methods: {
      loadSession: function (identifier) {
        this.editEnabled = false
        this.session = {}
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleSession.json' : '../ws/Session(' + identifier + ')'
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            console.log('response:', response)
            that.session = response.value
            that.editEnabled = true
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
      disconnect: function () {
        // TODO should give some warning
        $.ajax({
          type: this.enableMock ? 'GET' : 'POST',
          url: this.enableMock ? process.env.BASE_URL + 'mock/JustGetOk' : '../ws/Disconnect',
          data: {
            sessionId: this.session.identifier,
          },
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            window.location.replace('..')
          },
          error: function (response, status) {
            console.log(response)
            // TODO showAjaxError(response)
            // TODO disableEdit(false)
          },
        })
      },
      getCatalogs: function () {
        $.ajax({
          type: 'GET',
          url: this.enableMock ? process.env.BASE_URL + 'mock/SchemaInfo.json' : `../ws/Session(${this.session.identifier})/SchemaInfo`,
          data: {
            sessionId: this.session.identifier,
          },
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            var schemaInfo = data.value
            console.log(schemaInfo)
            // ASSOCIATION CATALOGS/SCHEMAS???
          },
          error: function (response, status) {
            console.log(response)
            // TODO showAjaxError(response);
          },
        })
      },
      addTableTab: function (tableName) {
        console.log('event:', tableName)
        this.items.push({ tab: tableName, type: 'table' })
      },
    },
  }
</script>
